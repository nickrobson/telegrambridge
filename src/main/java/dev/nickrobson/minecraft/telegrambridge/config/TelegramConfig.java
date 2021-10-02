package dev.nickrobson.minecraft.telegrambridge.config;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

public class TelegramConfig {
    @Comment("Telegram bot API token for bridging messages with")
    public String botApiToken = "";

    @Comment("ID of the Telegram chat to bridge messages to and from")
    public long chatId = 0;
}
