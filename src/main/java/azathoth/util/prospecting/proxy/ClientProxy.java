package azathoth.util.prospecting.proxy;

import azathoth.util.prospecting.Prospecting;
import azathoth.util.prospecting.blocks.BlockIndicatorFlower;
import azathoth.util.prospecting.items.PanItem;
import azathoth.util.prospecting.items.PickItem;
import azathoth.util.prospecting.items.SifterItem;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import sun.jvm.hotspot.opto.Block;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		Prospecting.FLOWERBLOCK.initModel();

		PanItem.INSTANCE.initModel();
		PickItem.INSTANCE.initModel();
		SifterItem.INSTANCE.initModel();
	}
}
