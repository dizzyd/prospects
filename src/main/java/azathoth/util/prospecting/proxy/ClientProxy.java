package azathoth.util.prospecting.proxy;

import azathoth.util.prospecting.blocks.BlockIndicatorFlower;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		for (BlockIndicatorFlower f: BlockIndicatorFlower.FLOWERS) {
			f.initModel();
		}
	}
}
