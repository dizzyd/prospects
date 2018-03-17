package com.dizzyd.prospects.proxy;

import com.dizzyd.prospects.blocks.BlockFlower;
import com.dizzyd.prospects.items.PanItem;
import com.dizzyd.prospects.items.PickItem;
import com.dizzyd.prospects.items.SifterItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class CommonProxy {

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		event.getRegistry().register(new BlockFlower());
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		BlockFlower.INSTANCE.registerItems(event.getRegistry());

		event.getRegistry().registerAll(new PanItem(), new PickItem(), new SifterItem());
	}
}
