package dev.nickrobson.minecraft.telegrambridge.core.config;

public record ConfigOption<T>(String category, String name, String[] comment, T defaultValue) {
}
