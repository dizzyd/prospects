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
	// [Block b, int meta]: [list of oredict IDs]
	private static HashMap<List, int[]> cache = new HashMap<List, int[]>();

	private OreDictCache() {
		// stub
	}

	public static boolean isOre(Block b, int meta) {
		if (getOreName(b, meta) == null) {
			return false;
		}
		return true;
	}

	public static String getOreName(Block b, int meta) {
		List key = Arrays.asList(b, meta);
		if (!cache.containsKey(key)) {
			cache.put(key, OreDictionary.getOreIDs(new ItemStack(b, 1, meta)));
		}
		
		if (cache.get(key).length == 0) {
			return null;
		} else {
			return OreDictionary.getOreName(cache.get(key)[0]);
		}
	}
}
