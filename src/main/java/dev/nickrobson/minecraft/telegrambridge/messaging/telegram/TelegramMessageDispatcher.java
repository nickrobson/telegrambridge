package dev.nickrobson.minecraft.telegrambridge.messaging.telegram;

import dev.nickrobson.minecraft.telegrambridge.messaging.MessageDispatcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TelegramMessageDispatcher implements MessageDispatcher {
    private static final Logger logger = LogManager.getLogger(TelegramMessageDispatcher.class);
    private final TelegramMessagingController telegramMessagingController;

    public TelegramMessageDispatcher(TelegramMessagingController telegramMessagingController) {
        this.telegramMessagingController = telegramMessagingController;
    }

    @Override
    public void sendMessage(String message) {
        telegramMessagingController.getTelegramClient().sendMessage(message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        logger.error("Failed to send message to Telegram", ex);
                    }
                });
    }
}
