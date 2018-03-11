package azathoth.util.prospecting.registry;

import azathoth.util.prospecting.Prospecting;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class OreDictCache {
	// [Block b, int meta]: "Name"
	private static HashMap<List, String> name_cache = new HashMap<List, String>();
	private static HashMap<List, Float> value_cache = new HashMap<List, Float>();

	public static boolean isOre(Block b, int meta) {
		return !(getOreName(b, meta) == null);
	}
	
	private static void cacheOre(Block b, int meta) {
		List key = Arrays.asList(b, meta);
		if (!name_cache.containsKey(key) && b != Blocks.STONE && b != Blocks.AIR && b != Blocks.DIRT && b != Blocks.GRASS && b != Blocks.GRAVEL) {
			String name = null;
			float value = 1f;
			ItemStack is = new ItemStack(b, 1, meta);
			if (is.isEmpty())
				return;

			int[] ids = OreDictionary.getOreIDs(is);

			if (ids.length > 0) {
				String dict_name = OreDictionary.getOreName(ids[0]);

				if (dict_name.length() > 3 && dict_name.substring(0, 3).equals("ore")) {
					dict_name = dict_name.substring(3);
					if (dict_name.length() > 4 && dict_name.substring(0, 4).equals("Poor")) {
						dict_name = dict_name.substring(4);
						value = (1f/9f);
					}
				} else if (dict_name.length() > 8 && dict_name.substring(0, 8).equals("denseore")) {
					dict_name = dict_name.substring(8);
					value = 3f;
				}

				String norm = normalizeName(dict_name);

				if (hasNugget(norm)) {
					Prospecting.logger.info("Parsed name: " + norm);
					Prospecting.logger.info("Amount: " + value);
					name_cache.put(key, norm);
					value_cache.put(key, value);
				}
			}
		}
	}

	public static String getOreName(Block b, int meta) {
		cacheOre(b, meta);
		return name_cache.get(Arrays.asList(b, meta));
	}

	public static float getOreValue(Block b, int meta) {
		cacheOre(b, meta);
		return value_cache.get(Arrays.asList(b, meta));
	}

	public static ItemStack getNuggetFromName(String name) {
		if (name.equals("Redstone")) {
			return OreDictionary.getOres("dust" + name).get(0);
		} else {
			return OreDictionary.getOres("nugget" + name).get(0);
		}
	}

	private static boolean hasNugget(String name) {
		if (name.equals("Redstone")) {
			return true;
		} else {
			return OreDictionary.getOres("nugget" + name).size() > 0;
		}
	}

	private static boolean blacklisted(String name) {
		try {
			return name.substring(0, 4).equals("bush") || name.substring(0, 4).equals("Poor") || name.substring(0, 5).equals("dense");
		} catch (StringIndexOutOfBoundsException e) {
			return false;
		}
	}

	public static String normalizeName(String name) {
		if (name.equals("Aluminium") || name.equals("Bauxite")) {
			return "Aluminum";
		} else if (name.equals("Ferrous")) {
			return "Nickel";
		}
		return name;
	}
}
