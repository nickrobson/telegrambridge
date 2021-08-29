package dev.nickrobson.minecraft.telegrambridge.core.telegram.client;

import dev.nickrobson.minecraft.telegrambridge.core.telegram.client.model.Update;

import java.util.List;

public interface TelegramUpdateListener {
    void processUpdates(List<Update> updates);
}
