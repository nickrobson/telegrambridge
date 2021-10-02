package dev.nickrobson.minecraft.telegrambridge.config;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

public class MessagesConfig {
    @Comment("When enabled, chat messages will be sent both ways between Minecraft and Telegram.")
    public boolean isChatMessageRelayEnabled = true;

    @Comment(
        """
        The format for the message sent to Telegram when a player sends a chat message in Minecraft
        Available placeholders:
        - {USERNAME} will be replaced with the player's username
        - {MESSAGE} will be replaced with the chat message content
        """
    )
    public String telegramChatMessageFormat = "<{USERNAME}> {MESSAGE}";

    @Comment(
        """
        The format for the message sent to Minecraft when a user sends a chat message in Telegram
        Available placeholders:
        - {USERNAME} will be replaced with the user's username in Telegram
        - {NAME} will be replaced with the user's display name in Telegram (if allowed by their privacy settings)
        - {MESSAGE} will be replaced with the chat message content
        """
    )
    public String minecraftChatMessageFormat = "<{USERNAME}> {MESSAGE}";

    @Comment("When enabled, player join messages will be sent to Telegram.")
    public boolean isJoinMessageEnabled = true;

    @Comment(
        """
        The format for the message sent to Telegram when a player joins the server
        The {USERNAME} placeholder will be replaced with the player's username
        """
    )
    public String joinMessageFormat = "{USERNAME} joined the Minecraft server.";

    @Comment("When enabled, player leave messages will be sent to Telegram.")
    public boolean isLeaveMessageEnabled = true;

    @Comment(
        """
        The format for the message sent to Telegram when a player leaves the server
        The {USERNAME} placeholder will be replaced with the player's username
        """
    )
    public String leaveMessageFormat = "{USERNAME} left the Minecraft server.";

    @Comment("When enabled, player death messages will be sent to Telegram.")
    public boolean isDeathMessageEnabled = true;

    @Comment("When enabled, server startup messages will be sent to Telegram.")
    public boolean isServerStartMessageEnabled = true;

    @Comment("Message sent to Telegram when the server starts up (if enabled)")
    public String serverStartupMessage = "The Minecraft server is now online.";

    @Comment("When enabled, server shutdown messages will be sent to Telegram.")
    public boolean isServerShutdownMessageEnabled = true;

    @Comment("Message sent to Telegram when the server shuts down (if enabled)")
    public String serverShutdownMessage = "The Minecraft server is now offline.";
}
