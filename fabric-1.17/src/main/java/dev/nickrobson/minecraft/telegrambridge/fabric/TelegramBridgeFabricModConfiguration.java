package dev.nickrobson.minecraft.telegrambridge.fabric;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import dev.nickrobson.minecraft.telegrambridge.core.config.ConfigOption;
import dev.nickrobson.minecraft.telegrambridge.core.config.MessageConfiguration;
import dev.nickrobson.minecraft.telegrambridge.core.config.TelegramBridgeConfiguration;
import dev.nickrobson.minecraft.telegrambridge.core.config.TelegramConfiguration;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import static dev.nickrobson.minecraft.telegrambridge.core.config.TelegramBridgeConfiguration.MESSAGES_SECTION;
import static dev.nickrobson.minecraft.telegrambridge.core.config.TelegramBridgeConfiguration.MESSAGES_SECTION_COMMENT;
import static dev.nickrobson.minecraft.telegrambridge.core.config.TelegramBridgeConfiguration.TELEGRAM_SECTION;
import static dev.nickrobson.minecraft.telegrambridge.core.config.TelegramBridgeConfiguration.TELEGRAM_SECTION_COMMENT;
import static dev.nickrobson.minecraft.telegrambridge.core.config.TelegramConfiguration.TELEGRAM_BOT_API_TOKEN;
import static dev.nickrobson.minecraft.telegrambridge.core.config.TelegramConfiguration.TELEGRAM_CHAT_ID;

public class TelegramBridgeFabricModConfiguration {
    private static final Logger logger = LogManager.getLogger(TelegramBridgeFabricModConfiguration.class);

    private final ConfigValue<String> telegramApiToken;
    private final ConfigValue<Integer> telegramChatId;

    private final ConfigValue<Boolean> isChatMessageEnabled;
    private final ConfigValue<String> telegramChatMessageFormat;
    private final ConfigValue<String> minecraftChatMessageFormat;
    private final ConfigValue<Boolean> isDeathMessageEnabled;
    private final ConfigValue<Boolean> isJoinMessageEnabled;
    private final ConfigValue<String> joinMessageFormat;
    private final ConfigValue<Boolean> isLeaveMessageEnabled;
    private final ConfigValue<String> leaveMessageFormat;
    private final ConfigValue<Boolean> isStartupMessageEnabled;
    private final ConfigValue<String> startupMessage;
    private final ConfigValue<Boolean> isShutdownMessageEnabled;
    private final ConfigValue<String> shutdownMessage;

    private List<ConfigValue<?>> configValues = new ArrayList<>();

    public TelegramBridgeFabricModConfiguration() {
        telegramApiToken = registerConfigOption(TELEGRAM_BOT_API_TOKEN);
        telegramChatId = registerConfigOption(TELEGRAM_CHAT_ID);

        isChatMessageEnabled = registerConfigOption(IS_CHAT_MESSAGES_ENABLED);
        telegramChatMessageFormat = registerConfigOption(TELEGRAM_CHAT_MESSAGE_FORMAT);
        minecraftChatMessageFormat = registerConfigOption(MINECRAFT_CHAT_MESSAGE_FORMAT);

        isDeathMessageEnabled = registerConfigOption(IS_DEATH_MESSAGE_ENABLED);

        isJoinMessageEnabled = registerConfigOption(IS_JOIN_MESSAGE_ENABLED);
        joinMessageFormat = registerConfigOption(JOIN_MESSAGE_FORMAT);
        isLeaveMessageEnabled = registerConfigOption(IS_LEAVE_MESSAGE_ENABLED);
        leaveMessageFormat = registerConfigOption(LEAVE_MESSAGE_FORMAT);

        isStartupMessageEnabled = registerConfigOption(IS_STARTUP_MESSAGE_ENABLED);
        startupMessage = registerConfigOption(STARTUP_MESSAGE);
        isShutdownMessageEnabled = registerConfigOption(IS_SHUTDOWN_MESSAGE_ENABLED);
        shutdownMessage = registerConfigOption(SHUTDOWN_MESSAGE);

        configValues = Collections.unmodifiableList(configValues);
    }

    public void loadConfig() {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        Path configFile = configDir.resolve("telegrambridge-server.toml");

        try (CommentedFileConfig config = CommentedFileConfig.of(configFile, TomlFormat.instance())) {
            if (Files.exists(configFile)) {
                config.load();
                configValues.forEach(configValue -> configValue.load(config));
                logger.info("Loaded TelegramBridge config from file");
            } else {
                try {
                    config.setComment(TELEGRAM_SECTION, TELEGRAM_SECTION_COMMENT);
                    config.setComment(MESSAGES_SECTION, MESSAGES_SECTION_COMMENT);
                    configValues.forEach(configValue -> configValue.save(config));
                    config.save();
                    logger.info("Saved default TelegramBridge config");
                } catch (Exception ex) {
                    logger.error("Failed to save default TelegramBridge config", ex);
                }
            }
        }

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

    private <T> ConfigValue<T> registerConfigOption(ConfigOption<T> configOption) {
        ConfigValue<T> configValue = new ConfigValue<>(configOption);
        configValues.add(configValue);
        return configValue;
    }

    private static class ConfigValue<T> {
        private final ConfigOption<T> configOption;
        private final String configKey;
        private T value;

        public ConfigValue(ConfigOption<T> configOption) {
            this.configOption = configOption;
            this.configKey = String.format("%s.%s", configOption.category(), configOption.name());
            this.value = configOption.defaultValue();
        }

        public T get() {
            return value != null ? value : configOption.defaultValue();
        }

        public void load(CommentedFileConfig commentedFileConfig) {
            Object value = commentedFileConfig.get(configKey);
            if (!configOption.defaultValue().getClass().isInstance(value)) {
                value = configOption.defaultValue();
            }
            //noinspection unchecked
            this.value = (T) value;
        }

        public void save(CommentedFileConfig commentedFileConfig) {
            commentedFileConfig.setComment(configKey, String.join("\n", configOption.comment()));
            commentedFileConfig.set(configKey, get());
        }
    }
}
