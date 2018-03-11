package azathoth.util.prospecting.proxy;

import azathoth.util.prospecting.blocks.BlockIndicatorFlower;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class CommonProxy {
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(BlockIndicatorFlower.FLOWERS);
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		for (Block f: BlockIndicatorFlower.FLOWERS) {
			event.getRegistry().register(new ItemBlock(f).setRegistryName(f.getRegistryName()));
		}
	}
}
