package dev.nickrobson.minecraft.telegrambridge.core.telegram.client.model;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class User {
    @Nonnull
    @SerializedName("first_name")
    public String firstName = "";

    @Nullable
    @SerializedName("last_name")
    public String lastName;

    @Nullable
    @SerializedName("username")
    public String username;
}
