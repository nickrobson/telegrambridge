package dev.nickrobson.minecraft.telegrambridge.config;

import dev.nickrobson.minecraft.telegrambridge.TelegramBridgeMod;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = TelegramBridgeMod.MOD_ID)
public class TelegramBridgeConfig implements ConfigData {
    public static void registerConfig() {
        AutoConfig.register(TelegramBridgeConfig.class, JanksonConfigSerializer::new);
    }

    public static TelegramBridgeConfig getConfig() {
        return AutoConfig.getConfigHolder(TelegramBridgeConfig.class).getConfig();
    }

    @ConfigEntry.Category("telegram")
    @Comment("Configuration for the Telegram bot that this mod will use to bridge messages")
    public TelegramConfig telegram = new TelegramConfig();

    @ConfigEntry.Category("messages")
    @Comment("Configuration for the messages that will be sent by this mod")
    public MessagesConfig messages = new MessagesConfig();
}
