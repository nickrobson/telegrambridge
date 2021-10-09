package dev.nickrobson.minecraft.telegrambridge.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.nickrobson.minecraft.telegrambridge.TelegramBridgeMod.MINECRAFT_CONTROLLER;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {
    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        MINECRAFT_CONTROLLER.onPlayerJoin(player.getGameProfile().getName());
    }

    @Inject(method = "remove", at = @At(value = "TAIL"))
    public void onPlayerDisconnect(ServerPlayerEntity player, CallbackInfo ci) {
        MINECRAFT_CONTROLLER.onPlayerLeave(player.getGameProfile().getName());
    }
}
