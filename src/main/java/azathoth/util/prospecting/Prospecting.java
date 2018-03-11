package azathoth.util.prospecting;

import azathoth.util.prospecting.blocks.BlockIndicatorFlower;
import azathoth.util.prospecting.config.ProspectingConfiguration;
import azathoth.util.prospecting.proxy.CommonProxy;
import azathoth.util.prospecting.world.ProspectingWorldGen;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Prospecting.MODID, name = Prospecting.NAME, version = Prospecting.VERSION, acceptedMinecraftVersions = "[1.12]", useMetadata = true)
public class Prospecting {
	public static final String MODID = "prospecting";
	public static final String NAME = "Prospecting";
	public static final String VERSION = "0.0.1";

	@SidedProxy(clientSide = "azathoth.util.prospecting.proxy.ClientProxy", serverSide = "azathoth.util.prospecting.proxy.CommonProxy")
	public static CommonProxy proxy;

	@Mod.Instance
	public static Prospecting instance;

	public static Logger logger = LogManager.getLogger("Prospecting");

	public static ProspectingConfiguration config;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		this.config = new ProspectingConfiguration(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		GameRegistry.registerWorldGenerator(new ProspectingWorldGen(), 1000);
	}
}
