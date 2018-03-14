package azathoth.util.prospecting;

import azathoth.util.prospecting.Prospecting;
import azathoth.util.prospecting.registry.Prospector;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class WorldGen implements IWorldGenerator {
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		if (world.provider.getDimension() != 0) {
			return;
		}

		if (random.nextFloat() > Prospecting.config.flower_chance) {
			return;
		}

		Block flower;
		boolean placedFlowers = false;

		// Get a ores and their counts for this chunk
		HashMap<String, Float> ores = Prospector.getOres(world, chunkX, chunkZ);
		for (String ore : ores.keySet()) {
			// If there is a flower block associated with the ore, try to place a flower
			flower = Prospector.getFlowerBlock(ore);
			if (flower != null) {
				for (int i = 0; i < getFlowerCount(ores.get(ore)); i++) {
					int x = (chunkX << 4) + random.nextInt(16);
					int z = (chunkZ << 4) + random.nextInt(16);
					placedFlowers &= placeFlower(world, new BlockPos(x, 64, z), flower);
				}
			}
		}

		// If no legitimate flowers were placed on this chunk, maybe place some false flowers
		if (!placedFlowers && ThreadLocalRandom.current().nextFloat() <= Prospecting.config.flower_false_chance) {
			flower = Prospector.getRandomFlowerBlock();
			if (flower != null) {
				for (int j = 0; j < ThreadLocalRandom.current().nextInt(5) + 1; j++) {
					int x = (chunkX << 4) + random.nextInt(16);
					int z = (chunkZ << 4) + random.nextInt(16);
					placeFlower(world, new BlockPos(x, 64, z), flower);
				}
			}
		}
	}

	private boolean placeFlower(World world, BlockPos pos, Block flower) {
		// Find the top-most block pos
		BlockPos topPos = world.getTopSolidOrLiquidBlock(pos);
		if (topPos.getY() == -1) {
			return false;
		}

		Block surface = world.getBlockState(topPos.down(1)).getBlock();
		Block top = world.getBlockState(topPos).getBlock();
		if ((surface == Blocks.GRASS || surface == Blocks.DIRT) && top == Blocks.AIR) {
			// TODO: Investigate why this is generating cascading world gen on occasion
			world.setBlockState(topPos, flower.getDefaultState());
			return true;
		}

		return false;
	}

	private int getFlowerCount(Float oreAmt) {
		double count = Math.ceil(oreAmt / Prospecting.config.ore_per_flower);
		return (int)Math.min(Math.round(count), (long)Prospecting.config.max_flowers);
	}
}
