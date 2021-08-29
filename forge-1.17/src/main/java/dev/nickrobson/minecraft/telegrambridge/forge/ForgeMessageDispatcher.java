package dev.nickrobson.minecraft.telegrambridge.forge;

import dev.nickrobson.minecraft.telegrambridge.core.minecraft.MinecraftMessageDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

import java.util.UUID;

import static dev.nickrobson.minecraft.telegrambridge.core.minecraft.MinecraftController.TELEGRAMBRIDGE_UUID;

public class ForgeMessageDispatcher implements MinecraftMessageDispatcher {
    @Override
    public void sendMessage(String messageText) {
        Component messageComponent = new TextComponent("").setStyle(Style.EMPTY)
                .append(new TextComponent("[Telegram] ").setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)))
                .append(new TextComponent(messageText).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)));

        // send to console
        ServerLifecycleHooks.getCurrentServer()
                .sendMessage(messageComponent, new UUID(0, 0));
        // send to players
        ServerLifecycleHooks.getCurrentServer()
                .getPlayerList()
                .broadcastMessage(messageComponent, ChatType.CHAT, TELEGRAMBRIDGE_UUID);
    }
}
