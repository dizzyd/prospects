package azathoth.util.prospecting.items;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public final class ProspectingItems {

	public static Item prospecting_pan;
	public static Item prospecting_pick;
	public static Item sifting_pan;

	public static final void init() {
		prospecting_pan = new PanItem().setUnlocalizedName("prospecting_pan");
		GameRegistry.registerItem(prospecting_pan, "prospecting_pan");

		prospecting_pick = new PickItem().setUnlocalizedName("prospecting_pick");
		GameRegistry.registerItem(prospecting_pick, "prospecting_pick");

		sifting_pan = new SifterItem().setUnlocalizedName("sifting_pan");
		GameRegistry.registerItem(sifting_pan, "sifting_pan");
	}
}
