package dev.nickrobson.minecraft.telegrambridge.forge;

import dev.nickrobson.minecraft.telegrambridge.core.config.ConfigOption;
import dev.nickrobson.minecraft.telegrambridge.core.config.MessageConfiguration;
import dev.nickrobson.minecraft.telegrambridge.core.config.TelegramBridgeConfiguration;
import dev.nickrobson.minecraft.telegrambridge.core.config.TelegramConfiguration;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import static dev.nickrobson.minecraft.telegrambridge.core.config.MessageConfiguration.IS_CHAT_MESSAGES_ENABLED;
import static dev.nickrobson.minecraft.telegrambridge.core.config.MessageConfiguration.IS_DEATH_MESSAGE_ENABLED;
import static dev.nickrobson.minecraft.telegrambridge.core.config.MessageConfiguration.IS_JOIN_MESSAGE_ENABLED;
import static dev.nickrobson.minecraft.telegrambridge.core.config.MessageConfiguration.IS_LEAVE_MESSAGE_ENABLED;
import static dev.nickrobson.minecraft.telegrambridge.core.config.MessageConfiguration.IS_SHUTDOWN_MESSAGE_ENABLED;
import static dev.nickrobson.minecraft.telegrambridge.core.config.MessageConfiguration.IS_STARTUP_MESSAGE_ENABLED;
import static dev.nickrobson.minecraft.telegrambridge.core.config.MessageConfiguration.JOIN_MESSAGE_FORMAT;
import static dev.nickrobson.minecraft.telegrambridge.core.config.MessageConfiguration.LEAVE_MESSAGE_FORMAT;
import static dev.nickrobson.minecraft.telegrambridge.core.config.MessageConfiguration.MINECRAFT_CHAT_MESSAGE_FORMAT;
import static dev.nickrobson.minecraft.telegrambridge.core.config.MessageConfiguration.SHUTDOWN_MESSAGE;
import static dev.nickrobson.minecraft.telegrambridge.core.config.MessageConfiguration.STARTUP_MESSAGE;
import static dev.nickrobson.minecraft.telegrambridge.core.config.MessageConfiguration.TELEGRAM_CHAT_MESSAGE_FORMAT;
import static dev.nickrobson.minecraft.telegrambridge.core.config.TelegramConfiguration.TELEGRAM_BOT_API_TOKEN;
import static dev.nickrobson.minecraft.telegrambridge.core.config.TelegramConfiguration.TELEGRAM_CHAT_ID;

public class TelegramBridgeForgeModConfiguration {
    private final ForgeConfigSpec config;

    private final ForgeConfigSpec.ConfigValue<String> telegramApiToken;
    private final ForgeConfigSpec.ConfigValue<Integer> telegramChatId;

    public final ForgeConfigSpec.ConfigValue<Boolean> isChatMessageEnabled;
    public final ForgeConfigSpec.ConfigValue<String> telegramChatMessageFormat;
    public final ForgeConfigSpec.ConfigValue<String> minecraftChatMessageFormat;
    public final ForgeConfigSpec.ConfigValue<Boolean> isDeathMessageEnabled;
    public final ForgeConfigSpec.ConfigValue<Boolean> isJoinMessageEnabled;
    public final ForgeConfigSpec.ConfigValue<String> joinMessageFormat;
    public final ForgeConfigSpec.ConfigValue<Boolean> isLeaveMessageEnabled;
    public final ForgeConfigSpec.ConfigValue<String> leaveMessageFormat;
    public final ForgeConfigSpec.ConfigValue<Boolean> isStartupMessageEnabled;
    public final ForgeConfigSpec.ConfigValue<String> startupMessage;
    public final ForgeConfigSpec.ConfigValue<Boolean> isShutdownMessageEnabled;
    public final ForgeConfigSpec.ConfigValue<String> shutdownMessage;

    private static <T> ForgeConfigSpec.ConfigValue<T> defineConfigOption(ForgeConfigSpec.Builder configBuilder, ConfigOption<T> configOption) {
        return configBuilder
                .comment(configOption.comment())
                .define(configOption.name(), configOption.defaultValue());
    }

    public TelegramBridgeForgeModConfiguration() {
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();

        // Telegram settings
        configBuilder
                    .comment(TelegramBridgeConfiguration.TELEGRAM_SECTION_COMMENT)
                    .push(TelegramBridgeConfiguration.TELEGRAM_SECTION);

        telegramApiToken = defineConfigOption(configBuilder, TELEGRAM_BOT_API_TOKEN);
        telegramChatId = defineConfigOption(configBuilder, TELEGRAM_CHAT_ID);

        configBuilder.pop();

        // Message settings
        configBuilder
                .comment(TelegramBridgeConfiguration.MESSAGES_SECTION_COMMENT)
                .push(TelegramBridgeConfiguration.MESSAGES_SECTION);

        isChatMessageEnabled = defineConfigOption(configBuilder, IS_CHAT_MESSAGES_ENABLED);
        telegramChatMessageFormat = defineConfigOption(configBuilder, TELEGRAM_CHAT_MESSAGE_FORMAT);
        minecraftChatMessageFormat = defineConfigOption(configBuilder, MINECRAFT_CHAT_MESSAGE_FORMAT);

        isDeathMessageEnabled = defineConfigOption(configBuilder, IS_DEATH_MESSAGE_ENABLED);

        isJoinMessageEnabled = defineConfigOption(configBuilder, IS_JOIN_MESSAGE_ENABLED);
        joinMessageFormat = defineConfigOption(configBuilder, JOIN_MESSAGE_FORMAT);
        isLeaveMessageEnabled = defineConfigOption(configBuilder, IS_LEAVE_MESSAGE_ENABLED);
        leaveMessageFormat = defineConfigOption(configBuilder, LEAVE_MESSAGE_FORMAT);

        isStartupMessageEnabled = defineConfigOption(configBuilder, IS_STARTUP_MESSAGE_ENABLED);
        startupMessage = defineConfigOption(configBuilder, STARTUP_MESSAGE);
        isShutdownMessageEnabled = defineConfigOption(configBuilder, IS_SHUTDOWN_MESSAGE_ENABLED);
        shutdownMessage = defineConfigOption(configBuilder, SHUTDOWN_MESSAGE);

        configBuilder.pop();

        config = configBuilder.build();
    }

    public void registerConfig() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, config);
    }

    public void onReload() {
        TelegramConfiguration telegramConfiguration = new TelegramConfiguration(
                telegramApiToken.get(),
                telegramChatId.get()
        );

        MessageConfiguration messageConfiguration = new MessageConfiguration(
            isChatMessageEnabled.get(),
            telegramChatMessageFormat.get(),
            minecraftChatMessageFormat.get(),
            isJoinMessageEnabled.get(),
            joinMessageFormat.get(),
            isLeaveMessageEnabled.get(),
            leaveMessageFormat.get(),
            isDeathMessageEnabled.get(),
            isStartupMessageEnabled.get(),
            startupMessage.get(),
            isShutdownMessageEnabled.get(),
            shutdownMessage.get()
        );

        TelegramBridgeConfiguration configuration = new TelegramBridgeConfiguration(
                telegramConfiguration,
                messageConfiguration
        );

        TelegramBridgeConfiguration.setConfiguration(configuration);
    }
}
