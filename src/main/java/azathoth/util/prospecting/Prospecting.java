package azathoth.util.prospecting;

import azathoth.util.prospecting.config.ProspectingConfiguration;
import azathoth.util.prospecting.items.ProspectingItems;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@Mod(modid = Prospecting.MODID, name = Prospecting.NAME, dependencies = "required-after:Forge@[7.0,);", version = Prospecting.VERSION)
public class Prospecting {
	public static final String MODID = "prospecting";
	public static final String NAME = "Prospecting";
	public static final String VERSION = "0.0.1";

	public static ProspectingConfiguration config;

	@Instance(MODID)
	public static Prospecting instance;
	
	public static Logger logger = LogManager.getLogger("Prospecting");

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ProspectingItems.init();
		this.config = new ProspectingConfiguration(event);
	}
}
