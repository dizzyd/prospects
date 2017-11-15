package azathoth.util.prospecting.items;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class SifterItem extends ProspectingItem {
	protected SifterItem() {
		super();
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitx, float hity, float hitz) {
		Block b = world.getBlock(x, y, z);
		if (b.equals(Blocks.dirt) || b.equals(Blocks.gravel)) {
			super.doProspect(player, world, x, y, z);
			return true;
		}
		return false;
	}
}
