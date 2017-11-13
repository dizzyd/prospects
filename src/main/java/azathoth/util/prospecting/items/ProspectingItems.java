package azathoth.util.prospecting.items;

import azathoth.util.prospecting.Prospecting;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public final class ProspectingItems {

	public static Item prospecting_pan;
	public static Item prospecting_pick;
	public static Item sifting_pan;

	public static final void init() {
		prospecting_pan = new PanItem().setUnlocalizedName("prospecting_pan").setTextureName(Prospecting.MODID + ":pan");
		GameRegistry.registerItem(prospecting_pan, "prospecting_pan");

		prospecting_pick = new PickItem().setUnlocalizedName("prospecting_pick").setTextureName(Prospecting.MODID + ":pick");
		GameRegistry.registerItem(prospecting_pick, "prospecting_pick");

		sifting_pan = new SifterItem().setUnlocalizedName("sifting_pan").setTextureName(Prospecting.MODID + ":sifter");
		GameRegistry.registerItem(sifting_pan, "sifting_pan");
	}
}
