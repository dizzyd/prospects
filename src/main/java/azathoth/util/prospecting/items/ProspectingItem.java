package azathoth.util.prospecting.items;

import azathoth.util.prospecting.Prospecting;
import azathoth.util.prospecting.registry.ProspectingRegistry;
import azathoth.util.prospecting.registry.Prospector;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public abstract class ProspectingItem extends Item {
	protected ProspectingItem() {
		super();
	}

	public abstract boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitx, float hity, float hitz);
	
	protected void doProspect(EntityPlayer player, World world, int x, int y, int z) {
		if (!world.isRemote) {
			Prospecting.logger.info("Prospecting...");

			ProspectingRegistry.register(world, x, z);
			ProspectingRegistry.logRegistry();

			Prospector.pumpNugs(player, world, x, y, z);
		}
	}
}
