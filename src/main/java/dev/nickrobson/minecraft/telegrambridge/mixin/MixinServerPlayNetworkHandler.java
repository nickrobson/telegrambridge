package dev.nickrobson.minecraft.telegrambridge.mixin;

import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.nickrobson.minecraft.telegrambridge.TelegramBridgeMod.MINECRAFT_CONTROLLER;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(
            method = "handleMessage",
            at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Ljava/util/function/Function;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V")
    )
    public void onChatMessage(TextStream.Message message, CallbackInfo ci) {
        String messageText = message.getRaw();
        MINECRAFT_CONTROLLER.onPlayerChat(player.getGameProfile().getName(), messageText);
    }
}
