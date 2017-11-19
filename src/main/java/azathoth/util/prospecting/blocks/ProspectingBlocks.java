package azathoth.util.prospecting.blocks;

import azathoth.util.prospecting.Prospecting;
import azathoth.util.prospecting.registry.Prospector;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;

public final class ProspectingBlocks {
	// Flowers
	public static final Block affine = new BlockIndicatorFlower();
	public static final Block camellia = new BlockIndicatorFlower();
	public static final Block clover = new BlockIndicatorFlower();
	public static final Block hauman = new BlockIndicatorFlower();
	public static final Block horsetail = new BlockIndicatorFlower();
	public static final Block leadplant = new BlockIndicatorFlower();
	public static final Block malva = new BlockIndicatorFlower();
	public static final Block mustard = new BlockIndicatorFlower();
	public static final Block poorjoe = new BlockIndicatorFlower();
	public static final Block primrose = new BlockIndicatorFlower();
	public static final Block shrub_violet = new BlockIndicatorFlower();
	public static final Block vallozia = new BlockIndicatorFlower();

	public static final Block flame_lily = new BlockIndicatorFlowerDouble();
	public static final Block tansy = new BlockIndicatorFlowerDouble();

	public static final void init() {
		affine.setBlockName("Affine").setBlockTextureName(Prospecting.MODID + ":affine");
		GameRegistry.registerBlock(affine, "affine");
		Prospector.registerFlower("Aluminum", affine);

		camellia.setBlockName("Camellia").setBlockTextureName(Prospecting.MODID + ":camellia");
		GameRegistry.registerBlock(camellia, "camellia");
		Prospector.registerFlower("Flourite", camellia);

		clover.setBlockName("RedClover").setBlockTextureName(Prospecting.MODID + ":clover");
		GameRegistry.registerBlock(clover, "clover");
		Prospector.registerFlower("Zinc", clover);

		hauman.setBlockName("Hauman").setBlockTextureName(Prospecting.MODID + ":hauman");
		GameRegistry.registerBlock(hauman, "hauman");
		Prospector.registerFlower("Copper", hauman);

		horsetail.setBlockName("Horsetail").setBlockTextureName(Prospecting.MODID + ":horsetail");
		GameRegistry.registerBlock(horsetail, "horsetail");
		Prospector.registerFlower("Gold", horsetail);

		leadplant.setBlockName("Leadplant").setBlockTextureName(Prospecting.MODID + ":leadplant");
		GameRegistry.registerBlock(leadplant, "leadplant");
		Prospector.registerFlower("Lead", leadplant);

		malva.setBlockName("Malva").setBlockTextureName(Prospecting.MODID + ":malva");
		GameRegistry.registerBlock(malva, "malva");
		Prospector.registerFlower("Cadmium", malva);

		mustard.setBlockName("IndianMustard").setBlockTextureName(Prospecting.MODID + ":mustard");
		GameRegistry.registerBlock(mustard, "mustard");
		Prospector.registerFlower("Silver", mustard);

		poorjoe.setBlockName("Poorjoe").setBlockTextureName(Prospecting.MODID + ":poorjoe");
		GameRegistry.registerBlock(poorjoe, "poorjoe");
		Prospector.registerFlower("Iron", poorjoe);

		primrose.setBlockName("Primrose").setBlockTextureName(Prospecting.MODID + ":primrose");
		GameRegistry.registerBlock(primrose, "primrose");
		Prospector.registerFlower("Uranium", primrose);

		shrub_violet.setBlockName("ShrubViolet").setBlockTextureName(Prospecting.MODID + ":shrub_violet");
		GameRegistry.registerBlock(shrub_violet, "shrub_violet");
		Prospector.registerFlower("Nickel", shrub_violet);

		vallozia.setBlockName("Vallozia").setBlockTextureName(Prospecting.MODID + ":vallozia");
		GameRegistry.registerBlock(vallozia, "vallozia");
		Prospector.registerFlower("Diamond", vallozia);

		flame_lily.setBlockName("FlameLily").setBlockTextureName(Prospecting.MODID + ":flame_lily");
		GameRegistry.registerBlock(flame_lily, "flame_lily");
		Prospector.registerFlower("Redstone", flame_lily);

		tansy.setBlockName("Tansy").setBlockTextureName(Prospecting.MODID + ":tansy");
		GameRegistry.registerBlock(tansy, "tansy");
		Prospector.registerFlower("Tin", tansy);
	}
}
