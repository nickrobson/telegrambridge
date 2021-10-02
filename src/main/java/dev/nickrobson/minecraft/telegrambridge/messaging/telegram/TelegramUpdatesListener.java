package dev.nickrobson.minecraft.telegrambridge.messaging.telegram;

import dev.nickrobson.minecraft.telegrambridge.config.TelegramBridgeConfig;
import dev.nickrobson.minecraft.telegrambridge.messaging.MessageDispatcher;
import dev.nickrobson.minecraft.telegrambridge.messaging.MessagePlaceholders;
import dev.nickrobson.minecraft.telegrambridge.messaging.telegram.client.TelegramUpdateListener;
import dev.nickrobson.minecraft.telegrambridge.messaging.telegram.client.model.Update;
import dev.nickrobson.minecraft.telegrambridge.messaging.telegram.client.model.User;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TelegramUpdatesListener implements TelegramUpdateListener {
    private final MessageDispatcher messageDispatcher;

    public TelegramUpdatesListener(MessageDispatcher messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
    }

    @Override
    public void processUpdates(List<Update> updates) {
        TelegramBridgeConfig config = TelegramBridgeConfig.getConfig();
        if (!config.messages.isChatMessageRelayEnabled) {
            return;
        }

        for (Update update : updates) {
            if (update.message != null && update.message.text != null) {
                String messageText = config.messages.minecraftChatMessageFormat
                        .replaceAll(MessagePlaceholders.PLACEHOLDER_USERNAME, update.message.from.username == null ? getDisplayName(update.message.from) : update.message.from.username)
                        .replaceAll(MessagePlaceholders.PLACEHOLDER_NAME, getDisplayName(update.message.from))
                        .replaceAll(MessagePlaceholders.PLACEHOLDER_MESSAGE, update.message.text);

                messageDispatcher.sendMessage(messageText);
            }
        }
    }

    private String getDisplayName(User user) {
        String displayName = Stream.of(user.firstName, user.lastName)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
        return displayName.length() != 0 ? displayName : "Unknown user";
    }
}
