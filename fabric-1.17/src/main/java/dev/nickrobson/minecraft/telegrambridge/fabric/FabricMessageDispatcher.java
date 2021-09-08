package dev.nickrobson.minecraft.telegrambridge.fabric;

import dev.nickrobson.minecraft.telegrambridge.core.minecraft.MinecraftMessageDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;

import static dev.nickrobson.minecraft.telegrambridge.core.minecraft.MinecraftController.TELEGRAMBRIDGE_UUID;

public class FabricMessageDispatcher implements MinecraftMessageDispatcher {
    @Override
    public void sendMessage(String messageText) {
        MinecraftServer minecraftServer = TelegramBridgeFabricMod.MINECRAFT_SERVER.get();
        if (minecraftServer == null) {
            return;
        }

        Component text = new TextComponent("").setStyle(Style.EMPTY)
                .append(new TextComponent("[Telegram] ").setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)))
                .append(new TextComponent(messageText).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)));

        minecraftServer.getPlayerList().broadcastMessage(text, ChatType.CHAT, TELEGRAMBRIDGE_UUID);
    }
}
