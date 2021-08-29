package dev.nickrobson.minecraft.telegrambridge.fabric;

import dev.nickrobson.minecraft.telegrambridge.core.minecraft.MinecraftController;
import dev.nickrobson.minecraft.telegrambridge.core.minecraft.MinecraftMessageDispatcher;
import dev.nickrobson.minecraft.telegrambridge.core.telegram.TelegramController;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicReference;

public class TelegramBridgeFabricMod implements DedicatedServerModInitializer {
    private static final Logger logger = LogManager.getLogger(TelegramBridgeFabricMod.class);

    public static MinecraftController MINECRAFT_CONTROLLER = null;
    public static final AtomicReference<MinecraftServer> MINECRAFT_SERVER = new AtomicReference<>();

    private final TelegramController telegramController;
    private final TelegramBridgeFabricModConfiguration configuration;

    public TelegramBridgeFabricMod() {
        configuration = new TelegramBridgeFabricModConfiguration();

        MinecraftMessageDispatcher fabricMessageDispatcher = new FabricMessageDispatcher();
        telegramController = new TelegramController(fabricMessageDispatcher);
        MINECRAFT_CONTROLLER = new MinecraftController(telegramController.getTelegramMessageDispatcher());
    }

    @Override
    public void onInitializeServer() {
        logger.info("Starting up TelegramBridge");

        configuration.loadConfig();
        telegramController.onReload();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            logger.info("Server started");
            MINECRAFT_SERVER.set(server);
            MINECRAFT_CONTROLLER.onServerStart();
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            logger.info("Server stopping...");
            MINECRAFT_SERVER.set(null);
            telegramController.getTelegramClient().flagServerShutdown();
        });
    }
}
