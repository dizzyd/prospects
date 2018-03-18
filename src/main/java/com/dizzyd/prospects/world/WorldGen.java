package com.dizzyd.prospects.world;

import com.dizzyd.prospects.Prospects;
import com.dizzyd.prospects.blocks.BlockFlower;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class WorldGen implements IWorldGenerator {

	private static HashMap<String, BlockFlower.EnumType> FLOWERS = new HashMap<String, BlockFlower.EnumType>();
	private static Object[] FLOWERS_ARRAY;

	public static void registerFlower(String ore, BlockFlower.EnumType flowerType) {
		FLOWERS.put(OreDictCache.normalizeName(ore), flowerType);
		FLOWERS_ARRAY = FLOWERS.values().toArray();
	}
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		if (world.provider.getDimension() != 0) {
			return;
		}

		BlockFlower.EnumType flowerType;
		boolean placedFlowers = false;

		// Get a ores and their counts for this chunk
		HashMap<String, Float> ores = WorldData.getOres(world, chunkX, chunkZ);
		for (String ore : ores.keySet()) {
			// If there is a flower block associated with the ore, try to place a flower
			flowerType = FLOWERS.get(ore);
			if (flowerType != null) {
				for (int i = 0; i < getFlowerCount(ores.get(ore)); i++) {
					if (random.nextFloat() > Prospects.config.flower_chance) {
						continue;
					}

					int x = (chunkX << 4)+ random.nextInt(15);
					int z = (chunkZ << 4) + random.nextInt(15);
					placedFlowers &= placeFlower(world, new BlockPos(x, 64, z), flowerType);
				}
			}
		}

		// If no legitimate flowers were placed on this chunk, maybe place some false flowers
		if (!placedFlowers && ThreadLocalRandom.current().nextFloat() <= Prospects.config.flower_false_chance) {
			flowerType = (BlockFlower.EnumType)FLOWERS_ARRAY[ThreadLocalRandom.current().nextInt(0, FLOWERS_ARRAY.length)];
			if (flowerType != null) {
				for (int j = 0; j < ThreadLocalRandom.current().nextInt(5) + 1; j++) {
					if (random.nextFloat() > Prospects.config.flower_chance) {
						continue;
					}

					int x = (chunkX << 4) + random.nextInt(15);
					int z = (chunkZ << 4) + random.nextInt(15);
					placeFlower(world, new BlockPos(x, 64, z), flowerType);
				}
			}
		}
	}

	private boolean placeFlower(World world, BlockPos pos, BlockFlower.EnumType flowerType) {
		// Find the top-most block pos
		BlockPos topPos = world.getTopSolidOrLiquidBlock(pos);
		if (topPos.getY() == -1) {
			return false;
		}

		Block surface = world.getBlockState(topPos.down(1)).getBlock();
		Block top = world.getBlockState(topPos).getBlock();
		if ((surface == Blocks.GRASS || surface == Blocks.DIRT) && top == Blocks.AIR) {
			BlockFlower.INSTANCE.placeAt(world, topPos, flowerType);
			return true;
		}

		return false;
	}

	private int getFlowerCount(Float oreAmt) {
		double count = Math.ceil(oreAmt / Prospects.config.ore_per_flower);
		return (int)Math.min(Math.round(count), (long) Prospects.config.max_flowers);
	}
}
