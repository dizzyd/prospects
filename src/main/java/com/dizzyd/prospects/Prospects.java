package com.dizzyd.prospects;

import com.dizzyd.prospects.items.PanItem;
import com.dizzyd.prospects.items.PickItem;
import com.dizzyd.prospects.items.SifterItem;
import com.dizzyd.prospects.proxy.CommonProxy;
import com.dizzyd.prospects.world.OreDictCache;
import com.dizzyd.prospects.world.WorldData;
import com.dizzyd.prospects.world.WorldGen;
import com.google.common.eventbus.Subscribe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Prospects.MODID, name = Prospects.NAME, version = Prospects.VERSION, acceptedMinecraftVersions = "[1.12]", useMetadata = true)
public class Prospects {
	public static final String MODID = "prospects";
	public static final String NAME = "Prospects";
	public static final String VERSION = "0.0.1";

	@SidedProxy(clientSide = "com.dizzyd.prospects.proxy.ClientProxy",
				serverSide = "com.dizzyd.prospects.proxy.CommonProxy")
	public static CommonProxy proxy;

	@Mod.Instance
	public static Prospects instance;

	public static Logger logger = LogManager.getLogger("Prospects");

	public static Config config;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = new Config(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		GameRegistry.registerWorldGenerator(new WorldGen(), 1000);
		PanItem.INSTANCE.registerRecipe();
		PickItem.INSTANCE.registerRecipe();
		SifterItem.INSTANCE.registerRecipe();

		// Register for events so we can handle world loads
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		OreDictCache.init();
	}

	@Mod.EventHandler
	public void onServerStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new Command());
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		WorldData.reconcileMissingChunks(event.getWorld());
	}
}
