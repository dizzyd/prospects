package azathoth.util.prospecting.items;

import azathoth.util.prospecting.Prospecting;
import azathoth.util.prospecting.registry.Prospector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PanItem extends Item {

	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.getBlockState(pos).getBlock() == Blocks.WATER) {
			if (!world.isRemote) {
				Prospecting.logger.debug("Prospecting...");
				Prospector.logChunk(world, pos);
				Prospector.spawnNugget(world, pos);
			}
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;

	}
}
