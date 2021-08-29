package dev.nickrobson.minecraft.telegrambridge.fabric.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.nickrobson.minecraft.telegrambridge.fabric.TelegramBridgeFabricMod.MINECRAFT_CONTROLLER;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {
    private static final Logger logger = LogManager.getLogger(MixinServerPlayNetworkHandler.class);

    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void onJoin(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        MINECRAFT_CONTROLLER.onPlayerJoin(player.getGameProfile().getName());
    }

    @Inject(method = "onDisconnected", at = @At(value = "RETURN"))
    public void onLeave(Text reason, CallbackInfo ci) {
        MINECRAFT_CONTROLLER.onPlayerLeave(player.getGameProfile().getName());
    }

    @Inject(
            method = "handleMessage(Lnet/minecraft/server/filter/TextStream$Message;)V",
            at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Ljava/util/function/Function;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V")
    )
    public void onChatMessage(TextStream.Message message, CallbackInfo ci) {
        String messageText = message.getRaw();
        MINECRAFT_CONTROLLER.onPlayerChat(player.getGameProfile().getName(), messageText);
    }
}
