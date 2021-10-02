package dev.nickrobson.minecraft.telegrambridge.mixin;

import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.TextFilter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.nickrobson.minecraft.telegrambridge.TelegramBridgeMod.MINECRAFT_CONTROLLER;

@Mixin(ServerGamePacketListenerImpl.class)
public class MixinServerPlayNetworkHandler {
    @Shadow
    public ServerPlayer player;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void onJoin(MinecraftServer server, Connection connection, ServerPlayer player, CallbackInfo ci) {
        MINECRAFT_CONTROLLER.onPlayerJoin(player.getGameProfile().getName());
    }

    @Inject(method = "disconnect", at = @At(value = "RETURN"))
    public void onLeave(Component component, CallbackInfo ci) {
        MINECRAFT_CONTROLLER.onPlayerLeave(player.getGameProfile().getName());
    }

    @Inject(
            method = "handleChat(Lnet/minecraft/server/network/TextFilter$FilteredText;)V",
            at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/server/players/PlayerList;broadcastMessage(Lnet/minecraft/network/chat/Component;Ljava/util/function/Function;Lnet/minecraft/network/chat/ChatType;Ljava/util/UUID;)V")
    )
    public void onChatMessage(TextFilter.FilteredText message, CallbackInfo ci) {
        String messageText = message.getRaw();
        MINECRAFT_CONTROLLER.onPlayerChat(player.getGameProfile().getName(), messageText);
    }
}
