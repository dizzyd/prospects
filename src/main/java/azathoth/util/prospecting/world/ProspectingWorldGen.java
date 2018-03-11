package azathoth.util.prospecting.world;

import azathoth.util.prospecting.Prospecting;
import azathoth.util.prospecting.registry.Prospector;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class ProspectingWorldGen implements IWorldGenerator {
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

		// Get a list of ores in this chunk
		Set<String> ores = Prospector.getOres(world, chunkX << 4, chunkZ << 4);
		for (String ore : ores) {
			// If there is a flower block associated with the ore AND we don't have too many flowers on this chunk
			// try to place the flower
			flower = Prospector.getFlowerBlock(ore);
			if (flower != null) {
//				for (int i = 0; i < Prospector.getFlowerCount(world, ore, chunkZ << 4, chunkZ << 4) && i < Prospecting.config.max_flowers; i++) {
					int x = (chunkZ << 4) + random.nextInt(16);
					int z = (chunkZ << 4) + random.nextInt(16);
					placeFlower(world, new BlockPos(x, 64, z), flower);
//				}
				placedFlowers = true;
			}
		}

		// If no legitimate flowers were placed on this chunk, maybe place some false flowers
		if (!placedFlowers && ThreadLocalRandom.current().nextFloat() <= Prospecting.config.flower_false_chance) {
			flower = Prospector.getRandomFlowerBlock();
			if (flower != null) {
				for (int j = 0; j < ThreadLocalRandom.current().nextInt(5) + 1; j++) {
					int x = (chunkZ << 4) + random.nextInt(16);
					int z = (chunkZ << 4) + random.nextInt(16);
					placeFlower(world, new BlockPos(x, 64, z), flower);
				}
			}
		}
	}

	public void placeFlower(World world, BlockPos pos, Block flower) {
		// Find the top-most block pos
		BlockPos top = world.getTopSolidOrLiquidBlock(pos);
		if (top.getY() == -1) {
			return;
		}

		Block surface = world.getBlockState(top.down(1)).getBlock();
		if (surface == Blocks.GRASS || surface == Blocks.DIRT) {
			System.out.println("Placing flower " + flower.toString() + " at: " + top);
			world.setBlockState(top, flower.getDefaultState());
		}
	}
}
