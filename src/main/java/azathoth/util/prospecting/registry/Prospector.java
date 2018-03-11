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

	public static void logChunk(World world, BlockPos pos) {
		register(world, pos.getX(), pos.getZ());
		registry.get(world.provider.getDimension()).logChunk(pos.getX() >> 4, pos.getZ() >> 4);
	}

	// Checks if chunk containing coordinates <x, z> has been registered
	public static boolean isRegistered(World world, int x, int z) {
		try {
			return registry.get(world.provider.getDimension()).hasChunk(x >> 4, z >> 4);
		} catch (NullPointerException e) {
			return false;
		}
	}

	public static void register(World world, int x, int z) {
		if (!registry.containsKey(world.provider.getDimension())) {
			Prospecting.logger.debug("No ProspectingSavedData found for dimension " + world.provider.getDimension() +", loading...");
			registry.put(world.provider.getDimension(), loadOrCreateData(world));
		}

		registry.get(world.provider.getDimension()).scanChunk(x >> 4, z >> 4);
	}

	private static ProspectingSavedData loadOrCreateData(World world) {
		ProspectingSavedData data = (ProspectingSavedData) world.getPerWorldStorage().getOrLoadData(ProspectingSavedData.class, IDENTIFIER);

		if (data == null) {
			Prospecting.logger.debug("No saved data found in world file, creating...");
			data = new ProspectingSavedData(world, IDENTIFIER);
			world.getPerWorldStorage().setData(IDENTIFIER, data);
		} else {
			data.setWorld(world);
		}

		return data;
	}

	public static void spawnNugget(World world, BlockPos pos) {
		register(world, pos.getX(), pos.getZ());

		ItemStack nugget = registry.get(world.provider.getDimension()).getNugget(pos.getX() >> 4, pos.getZ() >> 4);
		Prospecting.logger.info("nugget: " + nugget);
		if (nugget != null) {
			world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY() + 1, pos.getZ(), nugget));
		}
	}

	public static Set<String> getOres(World world, int x, int z) {
		register(world, x, z);
		return registry.get(world.provider.getDimension()).getOres(x >> 4, z >> 4);
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

	public static int getFlowerCount(World world, String ore, int x, int z) {
		register(world, x, z);
		return registry.get(world.provider.getDimension()).getFlowerCount(ore, x >> 4, z >> 4);
	}
}
