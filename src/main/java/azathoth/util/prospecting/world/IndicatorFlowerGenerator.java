package azathoth.util.prospecting.world;

import azathoth.util.prospecting.blocks.ProspectingBlocks;
import azathoth.util.prospecting.blocks.BlockIndicatorFlowerDouble;
import azathoth.util.prospecting.registry.Prospector;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.World;

import java.util.Random;

public class IndicatorFlowerGenerator {
	public boolean generate(World world, Random random, int x, int y, int z, Block flower) {
		Block b = world.getBlock(x, y-1, z);
		if (b == Blocks.grass || b == Blocks.dirt) {
			world.setBlock(x, y, z, flower);
			if (flower instanceof BlockIndicatorFlowerDouble && y < 255) {
				world.setBlock(x, y+1, z, flower, 1, 1);
			}
			return true;
		}
		return false;
	}
}
