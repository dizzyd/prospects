package azathoth.util.prospecting.registry;

import azathoth.util.prospecting.Prospecting;
import net.minecraft.block.Block;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProspectingRegistry {
	private static final ProspectingRegistry instance = new ProspectingRegistry();

	private static HashMap<List, HashMap<String, Integer>> registry = new HashMap<List, HashMap<String, Integer>>();
	private static HashMap<List, Long> cooldown = new HashMap<List, Long>();

	private static int cooldown_time = 60; // seconds

	private ProspectingRegistry() {
		// stub
	}

	public static void logRegistry() {
		Prospecting.logger.info("Current registry:");
		String s = "{";
		for (Map.Entry<List, HashMap<String, Integer>> e : registry.entrySet()) {
			s += e.getKey() + ": {";

			for (Map.Entry<String, Integer> f : e.getValue().entrySet()) {
				s += "\"" + f.getKey() + "\": " + f.getValue() + ", ";
			}

			s += "},";
		}
		Prospecting.logger.info(s);
	}

	public static ProspectingRegistry getInstance() {
		return instance;
	}

	// Checks if chunk containing coordinates <x, z> has been registered
	public static boolean isRegistered(int x, int z) {
		return registry.containsKey(Arrays.asList(x >> 4, z >> 4));
	}

	public static void register(World world, int x, int z) {
		int cx = x >> 4 << 4;
		int cz = z >> 4 << 4;
		Block b;
		HashMap<String, Integer> ores = new HashMap<String, Integer>();
		List chunk = Arrays.asList(x >> 4, z >> 4);

		if (!registry.containsKey(chunk) || !cooldown.containsKey(chunk) || world.getWorldTime() > cooldown.get(chunk)) {
			for (int i = 1; i <= 256; i++) {
				for (int j = 0; j < 16; j++) {
					for (int k = 0; k < 16; k++) {
						b = world.getBlock(cx + j, i, cz + k);

						String name = OreDictCache.getOreName(b, b.getDamageValue(world, cx + j , i, cz + k));
						if (name != null) {
							if (name.substring(0, 3).equals("ore")) {
								String o = name.substring(3);
								int amt = 1;
								if (ores.containsKey(o)) {
									amt += ores.remove(o);
								}
								ores.put(o, amt);
							}
						}
					}
				}
			}
			registry.remove(chunk);
			registry.put(chunk, ores);
			cooldown.remove(chunk);
			cooldown.put(chunk, world.getWorldTime() + (20 * cooldown_time));
		}
	}
	
	// Get ores in the chunk that contains coordinate <x, z>
	public static HashMap<String, Integer> getOres(World world, int x, int z) {
		if (!isRegistered(x, z)) {
			register(world, x, z);
		}

		return registry.get(Arrays.asList(x >> 4, z >> 4));
	}
}
