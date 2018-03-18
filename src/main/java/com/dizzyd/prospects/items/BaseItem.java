package com.dizzyd.prospects.items;

import com.dizzyd.prospects.Prospects;
import com.dizzyd.prospects.world.WorldData;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BaseItem extends Item {
	protected BaseItem(String name) {
		setRegistryName(Prospects.MODID, name);
		setUnlocalizedName(Prospects.MODID + "." + name);
		this.setCreativeTab(CreativeTabs.MISC);
	}

	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (shouldProspect(world, pos)) {
			if (!world.isRemote) {
				Prospects.logger.debug("Prospects...");
				WorldData.spawnNugget(world, pos);
			}
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;

	}

	protected abstract boolean shouldProspect(World world, BlockPos pos);

	public abstract void registerRecipe();

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0,
				new ModelResourceLocation(getRegistryName(), "inventory"));
	}

	protected ResourceLocation getRecipeName() {
		return new ResourceLocation(getRegistryName().getResourceDomain() +
				":recipe." + getRegistryName().getResourcePath());
	}
}
