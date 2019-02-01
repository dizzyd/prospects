package com.dizzyd.prospects.world;

import com.dizzyd.prospects.Prospects;
import com.dizzyd.prospects.blocks.BlockFlower;
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
		// If the dimension is not in our whitelist, skip generation in it
		if (!Prospects.config.dimension_whitelist.contains(world.provider.getDimension())) {
			return;
		}

		BlockFlower.EnumType flowerType;
		boolean placedFlowers = false;
		int xStart = chunkX << 4;
		int zStart = chunkZ << 4;

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

					int x = xStart + random.nextInt(15);
					int z = zStart + random.nextInt(15);
					placedFlowers |= BlockFlower.INSTANCE.placeFlower(world, x, z, flowerType);
				}
			}
		}

		// If no legitimate flowers were placed on this chunk, maybe place some false flowers
		if (!placedFlowers && Prospects.config.flower_false_chance > 0 && ThreadLocalRandom.current().nextFloat() <= Prospects.config.flower_false_chance) {
			flowerType = (BlockFlower.EnumType)FLOWERS_ARRAY[ThreadLocalRandom.current().nextInt(0, FLOWERS_ARRAY.length)];
			if (flowerType != null) {
				for (int j = 0; j < ThreadLocalRandom.current().nextInt(5) + 1; j++) {
					if (random.nextFloat() > Prospects.config.flower_chance) {
						continue;
					}

					int x = xStart + random.nextInt(15);
					int z = zStart + random.nextInt(15);
					BlockFlower.INSTANCE.placeFlower(world, x, z, flowerType);
				}
			}
		}
	}

	private int getFlowerCount(Float oreAmt) {
		double count = oreAmt / Prospects.config.ore_per_flower;
		return (int)Math.min(Math.round(count), (long) Prospects.config.max_flowers);
	}
}
