package azathoth.util.prospecting.items;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class PickItem extends BaseItem {
	public static PickItem INSTANCE = new PickItem();

	public PickItem() {
		super("prospecting_pick");
	}

	@Override
	protected boolean shouldProspect(World world, BlockPos pos) {
		return world.getBlockState(pos).getBlock() == Blocks.STONE;
	}
}
