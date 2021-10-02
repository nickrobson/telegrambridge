package dev.nickrobson.minecraft.telegrambridge.messaging;

import java.util.regex.Pattern;

public class MessagePlaceholders {
    public static final String PLACEHOLDER_USERNAME = Pattern.quote("{USERNAME}");
    public static final String PLACEHOLDER_NAME = Pattern.quote("{NAME}");
    public static final String PLACEHOLDER_MESSAGE = Pattern.quote("{MESSAGE}");
}
