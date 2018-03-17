package com.dizzyd.prospects.items;

import com.dizzyd.prospects.Prospects;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class PanItem extends BaseItem {

	public static final String NAME = "prospecting_pan";

	@GameRegistry.ObjectHolder(Prospects.MODID + ":" + NAME)
	public static PanItem INSTANCE;

	public PanItem() {
		super(NAME);
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
