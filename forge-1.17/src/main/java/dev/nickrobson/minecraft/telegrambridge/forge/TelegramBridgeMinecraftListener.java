package dev.nickrobson.minecraft.telegrambridge.forge;

import dev.nickrobson.minecraft.telegrambridge.core.minecraft.MinecraftController;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmlserverevents.FMLServerStartedEvent;

public class TelegramBridgeMinecraftListener {
    private final MinecraftController minecraftController;

    public TelegramBridgeMinecraftListener(MinecraftController minecraftController) {
        this.minecraftController = minecraftController;
    }

    @SubscribeEvent
    public void onPlayerChat(ServerChatEvent event) {
        minecraftController.onPlayerChat(event.getUsername(), event.getMessage());
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof Player) {
            Component deathMessageComponent = event.getSource().getLocalizedDeathMessage(event.getEntityLiving());
            String message = deathMessageComponent.getString();
            minecraftController.onPlayerDeath(message);
        }
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        minecraftController.onPlayerJoin(event.getPlayer().getGameProfile().getName());
    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        minecraftController.onPlayerLeave(event.getPlayer().getGameProfile().getName());
    }


    @SubscribeEvent
    public void onServerStartup(FMLServerStartedEvent event) {
        minecraftController.onServerStart();
    }
}
