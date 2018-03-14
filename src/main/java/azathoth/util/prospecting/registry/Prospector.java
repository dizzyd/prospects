package azathoth.util.prospecting.registry;

import azathoth.util.prospecting.Prospecting;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Prospector {
	private static final String IDENTIFIER = "ProspectingData";
	private static HashMap<Integer, ProspectingSavedData> registry = new HashMap<Integer, ProspectingSavedData>();
	private static HashMap<String, Block> flower_registry = new HashMap<String, Block>();

	private static ProspectingSavedData loadAndScan(World world, int cx, int cz) {
		// First, see if we've already got world-data loaded for this dimension
		ProspectingSavedData data = registry.getOrDefault(world.provider.getDimension(), null);
		if (data == null) {
			// No world-data available; load or create it
			data = (ProspectingSavedData) world.getPerWorldStorage().getOrLoadData(ProspectingSavedData.class, IDENTIFIER);
			if (data == null) {
				// No data was ever available; create one
				data = new ProspectingSavedData(world, IDENTIFIER);
				world.getPerWorldStorage().setData(IDENTIFIER, data);
			} else {
				data.setWorld(world);
			}

			registry.put(world.provider.getDimension(), data);
		}

		// Finally, scan the requested chunk
		data.scanChunk(cx, cz);
		return data;
	}

	public static void spawnNugget(World world, BlockPos pos) {
		int cx = pos.getX() >> 4;
		int cz = pos.getZ() >> 4;
		ProspectingSavedData data = loadAndScan(world, cx, cz);

		ItemStack nugget = data.getNugget(cx, cz);
		if (nugget != null) {
			world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY() + 1, pos.getZ(), nugget));
		}
	}

	public static HashMap<String, Float> getOres(World world, int cx, int cz) {
		return loadAndScan(world, cx, cz).getOreCounts(cx, cz);
	}

	public static void registerFlower(String ore, Block f) {
		flower_registry.put(OreDictCache.normalizeName(ore), f);
	}

	public static Block getFlowerBlock(String ore) {
		return flower_registry.get(OreDictCache.normalizeName(ore));
	}

	public static Block getRandomFlowerBlock() {
		return (Block) flower_registry.values().toArray()[ThreadLocalRandom.current().nextInt(0, flower_registry.size())];
	}
}
