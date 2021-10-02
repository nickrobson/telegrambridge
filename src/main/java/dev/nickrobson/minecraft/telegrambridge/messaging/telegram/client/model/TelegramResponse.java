package dev.nickrobson.minecraft.telegrambridge.messaging.telegram.client.model;

import com.google.gson.annotations.SerializedName;

public class TelegramResponse<T> {
    @SerializedName("ok")
    public boolean ok = false;

    @SerializedName("description")
    public String description = null;

    @SerializedName("error_code")
    public Integer errorCode = null;

    @SerializedName("result")
    public T result = null;
}
