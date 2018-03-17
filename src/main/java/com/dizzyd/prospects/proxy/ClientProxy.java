package com.dizzyd.prospects.proxy;

import com.dizzyd.prospects.blocks.BlockFlower;
import com.dizzyd.prospects.items.PanItem;
import com.dizzyd.prospects.items.PickItem;
import com.dizzyd.prospects.items.SifterItem;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		BlockFlower.INSTANCE.initModel();

		PanItem.INSTANCE.initModel();
		PickItem.INSTANCE.initModel();
		SifterItem.INSTANCE.initModel();
	}
}
