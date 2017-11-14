package azathoth.util.prospecting.registry;

import azathoth.util.prospecting.Prospecting;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Prospector {
	private static final String IDENTIFIER = "ProspectingData";
	private static HashMap<Integer, ProspectingSavedData> registry = new HashMap<Integer, ProspectingSavedData>();

	public static void logRegistry() {
		// prospecting.logger.info("current registry:");
		// string s = "{";
		// for (map.entry<list, hashmap<string, integer>> e : registry.entryset()) {
		// 	s += e.getkey() + ": {";

		// 	for (Map.Entry<String, Integer> f : e.getValue().entrySet()) {
		// 		s += "\"" + f.getKey() + "\": " + f.getValue() + ", ";
		// 	}

		// 	s += "},";
		// }
		// Prospecting.logger.info(s);
	}

	// Checks if chunk containing coordinates <x, z> has been registered
	public static boolean isRegistered(World world, int x, int z) {
		try {
			return registry.get(world.provider.dimensionId).hasChunk(x >> 4 << 4, z >> 4 << 4);
		} catch (NullPointerException e) {
			return false;
		}
	}

	public static void register(World world, int x, int z) {
		if (!registry.containsKey(world.provider.dimensionId)) {
			registry.put(world.provider.dimensionId, loadOrCreateData(world));
		}

		registry.get(world.provider.dimensionId).scanChunk(x >> 4 << 4, z >> 4 << 4);
	}

	private static ProspectingSavedData loadOrCreateData(World world) {
		ProspectingSavedData data = (ProspectingSavedData) world.perWorldStorage.loadData(ProspectingSavedData.class, IDENTIFIER);

		if (data == null) {
			data = new ProspectingSavedData(world, IDENTIFIER);
			world.perWorldStorage.setData(IDENTIFIER, data);
		}

		return data;
	}
	
	public static void spawnNugget(World world, int x, int y, int z) {
		register(world, x, z);

		ItemStack nugget = registry.get(world.provider.dimensionId).getNugget(x >> 4 << 4, z >> 4 << 4);
		if (nugget != null) {
			world.spawnEntityInWorld(new EntityItem(world, x, y + 1, z, nugget));
		}
	}
}
