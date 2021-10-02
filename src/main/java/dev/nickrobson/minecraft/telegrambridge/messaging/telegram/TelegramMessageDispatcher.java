package dev.nickrobson.minecraft.telegrambridge.messaging.telegram;

import dev.nickrobson.minecraft.telegrambridge.messaging.MessageDispatcher;

public class TelegramMessageDispatcher implements MessageDispatcher {
    private final TelegramMessagingController telegramMessagingController;

    public TelegramMessageDispatcher(TelegramMessagingController telegramMessagingController) {
        this.telegramMessagingController = telegramMessagingController;
    }

    @Override
    public void sendMessage(String message) {
        telegramMessagingController.getTelegramClient().sendMessage(message);
    }
}
