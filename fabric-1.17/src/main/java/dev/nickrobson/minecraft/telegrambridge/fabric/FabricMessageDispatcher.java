package dev.nickrobson.minecraft.telegrambridge.fabric;

import dev.nickrobson.minecraft.telegrambridge.core.minecraft.MinecraftMessageDispatcher;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static dev.nickrobson.minecraft.telegrambridge.core.minecraft.MinecraftController.TELEGRAMBRIDGE_UUID;

public class FabricMessageDispatcher implements MinecraftMessageDispatcher {
    @Override
    public void sendMessage(String messageText) {
        MinecraftServer minecraftServer = TelegramBridgeFabricMod.MINECRAFT_SERVER.get();
        if (minecraftServer == null) {
            return;
        }

        Text text = new LiteralText("").setStyle(Style.EMPTY)
                .append(new LiteralText("[Telegram] ").setStyle(Style.EMPTY.withColor(Formatting.AQUA)))
                .append(new LiteralText(messageText).setStyle(Style.EMPTY.withColor(Formatting.WHITE)));

        minecraftServer.getPlayerManager().broadcastChatMessage(text, MessageType.CHAT, TELEGRAMBRIDGE_UUID);
    }
}
