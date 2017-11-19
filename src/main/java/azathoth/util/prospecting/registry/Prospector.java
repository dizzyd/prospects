package azathoth.util.prospecting.registry;

import azathoth.util.prospecting.Prospecting;
import azathoth.util.prospecting.blocks.ProspectingBlocks;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Prospector {
	private static final String IDENTIFIER = "ProspectingData";
	private static HashMap<Integer, ProspectingSavedData> registry = new HashMap<Integer, ProspectingSavedData>();
	private static HashMap<String, Block> flower_registry = new HashMap<String, Block>();

	public static void logChunk(World world, int x, int z) {
		register(world, x, z);
		registry.get(world.provider.dimensionId).logChunk(x >> 4, z >> 4);
	}

	// Checks if chunk containing coordinates <x, z> has been registered
	public static boolean isRegistered(World world, int x, int z) {
		try {
			return registry.get(world.provider.dimensionId).hasChunk(x >> 4, z >> 4);
		} catch (NullPointerException e) {
			return false;
		}
	}

	public static void register(World world, int x, int z) {
		if (!registry.containsKey(world.provider.dimensionId)) {
			Prospecting.logger.debug("No ProspectingSavedData found for dimension " + world.provider.dimensionId +", loading...");
			registry.put(world.provider.dimensionId, loadOrCreateData(world));
		}

		registry.get(world.provider.dimensionId).scanChunk(x >> 4, z >> 4);
		// logChunk(world, x, z);
	}

	private static ProspectingSavedData loadOrCreateData(World world) {
		ProspectingSavedData data = (ProspectingSavedData) world.perWorldStorage.loadData(ProspectingSavedData.class, IDENTIFIER);

		if (data == null) {
			Prospecting.logger.debug("No saved data found in world file, creating...");
			data = new ProspectingSavedData(world, IDENTIFIER);
			world.perWorldStorage.setData(IDENTIFIER, data);
		} else {
			data.setWorld(world);
		}

		return data;
	}

	public static void spawnNugget(World world, int x, int y, int z) {
		register(world, x, z);

		ItemStack nugget = registry.get(world.provider.dimensionId).getNugget(x >> 4, z >> 4);
		Prospecting.logger.info("nugget: " + nugget);
		if (nugget != null) {
			world.spawnEntityInWorld(new EntityItem(world, x, y + 1, z, nugget));
		}
	}

	public static Set<String> getOres(World world, int x, int z) {
		register(world, x, z);
		return registry.get(world.provider.dimensionId).getOres(x >> 4, z >> 4);
	}

	public static void registerFlower(String name, Block f) {
		flower_registry.put(OreDictCache.normalizeName(name), f);
	}

	public static Block getFlowerBlock(String ore) {
		return flower_registry.get(OreDictCache.normalizeName(ore));
	}

	public static Block getRandomFlowerBlock() {
		return (Block) flower_registry.values().toArray()[ThreadLocalRandom.current().nextInt(0, flower_registry.size())];
	}

	public static int getFlowerCount(World world, String ore, int x, int z) {
		register(world, x, z);
		return registry.get(world.provider.dimensionId).getFlowerCount(ore, x >> 4, z >> 4);
	}
}
