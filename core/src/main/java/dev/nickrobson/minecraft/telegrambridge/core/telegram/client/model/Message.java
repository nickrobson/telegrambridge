package dev.nickrobson.minecraft.telegrambridge.core.telegram.client.model;

import com.google.gson.annotations.SerializedName;

public class Message {
    @SerializedName("from")
    public User from;

    @SerializedName("text")
    public String text;
}
