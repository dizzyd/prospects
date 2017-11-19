package azathoth.util.prospecting;

import azathoth.util.prospecting.config.ProspectingConfiguration;
import azathoth.util.prospecting.items.ProspectingItems;
import azathoth.util.prospecting.blocks.ProspectingBlocks;
import azathoth.util.prospecting.world.ProspectingWorldGen;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

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
		this.config = new ProspectingConfiguration(event);
		ProspectingItems.init();
		ProspectingBlocks.init();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		GameRegistry.registerWorldGenerator(new ProspectingWorldGen(), 1000);

		GameRegistry.addRecipe(new ItemStack(ProspectingItems.prospecting_pan), new Object[] {"s s", " s ", 's', Blocks.stone_slab});
		GameRegistry.addRecipe(new ItemStack(ProspectingItems.prospecting_pick), new Object[] {"iis", "  s", 'i', Items.iron_ingot, 's', Items.stick});
		GameRegistry.addRecipe(new ItemStack(ProspectingItems.sifting_pan), new Object[] {"s s", "s#s", "s#s", 's', Items.stick, '#', Items.string});
	}
}
