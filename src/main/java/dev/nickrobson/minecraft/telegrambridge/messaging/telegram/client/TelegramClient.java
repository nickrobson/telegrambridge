package dev.nickrobson.minecraft.telegrambridge.messaging.telegram.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.nickrobson.minecraft.telegrambridge.config.TelegramBridgeConfig;
import dev.nickrobson.minecraft.telegrambridge.config.TelegramConfig;
import dev.nickrobson.minecraft.telegrambridge.messaging.telegram.client.model.TelegramResponse;
import dev.nickrobson.minecraft.telegrambridge.messaging.telegram.client.model.Update;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.MessageFormatMessage;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class TelegramClient {
    private static final Logger logger = LogManager.getLogger(TelegramClient.class);
    private static final int LONG_POLLING_TIMEOUT_SECONDS = 30;
    private static final Type updateListType = new TypeToken<TelegramResponse<List<Update>>>(){}.getType();

    private final Gson gson;
    private final Executor requestMakingExecutor;

    private CompletableFuture<Void> updatesFuture;

    private final AtomicReference<TelegramUpdateListener> updatesListener = new AtomicReference<>(null);
    private final AtomicBoolean hasSentShutdown = new AtomicBoolean(false);
    private final AtomicBoolean isUpdatesPolling = new AtomicBoolean(false);
    private final AtomicLong currentOffset = new AtomicLong(-1L);

    public TelegramClient() {
        this.gson = new GsonBuilder().create();

        this.requestMakingExecutor = Executors.newSingleThreadExecutor(runnable -> {
            Thread t = new Thread(runnable, "TelegramBridge Telegram Thread");
            t.setDaemon(true); // allow the server to shut down when this is still running
            return t;
        });
    }

    private HttpClient createHttpClient() {
        return HttpClient.newBuilder()
                .executor(requestMakingExecutor)
                .build();
    }

    public void startUpdatesPolling() {
        if (isUpdatesPolling.getAndSet(true)) {
            return;
        }
        pollForUpdatesLoop();
    }

    public void stopUpdatesPolling() {
        this.isUpdatesPolling.set(false);
        this.updatesListener.set(null);
        if (this.updatesFuture != null) {
            this.updatesFuture.cancel(true);
        }
    }

    public void setUpdatesListener(TelegramUpdateListener updatesListener) {
        this.updatesListener.set(updatesListener);
    }

    public void pollForUpdatesLoop() {
        this.updatesFuture = getUpdates()
                .handle((updates, exception) -> {
                    if (exception != null) {
                        logger.error("Failed to get updates", exception);
                        return null;
                    }

                    try {
                        TelegramUpdateListener updateListener = updatesListener.get();
                        if (updateListener != null) {
                            updateListener.processUpdates(updates);
                        }
                    } catch (Exception ex) {
                        logger.error("Failed to handle updates", ex);
                    }

                    try {
                        for (Update update : updates) {
                            if (update.updateId > currentOffset.get()) {
                                this.currentOffset.set(update.updateId);
                            }
                        }
                    } catch (Exception ex) {
                        logger.error("Failed to set update offset", ex);
                    }

                    if (isUpdatesPolling.get()) {
                        pollForUpdatesLoop();
                    }
                    return null;
                });
    }

    public CompletableFuture<List<Update>> getUpdates() {
        List<AbstractMap.SimpleEntry<String, Object>> params = new ArrayList<>();
        params.add(new AbstractMap.SimpleEntry<>("allowed_updates", "[\"message\"]"));
        if (currentOffset.get() > 0) {
            // Telegram will by default give us the updates we haven't seen,
            // So we only set the offset when we want to see the next updates.
            params.add(new AbstractMap.SimpleEntry<>("offset", currentOffset.get() + 1));
        }
        params.add(new AbstractMap.SimpleEntry<>("timeout", LONG_POLLING_TIMEOUT_SECONDS));

        return get("getUpdates", params)
                .thenApply(updatesResponse -> {
                    TelegramResponse<List<Update>> response = gson.fromJson(updatesResponse, updateListType);

                    if (!response.ok) {
                        logger.error("Failed to retrieve updates from Telegram ({}): {}", response.errorCode, response.description);
                        return Collections.emptyList();
                    }

                    return response.result;
                });
    }

    public CompletableFuture<Void> sendMessage(String message) {
        TelegramConfig config = TelegramBridgeConfig.getConfig().telegram;
        String apiToken = config.botApiToken;
        long chatId = config.chatId;

        if (apiToken == null || apiToken.isBlank()) {
            logger.warn("Skipping sending message as Telegram bot API token is unset");
            return CompletableFuture.completedFuture(null);
        }

        if (chatId == 0) {
            logger.warn("Skipping sending message as Telegram chat ID is unset");
            return CompletableFuture.completedFuture(null);
        }

        // Telegram's API only supports <, >, &, and ", so supporting more than that is overkill atm.
        // String htmlEscapedMessage = StringEscapeUtils.escapeHtml4(message);
        String htmlEscapedMessage = message
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot");

        try {
            List<AbstractMap.SimpleEntry<String, Object>> params = new ArrayList<>();
            params.add(new AbstractMap.SimpleEntry<>("parse_mode", "HTML"));
            params.add(new AbstractMap.SimpleEntry<>("chat_id", chatId));
            params.add(new AbstractMap.SimpleEntry<>("text", htmlEscapedMessage));

            return post("sendMessage", params)
                    .thenRun(() -> {});
        } catch (Exception ex) {
            logger.error(new MessageFormatMessage("Failed to send message to Telegram: (message: {})", message), ex);
            return CompletableFuture.failedFuture(ex);
        }
    }

    public void flagServerShutdown() {
        if (hasSentShutdown.compareAndSet(false, true)) {
            TelegramBridgeConfig config = TelegramBridgeConfig.getConfig();
            if (!config.messages.isServerShutdownMessageEnabled) {
                return;
            }

            String message = config.messages.serverShutdownMessage;
            this.sendMessage(message)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            logger.error("Failed to send shutdown message", ex);
                        }
                    });

            this.isUpdatesPolling.set(false);
        }
    }

    private CompletableFuture<String> get(String method, List<AbstractMap.SimpleEntry<String, Object>> params) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(getTelegramApiUri(method, params))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .GET()
                .build();

        return createHttpClient().sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() != 200) {
                        logger.error("Error calling Telegram endpoint! {}\nResponse: {}", response.statusCode(), response.body());
                    }
                    return response.body();
                });
    }

    private CompletableFuture<String> post(String method, List<AbstractMap.SimpleEntry<String, Object>> params) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(getTelegramApiUri(method, null))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(getQueryString(params)))
                .build();

        return createHttpClient().sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() != 200) {
                        logger.error("Error calling Telegram endpoint! {}\nResponse: {}", response.statusCode(), response.body());
                    }
                    return response.body();
                });
    }

    private static URI getTelegramApiUri(String method, List<AbstractMap.SimpleEntry<String, Object>> params) {
        // https://api.telegram.org/bot<token>/METHOD_NAME
        String apiToken = TelegramBridgeConfig.getConfig().telegram.botApiToken;
        if (apiToken == null || apiToken.isBlank()) {
            throw new IllegalStateException("API Token is invalid");
        }

        String url = "https://api.telegram.org/bot" + apiToken + "/" + method;
        if (params != null) {
            url += "?" + getQueryString(params);
        }
        return URI.create(url);
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
