package dev.nickrobson.minecraft.telegrambridge.core.telegram.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.nickrobson.minecraft.telegrambridge.core.config.TelegramBridgeConfiguration;
import dev.nickrobson.minecraft.telegrambridge.core.config.TelegramConfiguration;
import dev.nickrobson.minecraft.telegrambridge.core.telegram.client.model.TelegramResponse;
import dev.nickrobson.minecraft.telegrambridge.core.telegram.client.model.Update;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class TelegramClient {
    private static final Logger logger = LogManager.getLogger(TelegramClient.class);
    private static final int LONG_POLLING_TIMEOUT_SECONDS = 30;
    private static final Type updateListType = new TypeToken<TelegramResponse<List<Update>>>(){}.getType();

    private final ScheduledExecutorService updatePollingExecutor;
    private final ScheduledExecutorService requestMakingExecutor;
    private final Gson gson;

    private final AtomicReference<TelegramUpdateListener> updatesListener = new AtomicReference<>(null);
    private final AtomicBoolean hasSentShutdown = new AtomicBoolean(false);
    private final AtomicBoolean isUpdatesPolling = new AtomicBoolean(false);
    private final AtomicLong currentOffset = new AtomicLong(-1L);

    public TelegramClient() {
        this.updatePollingExecutor = Executors.newSingleThreadScheduledExecutor();
        this.requestMakingExecutor = Executors.newSingleThreadScheduledExecutor();
        this.gson = new GsonBuilder().create();
    }

    public void startUpdatesPolling() {
        if (isUpdatesPolling.getAndSet(true)) {
            return;
        }
        scheduleUpdatePolling();
    }

    public void stopUpdatesPolling() {
        this.isUpdatesPolling.set(false);
        this.updatesListener.set(null);
    }

    public void setUpdatesListener(TelegramUpdateListener updatesListener) {
        this.updatesListener.set(updatesListener);
    }

    public void scheduleUpdatePolling() {
        updatePollingExecutor.execute(() -> {
            try {
                List<Update> updates = getUpdatesOnCurrentThread();
                try {
                    TelegramUpdateListener updateListener = updatesListener.get();
                    if (updateListener != null) {
                        updateListener.processUpdates(updates);
                    }
                } catch (Exception ex) {
                    logger.error("Failed to handle updates", ex);
                }
                for (Update update : updates) {
                    if (update.updateId > currentOffset.get()) {
                        this.currentOffset.set(update.updateId);
                    }
                }
            } catch (InterruptedException ex) {
                isUpdatesPolling.set(false);
            } catch (Exception ex) {
                logger.error("Failed to get updates", ex);
            } finally {
                if (isUpdatesPolling.get()) {
                    scheduleUpdatePolling();
                }
            }
        });
    }

    public List<Update> getUpdatesOnCurrentThread() throws IOException, InterruptedException {
        List<AbstractMap.SimpleEntry<String, Object>> params = new ArrayList<>();
        params.add(new AbstractMap.SimpleEntry<>("allowed_updates", "[\"message\"]"));
        if (currentOffset.get() > 0) {
            // Telegram will by default give us the updates we haven't seen,
            // So we only set the offset when we want to see the next updates.
            params.add(new AbstractMap.SimpleEntry<>("offset", currentOffset.get() + 1));
        }
        params.add(new AbstractMap.SimpleEntry<>("timeout", LONG_POLLING_TIMEOUT_SECONDS));

        TelegramResponse<List<Update>> response = gson.fromJson(get("getUpdates", params), updateListType);
        return response.result;
    }

    public void sendMessage(String message) {
        requestMakingExecutor.execute(() -> this.sendMessageOnCurrentThread(message));
    }

    private void sendMessageOnCurrentThread(String message) {
        TelegramConfiguration configuration = TelegramBridgeConfiguration.getConfiguration().telegram();
        String apiToken = configuration.apiToken();
        int chatId = configuration.chatId();

        if (apiToken == null || apiToken.isBlank()) {
            logger.warn("Skipping sending message as Telegram bot API token is unset");
            return;
        }

        if (chatId == 0) {
            logger.warn("Skipping sending message as Telegram chat ID is unset");
            return;
        }

        String htmlEscapedMessage = StringEscapeUtils.escapeHtml4(message);
        try {
            List<AbstractMap.SimpleEntry<String, Object>> params = new ArrayList<>();
            params.add(new AbstractMap.SimpleEntry<>("parse_mode", "HTML"));
            params.add(new AbstractMap.SimpleEntry<>("chat_id", chatId));
            params.add(new AbstractMap.SimpleEntry<>("text", htmlEscapedMessage));

            post("sendMessage", params);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Failed to send message to Telegram: (message: {}; HTML escaped message: {})", message, htmlEscapedMessage);
        }
    }

    public void flagServerShutdown() {
        if (hasSentShutdown.compareAndSet(false, true)) {
            TelegramBridgeConfiguration configuration = TelegramBridgeConfiguration.getConfiguration();
            if (!configuration.messages().isShutdownMessageEnabled()) {
                return;
            }

            String message = configuration.messages().shutdownMessage();
            this.sendMessageOnCurrentThread(message);

            this.isUpdatesPolling.set(false);
            this.updatePollingExecutor.shutdownNow();
            this.requestMakingExecutor.shutdownNow();
        }
    }

    private static URL getUrl(String method, List<AbstractMap.SimpleEntry<String, Object>> params) throws MalformedURLException {
        // https://api.telegram.org/bot<token>/METHOD_NAME
        String apiToken = TelegramBridgeConfiguration.getConfiguration().telegram().apiToken();
        if (apiToken == null || apiToken.isBlank()) {
            throw new IllegalStateException("API Token is invalid");
        }

        String url = "https://api.telegram.org/bot" + apiToken + "/" + method;
        if (params != null) {
            url += "?" + getQueryString(params);
        }
        return new URL(url);
    }

    private static String get(String method, List<AbstractMap.SimpleEntry<String, Object>> params) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) getUrl(method, params).openConnection();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = br.readLine()) != null) {
                response.append(line).append('\r');
            }
            connection.disconnect();
            return response.toString();
        }
    }

    private static String post(String method, List<AbstractMap.SimpleEntry<String, Object>> params) throws IOException {
        HttpURLConnection connection = null;
        try {
            byte[] postDataBytes = getQueryString(params).getBytes(StandardCharsets.UTF_8);

            connection = (HttpURLConnection) getUrl(method, null).openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", StandardCharsets.UTF_8.toString());
            connection.setRequestProperty("Content-Length", Integer.toString(postDataBytes.length));

            try (OutputStream os = connection.getOutputStream()) {
                os.write(postDataBytes);
                os.flush();
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    response.append(line).append('\r');
                }

                if (connection.getResponseCode() != 200) {
                    logger.error("Error calling Telegram endpoint! " + connection.getResponseCode() + "\nResponse: " + response);
                }

                return response.toString();
            }
        } catch (Exception ex) {
            logger.error("Failed to make request to Telegram", ex);
            throw ex;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String getQueryString(List<AbstractMap.SimpleEntry<String, Object>> params) {
        List<String> queryParams = new ArrayList<>();

        for (AbstractMap.SimpleEntry<String, Object> param : params) {
            queryParams.add(
                    URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8) +
                            "=" +
                            URLEncoder.encode(param.getValue().toString(), StandardCharsets.UTF_8)
            );
        }

        return String.join("&", queryParams);
    }
}
