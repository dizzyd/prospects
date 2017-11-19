package azathoth.util.prospecting.blocks;

import azathoth.util.prospecting.Prospecting;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;

public class BlockIndicatorFlower extends BlockBush {
	
	public BlockIndicatorFlower() {
		super(Material.plants);
		// this.setBlockName("Test Flower");
		// this.setBlockTextureName(Prospecting.MODID + ":test_flower");
		this.setStepSound(soundTypeGrass);
	}
}
