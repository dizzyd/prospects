package azathoth.util.prospecting.blocks;

import azathoth.util.prospecting.Prospecting;
import azathoth.util.prospecting.registry.Prospector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockIndicatorFlower extends BlockBush {
	
	// Flower instances
	public static final BlockIndicatorFlower AFFINE = new BlockIndicatorFlower("affine", "Aluminum");
	public static final BlockIndicatorFlower CAMELLIA = new BlockIndicatorFlower("camellia", "Flourite");
	public static final BlockIndicatorFlower CLOVER = new BlockIndicatorFlower("clover", "Zinc");
	public static final BlockIndicatorFlower HAUMAN = new BlockIndicatorFlower("hauman", "Copper");
	public static final BlockIndicatorFlower HORSETAIL = new BlockIndicatorFlower("horsetail", "Gold");
	public static final BlockIndicatorFlower LEADPLANT = new BlockIndicatorFlower("leadplant", "Lead");
	public static final BlockIndicatorFlower MALVA = new BlockIndicatorFlower("malva", "Cadmium");
	public static final BlockIndicatorFlower MUSTARD = new BlockIndicatorFlower("mustard", "Silver");
	public static final BlockIndicatorFlower POORJOE = new BlockIndicatorFlower("poorjoe", "Iron");
	public static final BlockIndicatorFlower PRIMROSE = new BlockIndicatorFlower("primrose", "Uranium");
	public static final BlockIndicatorFlower SHRUB_VIOLET = new BlockIndicatorFlower("shrub_violet", "Nickel");
	public static final BlockIndicatorFlower VALLOZIA = new BlockIndicatorFlower("vallozia", "Diamond");

	public static final BlockIndicatorFlower[] FLOWERS = {AFFINE, CAMELLIA, CLOVER, HAUMAN, HORSETAIL, LEADPLANT,
		MALVA, MUSTARD, POORJOE, PRIMROSE, SHRUB_VIOLET, VALLOZIA};

	public BlockIndicatorFlower(String name, String ore) {
		super(Material.PLANTS);
		this.setUnlocalizedName(Prospecting.MODID + "." + name);
		this.setRegistryName(Prospecting.MODID, name);
		Prospector.registerFlower(ore, this);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0,
				new ModelResourceLocation(getRegistryName(), "inventory"));
	}
}
