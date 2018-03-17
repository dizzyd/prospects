package azathoth.util.prospecting.proxy;

import azathoth.util.prospecting.Prospecting;
import azathoth.util.prospecting.blocks.BlockIndicatorFlower;
import azathoth.util.prospecting.items.PanItem;
import azathoth.util.prospecting.items.PickItem;
import azathoth.util.prospecting.items.SifterItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
public class CommonProxy {

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		event.getRegistry().register(Prospecting.FLOWERBLOCK);
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		Prospecting.FLOWERBLOCK.registerItems(event.getRegistry());

		event.getRegistry().registerAll(PanItem.INSTANCE, PickItem.INSTANCE, SifterItem.INSTANCE);
	}
}
