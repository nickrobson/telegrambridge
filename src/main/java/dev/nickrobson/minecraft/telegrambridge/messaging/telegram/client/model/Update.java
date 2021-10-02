package dev.nickrobson.minecraft.telegrambridge.messaging.telegram.client.model;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nullable;

public class Update {
    @SerializedName("update_id")
    public long updateId;

    @Nullable
    @SerializedName("message")
    public Message message;
}
