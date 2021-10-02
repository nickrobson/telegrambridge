package dev.nickrobson.minecraft.telegrambridge;

import dev.nickrobson.minecraft.telegrambridge.config.TelegramBridgeConfig;
import dev.nickrobson.minecraft.telegrambridge.messaging.MinecraftMessageDispatcher;
import dev.nickrobson.minecraft.telegrambridge.messaging.MinecraftMessagingController;
import dev.nickrobson.minecraft.telegrambridge.messaging.telegram.TelegramMessagingController;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicReference;

public class TelegramBridgeMod implements DedicatedServerModInitializer {
    private static final Logger logger = LogManager.getLogger(TelegramBridgeMod.class);

    public static final String MOD_ID = "telegrambridge";

    public static MinecraftMessagingController MINECRAFT_CONTROLLER = null;
    public static final AtomicReference<MinecraftServer> MINECRAFT_SERVER = new AtomicReference<>();

    private final TelegramMessagingController telegramMessagingController;

    public TelegramBridgeMod() {
        MinecraftMessageDispatcher minecraftMessageDispatcher = new MinecraftMessageDispatcher();
        telegramMessagingController = new TelegramMessagingController(minecraftMessageDispatcher);
        MINECRAFT_CONTROLLER = new MinecraftMessagingController(telegramMessagingController.getTelegramMessageDispatcher());
    }

    @Override
    public void onInitializeServer() {
        logger.info("({}) Initialising server", MOD_ID);

        TelegramBridgeConfig.registerConfig();
        telegramMessagingController.onReload();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            logger.info("Server started");
            MINECRAFT_SERVER.set(server);
            MINECRAFT_CONTROLLER.onServerStart();
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            logger.info("Server stopping...");
            MINECRAFT_SERVER.set(null);
            telegramMessagingController.getTelegramClient().flagServerShutdown();
        });
    }
}
