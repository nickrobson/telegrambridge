package dev.nickrobson.minecraft.telegrambridge.core.telegram;

import dev.nickrobson.minecraft.telegrambridge.core.MessageDispatcher;
import dev.nickrobson.minecraft.telegrambridge.core.config.TelegramBridgeConfiguration;
import dev.nickrobson.minecraft.telegrambridge.core.telegram.client.TelegramClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TelegramController {
    private static final Logger logger = LogManager.getLogger(TelegramController.class);

    private final MessageDispatcher telegramMessageDispatcher;
    private final MessageDispatcher externalMessageDispatcher;
    private final TelegramClient telegramClient;

    public TelegramController(MessageDispatcher externalMessageDispatcher) {
        this.externalMessageDispatcher = externalMessageDispatcher;
        this.telegramMessageDispatcher = new TelegramMessageDispatcher(this);
        this.telegramClient = new TelegramClient();
    }

    public TelegramClient getTelegramClient() {
        return telegramClient;
    }

    public MessageDispatcher getTelegramMessageDispatcher() {
        return telegramMessageDispatcher;
    }

    public void onReload() {
        this.telegramClient.stopUpdatesPolling();

        TelegramBridgeConfiguration configuration = TelegramBridgeConfiguration.getConfiguration();
        String apiToken = configuration.telegram().apiToken();
        int chatId = configuration.telegram().chatId();

        String errorMessage = null;
        if (apiToken == null || apiToken.isBlank() || chatId == 0) {
            errorMessage = "API token and/or chat ID is invalid. Make sure they're both set to the correct values!";
        }
        if (errorMessage != null) {
            throw new IllegalStateException(errorMessage);
        }

        logger.info("Loaded and validated config");

        TelegramUpdatesListener updatesListener = new TelegramUpdatesListener(externalMessageDispatcher);
        this.telegramClient.setUpdatesListener(updatesListener);
        this.telegramClient.startUpdatesPolling();
    }

}
