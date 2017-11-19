package azathoth.util.prospecting.world;

import azathoth.util.prospecting.Prospecting;
import azathoth.util.prospecting.registry.Prospector;
import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ProspectingWorldGen implements IWorldGenerator {
	protected IndicatorFlowerGenerator generator = new IndicatorFlowerGenerator();

	@Override
	public void generate(Random random, int cx, int cz, World world, IChunkProvider chunk_generator, IChunkProvider chunk_provider) {
		if (world.provider.dimensionId == 0) {
			this.runGenerator(this.generator, world, random, cx, cz);
		}
	}

	private void runGenerator(IndicatorFlowerGenerator generator, World world, Random random, int cx, int cz) {
		if (random.nextFloat() > Prospecting.config.flower_chance)
			return;

		Block flower;
		int c = 0;
		for (String ore : Prospector.getOres(world, cx << 4, cz << 4)) {
			flower = Prospector.getFlowerBlock(ore);
			if (flower != null) {
				for (int i = 0; i < Prospector.getFlowerCount(world, ore, cx << 4, cz << 4) && i < Prospecting.config.max_flowers; i++) {
					int x = (cx << 4) + random.nextInt(16);
					int z = (cz << 4) + random.nextInt(16);
					int y = world.getTopSolidOrLiquidBlock(x, z);
					if (y != -1) {
						generator.generate(world, random, x, y, z, flower);
					}
				}
				c++;
			}
		}

		if (c == 0 && ThreadLocalRandom.current().nextFloat() <= Prospecting.config.flower_false_chance) {
			flower = Prospector.getRandomFlowerBlock();
			if (flower != null) {
				for (int j = 0; j < ThreadLocalRandom.current().nextInt(5) + 1; j++) {
					int x = (cx << 4) + random.nextInt(16);
					int z = (cz << 4) + random.nextInt(16);
					int y = world.getTopSolidOrLiquidBlock(x, z);
					if (y != -1) {
						generator.generate(world, random, x, y, z, flower);
					}
				}
			}
		}
	}
}
