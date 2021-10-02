package dev.nickrobson.minecraft.telegrambridge.messaging.telegram.client;

import dev.nickrobson.minecraft.telegrambridge.messaging.telegram.client.model.Update;

import java.util.List;

public interface TelegramUpdateListener {
    void processUpdates(List<Update> updates);
}
