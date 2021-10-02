package dev.nickrobson.minecraft.telegrambridge.messaging;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static dev.nickrobson.minecraft.telegrambridge.config.TelegramBridgeConfig.getConfig;
import static dev.nickrobson.minecraft.telegrambridge.messaging.MessagePlaceholders.PLACEHOLDER_MESSAGE;
import static dev.nickrobson.minecraft.telegrambridge.messaging.MessagePlaceholders.PLACEHOLDER_USERNAME;

public class MinecraftMessagingController {
    public static UUID TELEGRAMBRIDGE_UUID = UUID.nameUUIDFromBytes("TelegramBridge".getBytes(StandardCharsets.UTF_8));

    private final MessageDispatcher messageDispatcher;

    public MinecraftMessagingController(MessageDispatcher messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
    }

    public void onPlayerChat(String playerName, String chatMessage) {
        if (!getConfig().messages.isChatMessageRelayEnabled) {
            return;
        }

        String message = getConfig().messages.telegramChatMessageFormat
                .replaceAll(PLACEHOLDER_USERNAME, playerName)
                .replaceAll(PLACEHOLDER_MESSAGE, chatMessage);

        messageDispatcher.sendMessage(message);
    }

    public void onPlayerDeath(String deathMessage) {
        if (!getConfig().messages.isDeathMessageEnabled) {
            return;
        }
        messageDispatcher.sendMessage(deathMessage);
    }

    public void onPlayerJoin(String username) {
        if (!getConfig().messages.isJoinMessageEnabled) {
            return;
        }

        String message = getConfig().messages.joinMessageFormat
                .replaceAll(PLACEHOLDER_USERNAME, username);
        messageDispatcher.sendMessage(message);
    }

    public void onPlayerLeave(String username) {
        if (!getConfig().messages.isLeaveMessageEnabled) {
            return;
        }

        String message = getConfig().messages.leaveMessageFormat
                .replaceAll(PLACEHOLDER_USERNAME, username);
        messageDispatcher.sendMessage(message);
    }

    public void onServerStart() {
        if (!getConfig().messages.isServerStartMessageEnabled) {
            return;
        }

        String message = getConfig().messages.serverStartupMessage;
        messageDispatcher.sendMessage(message);
    }
}
