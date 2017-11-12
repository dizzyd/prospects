package azathoth.util.prospecting.items;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public final class ProspectingItems {

	public static Item prospecting_pick;

	public static final void init() {
		prospecting_pick = new ProspectingPickItem().setUnlocalizedName("prospecting_pick");
		GameRegistry.registerItem(prospecting_pick, "prospecting_pick");
	}
}
