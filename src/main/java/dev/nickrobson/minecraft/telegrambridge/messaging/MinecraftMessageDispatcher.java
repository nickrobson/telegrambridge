package dev.nickrobson.minecraft.telegrambridge.messaging;

import dev.nickrobson.minecraft.telegrambridge.TelegramBridgeMod;
import net.minecraft.server.MinecraftServer;
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

        Text text = Text.empty().setStyle(Style.EMPTY)
                .append(Text.literal("[Telegram] ").setStyle(Style.EMPTY.withColor(Formatting.AQUA)))
                .append(Text.literal(messageText).setStyle(Style.EMPTY.withColor(Formatting.WHITE)));

        minecraftServer.getPlayerManager().broadcast(text, false);
    }
}
