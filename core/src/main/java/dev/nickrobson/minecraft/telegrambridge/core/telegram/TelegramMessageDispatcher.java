package dev.nickrobson.minecraft.telegrambridge.core.telegram;

import dev.nickrobson.minecraft.telegrambridge.core.MessageDispatcher;

public class TelegramMessageDispatcher implements MessageDispatcher {
    private final TelegramController telegramController;

    public TelegramMessageDispatcher(TelegramController telegramController) {
        this.telegramController = telegramController;
    }

    @Override
    public void sendMessage(String message) {
        telegramController.getTelegramClient().sendMessage(message);
    }
}
