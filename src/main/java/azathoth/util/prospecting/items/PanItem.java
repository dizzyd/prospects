package azathoth.util.prospecting.items;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class PanItem extends ProspectingItem {
	protected PanItem() {
		super();
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitx, float hity, float hitz) {
		if (world.getBlock(x, y + 1, z).equals(Blocks.water)) {
			super.doProspect(player, world, x, y, z);
			return true;
		}
		return false;
	}
}
