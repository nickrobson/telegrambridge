package dev.nickrobson.minecraft.telegrambridge.messaging;

import dev.nickrobson.minecraft.telegrambridge.TelegramBridgeMod;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;

public class MinecraftMessageDispatcher implements MessageDispatcher {
    @Override
    public void sendMessage(String messageText) {
        MinecraftServer minecraftServer = TelegramBridgeMod.MINECRAFT_SERVER.get();
        if (minecraftServer == null) {
            return;
        }

        Component text = new TextComponent("").setStyle(Style.EMPTY)
                .append(new TextComponent("[Telegram] ").setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)))
                .append(new TextComponent(messageText).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)));

        minecraftServer.getPlayerList().broadcastMessage(text, ChatType.CHAT, MinecraftMessagingController.TELEGRAMBRIDGE_UUID);
    }
}
