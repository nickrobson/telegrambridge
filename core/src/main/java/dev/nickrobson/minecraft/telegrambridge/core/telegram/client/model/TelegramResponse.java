package dev.nickrobson.minecraft.telegrambridge.core.telegram.client.model;

import com.google.gson.annotations.SerializedName;

public class TelegramResponse<T> {
    @SerializedName("ok")
    public boolean ok = false;

    @SerializedName("result")
    public T result = null;
}
