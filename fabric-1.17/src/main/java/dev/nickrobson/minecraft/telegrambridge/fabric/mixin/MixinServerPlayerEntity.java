package dev.nickrobson.minecraft.telegrambridge.fabric.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.nickrobson.minecraft.telegrambridge.fabric.TelegramBridgeFabricMod.MINECRAFT_CONTROLLER;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity {
    private static final Logger logger = LogManager.getLogger(MixinServerPlayerEntity.class);

    public MixinServerPlayerEntity(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(
            method = "onDeath",
            at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/entity/damage/DamageTracker;getDeathMessage()Lnet/minecraft/text/Text;")
    )
    public void onDeath(DamageSource source, CallbackInfo ci) {
        Text text = getDamageTracker().getDeathMessage();
        MINECRAFT_CONTROLLER.onPlayerDeath(text.getString());
    }
}
