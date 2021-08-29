package dev.nickrobson.minecraft.telegrambridge.core.minecraft;

import dev.nickrobson.minecraft.telegrambridge.core.MessageDispatcher;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static dev.nickrobson.minecraft.telegrambridge.core.MessagePlaceholders.PLACEHOLDER_MESSAGE;
import static dev.nickrobson.minecraft.telegrambridge.core.MessagePlaceholders.PLACEHOLDER_USERNAME;
import static dev.nickrobson.minecraft.telegrambridge.core.config.TelegramBridgeConfiguration.getConfiguration;

public class MinecraftController {
    public static UUID TELEGRAMBRIDGE_UUID = UUID.nameUUIDFromBytes("TelegramBridge".getBytes(StandardCharsets.UTF_8));

    private final MessageDispatcher messageDispatcher;

    public MinecraftController(MessageDispatcher messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
    }

    public void onPlayerChat(String playerName, String chatMessage) {
        if (!getConfiguration().messages().isChatMessageEnabled()) {
            return;
        }

        String message = getConfiguration().messages().telegramChatMessageFormat()
                .replaceAll(PLACEHOLDER_USERNAME, playerName)
                .replaceAll(PLACEHOLDER_MESSAGE, chatMessage);

        messageDispatcher.sendMessage(message);
    }

    public void onPlayerDeath(String deathMessage) {
        if (!getConfiguration().messages().isDeathMessageEnabled()) {
            return;
        }
        messageDispatcher.sendMessage(deathMessage);
    }

    public void onPlayerJoin(String username) {
        if (!getConfiguration().messages().isJoinMessageEnabled()) {
            return;
        }

        String message = getConfiguration().messages().joinMessageFormat()
                .replaceAll(PLACEHOLDER_USERNAME, username);
        messageDispatcher.sendMessage(message);
    }

    public void onPlayerLeave(String username) {
        if (!getConfiguration().messages().isLeaveMessageEnabled()) {
            return;
        }

        String message = getConfiguration().messages().leaveMessageFormat()
                .replaceAll(PLACEHOLDER_USERNAME, username);
        messageDispatcher.sendMessage(message);
    }

    public void onServerStart() {
        if (!getConfiguration().messages().isStartupMessageEnabled()) {
            return;
        }

        String message = getConfiguration().messages().startupMessage();
        messageDispatcher.sendMessage(message);
    }
}
