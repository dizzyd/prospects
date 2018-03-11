package azathoth.util.prospecting.registry;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.List;

public class OreDictCache {

	private static HashMap<IBlockState, ItemStack> oresParticles = new HashMap<>();
	private static HashMap<IBlockState, String> oreNames = new HashMap<>();

	public static void init() {
		// Walk over the ore dictionary and identify ores and (if available) which ones are prospectable
		for (String name : OreDictionary.getOreNames()) {
			if (name.startsWith("ore")) {
				// Identify the item stack we'll be using when prospecting; check first for a nugget
				// then a dust.
				String nuggetId = "nugget" + name.substring(3);
				String dustId = "dust" + name.substring(3);
				ItemStack oreParticle = null;
				if (OreDictionary.doesOreNameExist(nuggetId)) {
					oreParticle = OreDictionary.getOres(nuggetId).get(0);
				} else if (OreDictionary.doesOreNameExist(dustId)) {
					oreParticle = OreDictionary.getOres(dustId).get(0);
				}

				// For each of the ores associated with this ore dictionary entry, we want try and convert back
				// to a block so we can get the registry name which will enable quicker lookups
				for (ItemStack stack : OreDictionary.getOres(name)) {
					Block b = Block.getBlockFromItem(stack.getItem());

					// Determine the appropriate block state for this ore; the meta is passed in the damage field
					// on the item stack (?!?!)
					IBlockState bs = b.getBlockState().getValidStates().get(stack.getItemDamage());
					if (oreParticle != null) {
						oresParticles.put(bs, oreParticle);
					}
					oreNames.put(bs, normalizeName(name.substring(3)));
				}
			}
		}
	}

	public static String getOreName(IBlockState bs) {
		return oreNames.get(bs);
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

	public static String normalizeName(String name) {
		if (name.equals("Aluminium") || name.equals("Bauxite")) {
			return "Aluminum";
		} else if (name.equals("Ferrous")) {
			return "Nickel";
		}
		return name;
	}
}
