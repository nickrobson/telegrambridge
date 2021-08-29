package dev.nickrobson.minecraft.telegrambridge.core.config;

import java.util.concurrent.atomic.AtomicReference;

public record TelegramBridgeConfiguration(
        TelegramConfiguration telegram,
        MessageConfiguration messages) {
    public static final String TELEGRAM_SECTION = "telegram";
    public static final String TELEGRAM_SECTION_COMMENT = "Configuration for the Telegram bot that this mod will use to bridge messages";

    public static final String MESSAGES_SECTION = "messages";
    public static final String MESSAGES_SECTION_COMMENT = "Configuration for the messages that will be sent by this mod";

    private static final AtomicReference<TelegramBridgeConfiguration> configuration = new AtomicReference<>(null);
    public static TelegramBridgeConfiguration getConfiguration() {
        return configuration.get();
    }
    public static void setConfiguration(TelegramBridgeConfiguration configuration) {
        TelegramBridgeConfiguration.configuration.set(configuration);
    }
}
