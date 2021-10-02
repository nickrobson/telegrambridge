package dev.nickrobson.minecraft.telegrambridge.messaging;

import dev.nickrobson.minecraft.telegrambridge.TelegramBridgeMod;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MinecraftMessageDispatcher implements MessageDispatcher {
    @Override
    public void sendMessage(String messageText) {
        MinecraftServer minecraftServer = TelegramBridgeMod.MINECRAFT_SERVER.get();
        if (minecraftServer == null) {
            return;
        }

        Text text = new LiteralText("").setStyle(Style.EMPTY)
                .append(new LiteralText("[Telegram] ").setStyle(Style.EMPTY.withColor(Formatting.AQUA)))
                .append(new LiteralText(messageText).setStyle(Style.EMPTY.withColor(Formatting.WHITE)));

        minecraftServer.getPlayerManager().broadcastChatMessage(text, MessageType.CHAT, MinecraftMessagingController.TELEGRAMBRIDGE_UUID);
    }
}
