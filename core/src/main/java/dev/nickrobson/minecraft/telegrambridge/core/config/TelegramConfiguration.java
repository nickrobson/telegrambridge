package dev.nickrobson.minecraft.telegrambridge.core.config;

public record TelegramConfiguration(
        String apiToken,
        int chatId
) {

    public static final ConfigOption<String> TELEGRAM_BOT_API_TOKEN = new ConfigOption<>(
            TelegramBridgeConfiguration.TELEGRAM_SECTION,
            "Telegram bot token",
            new String[] {
                    "Telegram bot API token for bridging messages with"
            },
            ""
    );

    public static final ConfigOption<Integer> TELEGRAM_CHAT_ID = new ConfigOption<>(
            TelegramBridgeConfiguration.TELEGRAM_SECTION,
            "Telegram chat ID",
            new String[] {
                    "ID of the Telegram chat to bridge messages to and from"
            },
            0
    );

}
