package com.dizzyd.prospects.items;

import com.dizzyd.prospects.Prospects;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SifterItem extends BaseItem {

	public static final String NAME = "sifter";

	@GameRegistry.ObjectHolder(Prospects.MODID + ":" + NAME)
	public static SifterItem INSTANCE;

	public SifterItem() {
		super(NAME);
	}

	@Override
	protected boolean shouldProspect(World world, BlockPos pos) {
		Block b = world.getBlockState(pos).getBlock();
		return (b == Blocks.GRAVEL || b == Blocks.DIRT || b == Blocks.SAND);
	}

	@Override
	public void registerRecipe() {
		GameRegistry.addShapedRecipe(getRecipeName(), null, new ItemStack(INSTANCE),
				new Object[]{
						"s s", "s#s", "s#s", 's', Items.STICK, '#', Items.STRING
				});

	}
}
