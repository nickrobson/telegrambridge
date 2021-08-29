package dev.nickrobson.minecraft.telegrambridge.core.config;

public record MessageConfiguration(
        boolean isChatMessageEnabled,
        String telegramChatMessageFormat,
        String minecraftChatMessageFormat,
        boolean isJoinMessageEnabled,
        String joinMessageFormat,
        boolean isLeaveMessageEnabled,
        String leaveMessageFormat,
        boolean isDeathMessageEnabled,
        boolean isStartupMessageEnabled,
        String startupMessage,
        boolean isShutdownMessageEnabled,
        String shutdownMessage
) {
    public static final ConfigOption<Boolean> IS_CHAT_MESSAGES_ENABLED = new ConfigOption<>(
            TelegramBridgeConfiguration.MESSAGES_SECTION,
            "Chat messages enabled?",
            new String[]{
                    "When enabled, chat messages will be sent both ways between Minecraft and Telegram."
            },
            true
    );

    public static final ConfigOption<String> TELEGRAM_CHAT_MESSAGE_FORMAT = new ConfigOption<>(
            TelegramBridgeConfiguration.MESSAGES_SECTION,
            "Format for chat messages bridged to Telegram",
            new String[]{
                    "The format for the message sent to Telegram when a player sends a chat message in Minecraft",
                    "Available placeholders:",
                    "- {USERNAME} will be replaced with the player's username",
                    "- {MESSAGE} will be replaced with the chat message content"
            },
            "<{USERNAME}> {MESSAGE}"
    );

    public static final ConfigOption<String> MINECRAFT_CHAT_MESSAGE_FORMAT = new ConfigOption<>(
            TelegramBridgeConfiguration.MESSAGES_SECTION,
            "Format for chat messages bridged to Minecraft",
            new String[]{
                    "The format for the message sent to Minecraft when a user sends a chat message in Telegram",
                    "Available placeholders:",
                    "- {USERNAME} will be replaced with the user's username in Telegram",
                    "- {NAME} will be replaced with the user's display name in Telegram (according to their privacy settings)",
                    "- {MESSAGE} will be replaced with the chat message content"
            },
            "<{USERNAME}> {MESSAGE}"
    );

    public static final ConfigOption<Boolean> IS_JOIN_MESSAGE_ENABLED = new ConfigOption<>(
            TelegramBridgeConfiguration.MESSAGES_SECTION,
            "Join messages enabled?",
            new String[]{
                    "When enabled, player join messages will be sent to Telegram."
            },
            true
    );

    public static final ConfigOption<String> JOIN_MESSAGE_FORMAT = new ConfigOption<>(
            TelegramBridgeConfiguration.MESSAGES_SECTION,
            "Format for the message sent to Telegram when a user joins the server (if enabled)",
            new String[]{
                    "The format for the message sent to Telegram when a player joins the server",
                    "The {USERNAME} placeholder will be replaced with the player's username"
            },
            "{USERNAME} joined the Minecraft server."
    );

    public static final ConfigOption<Boolean> IS_LEAVE_MESSAGE_ENABLED = new ConfigOption<>(
            TelegramBridgeConfiguration.MESSAGES_SECTION,
            "Leave messages enabled?",
            new String[]{
                    "When enabled, player leave messages will be sent to Telegram."
            },
            true
    );

    public static final ConfigOption<String> LEAVE_MESSAGE_FORMAT = new ConfigOption<>(
            TelegramBridgeConfiguration.MESSAGES_SECTION,
            "Format for the message sent to Telegram when a user leaves the server (if enabled)",
            new String[]{
                    "The format for the message sent to Telegram when a player leaves the server",
                    "The {USERNAME} placeholder will be replaced with the player's username"
            },
            "{USERNAME} left the Minecraft server."
    );

    public static final ConfigOption<Boolean> IS_DEATH_MESSAGE_ENABLED = new ConfigOption<>(
            TelegramBridgeConfiguration.MESSAGES_SECTION,
            "Death messages enabled?",
            new String[]{
                    "When enabled, death messages will be sent to Telegram."
            },
            true
    );

    public static final ConfigOption<Boolean> IS_STARTUP_MESSAGE_ENABLED = new ConfigOption<>(
            TelegramBridgeConfiguration.MESSAGES_SECTION,
            "Startup messages enabled?",
            new String[]{
                    "When enabled, server startup messages will be sent to Telegram."
            },
            true
    );

    public static final ConfigOption<String> STARTUP_MESSAGE = new ConfigOption<>(
            TelegramBridgeConfiguration.MESSAGES_SECTION,
            "Startup message",
            new String[]{
                    "Message sent to Telegram when the server starts up (if enabled)"
            },
            "The Minecraft server is now online."
    );

    public static final ConfigOption<Boolean> IS_SHUTDOWN_MESSAGE_ENABLED = new ConfigOption<>(
            TelegramBridgeConfiguration.MESSAGES_SECTION,
            "Shutdown messages enabled?",
            new String[]{
                    "When enabled, server shutdown messages will be sent to Telegram."
            },
            true
    );

    public static final ConfigOption<String> SHUTDOWN_MESSAGE = new ConfigOption<>(
            TelegramBridgeConfiguration.MESSAGES_SECTION,
            "Shutdown message",
            new String[]{
                    "Message sent to Telegram when the server shuts down (if enabled)",
            },
            "The Minecraft server is now offline."
    );
}
