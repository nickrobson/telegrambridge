package dev.nickrobson.minecraft.telegrambridge.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.nickrobson.minecraft.telegrambridge.TelegramBridgeMod.MINECRAFT_CONTROLLER;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayerEntity extends Player {
    public MixinServerPlayerEntity(Level world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(
            method = "die",
            at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/damagesource/CombatTracker;getDeathMessage()Lnet/minecraft/network/chat/Component;")
    )
    public void onDeath(DamageSource source, CallbackInfo ci) {
        Component text = getCombatTracker().getDeathMessage();
        MINECRAFT_CONTROLLER.onPlayerDeath(text.getString());
    }
}
