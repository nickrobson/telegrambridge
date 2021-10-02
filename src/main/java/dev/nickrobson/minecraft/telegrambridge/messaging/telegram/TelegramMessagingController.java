package dev.nickrobson.minecraft.telegrambridge.messaging.telegram;

import dev.nickrobson.minecraft.telegrambridge.config.TelegramBridgeConfig;
import dev.nickrobson.minecraft.telegrambridge.config.TelegramConfig;
import dev.nickrobson.minecraft.telegrambridge.messaging.MessageDispatcher;
import dev.nickrobson.minecraft.telegrambridge.messaging.telegram.client.TelegramClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TelegramMessagingController {
    private static final Logger logger = LogManager.getLogger(TelegramMessagingController.class);

    private final MessageDispatcher telegramMessageDispatcher;
    private final MessageDispatcher externalMessageDispatcher;
    private final TelegramClient telegramClient;

    public TelegramMessagingController(MessageDispatcher externalMessageDispatcher) {
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

        TelegramConfig config = TelegramBridgeConfig.getConfig().telegram;
        String apiToken = config.botApiToken;
        long chatId = config.chatId;

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
