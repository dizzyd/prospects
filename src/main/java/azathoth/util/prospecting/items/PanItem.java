package azathoth.util.prospecting.items;

import azathoth.util.prospecting.Prospecting;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class PanItem extends BaseItem {
	public static final PanItem INSTANCE = new PanItem();

	public PanItem() {
		super("prospecting_pan");
	}

	@Override
	protected boolean shouldProspect(World world, BlockPos pos) {
		// Pan only works on water blocks
		return world.getBlockState(pos.up(1)).getBlock() == Blocks.WATER;
	}

	@Override
	public void registerRecipe() {
		GameRegistry.addShapedRecipe(getRecipeName(), null, new ItemStack(INSTANCE),
				new Object[] {
					"s s", " s ", 's', Blocks.STONE_SLAB
				});
	}
}
