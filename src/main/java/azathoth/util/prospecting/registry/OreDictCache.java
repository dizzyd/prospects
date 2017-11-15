package azathoth.util.prospecting.registry;

import azathoth.util.prospecting.Prospecting;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class OreDictCache {
	// [Block b, int meta]: "Name"
	private static HashMap<List, String> cache = new HashMap<List, String>();

	public static boolean isOre(Block b, int meta) {
		return !(getOreName(b, meta) == null);
	}

	public static String getOreName(Block b, int meta) {
		List key = Arrays.asList(b, meta);
		if (!cache.containsKey(key)) {
			String name = null;
			int[] ids = OreDictionary.getOreIDs(new ItemStack(b, 1, meta));
			if (ids.length > 0) {
				String dict_name = OreDictionary.getOreName(ids[0]);
				if (dict_name.length() > 3 && dict_name.substring(0, 3).equals("ore") && hasNugget(dict_name.substring(3))) {
					name = dict_name.substring(3);
					Prospecting.logger.debug("Parsed name: " + name);
				}
			}
			cache.put(key, name);
		}

		return cache.get(key);
	}

	public static ItemStack getNuggetFromName(String name) {
		return OreDictionary.getOres("nugget" + name).get(0);
	}

	private static boolean hasNugget(String name) {
		return OreDictionary.getOres("nugget" + name).size() > 0;
	}

	private static boolean blacklisted(String name) {
		try {
			return name.substring(0, 4).equals("bush") || name.substring(0, 4).equals("Poor") || name.substring(0, 5).equals("dense");
		} catch (StringIndexOutOfBoundsException e) {
			return false;
		}
	}
}
