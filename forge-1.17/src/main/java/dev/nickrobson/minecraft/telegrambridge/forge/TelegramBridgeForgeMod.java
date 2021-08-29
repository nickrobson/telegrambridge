package dev.nickrobson.minecraft.telegrambridge.forge;

import dev.nickrobson.minecraft.telegrambridge.core.minecraft.MinecraftController;
import dev.nickrobson.minecraft.telegrambridge.core.minecraft.MinecraftMessageDispatcher;
import dev.nickrobson.minecraft.telegrambridge.core.telegram.TelegramController;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;
import net.minecraftforge.fmlserverevents.FMLServerStoppingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("telegrambridge")
public class TelegramBridgeForgeMod {
    private static final Logger logger = LogManager.getLogger(TelegramBridgeForgeMod.class);

    private final TelegramController telegramController;
    private final TelegramBridgeForgeModConfiguration configuration;

    public TelegramBridgeForgeMod() {
        ModContainer modContainer = ModLoadingContext.get().getActiveContainer();
        logger.info("Starting up " + modContainer.getModId() + " v" + modContainer.getModInfo().getVersion());

        configuration = new TelegramBridgeForgeModConfiguration();
        configuration.registerConfig();

        MinecraftMessageDispatcher forgeMessageDispatcher = new ForgeMessageDispatcher();
        telegramController = new TelegramController(forgeMessageDispatcher);

        MinecraftController minecraftController = new MinecraftController(telegramController.getTelegramMessageDispatcher());
        MinecraftForge.EVENT_BUS.register(new TelegramBridgeMinecraftListener(minecraftController));
        MinecraftForge.EVENT_BUS.addListener(this::onServerStop);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConfigLoadEvent);

        // This is intended to work as running only on the server
        ModLoadingContext.get().registerExtensionPoint(
                IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(
                        () -> FMLNetworkConstants.IGNORESERVERONLY,
                        (a, b) -> true
                )
        );
    }

    public void onConfigLoadEvent(ModConfigEvent.Loading event) {
        if (event.getConfig().getType() != ModConfig.Type.SERVER) {
            return;
        }

        configuration.onReload();
        telegramController.onReload();
    }

    public void onServerStop(FMLServerStoppingEvent event) {
        telegramController.getTelegramClient().flagServerShutdown();
    }
}
