package com.dizzyd.prospects.world;

import com.dizzyd.prospects.Prospects;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;

public class OreDictCache {

	private static HashMap<String, ItemStack> oreParticles = new HashMap<>();
	private static HashMap<Integer, String> oreNames = new HashMap<>();

	private static String[] knownOres =
		new String[]{"Coal", "Iron", "Gold", "Copper", "Lead", "Silver", "Uranium", "Nickel", "Ferrous", "Diamond", "Redstone", "Tin", "Aluminum", "Bauxite"};

	public static void init() {
		// Special check for Geolosys in OreDict; we can't use the item->block transformation with this mod
		// because it uses an abstraction of BlockStates->ore drops. Thus, I had to manually code the block/metas
		// in.
		if (OreDictionary.doesOreNameExist("oreBlockHematite")) {
			initGeolosysOres();
		}

		for (String oreBase : knownOres) {
			if (OreDictionary.doesOreNameExist("ore" + oreBase)) {
				registerOre(oreBase, "ore" + oreBase);
			}
		}
		Prospects.logger.info("All done registering!");
	}

	public static void registerOre(String base, String oreDictName) {
			// Identify the item stack we'll be using when prospecting; check first for a nugget
			// then a dust.
			String nuggetId = "nugget" + base;
			String dustId = "dust" + base;
			ItemStack oreParticle = null;
			if (OreDictionary.doesOreNameExist(nuggetId)) {
				oreParticle = getOres(nuggetId);
			} else if (OreDictionary.doesOreNameExist(dustId)) {
				oreParticle = getOres(nuggetId);
			}

			// For each of the ores associated with this ore dictionary entry, we want try and convert back
			// to a block so we can get the registry name which will enable quicker lookups
			for (ItemStack stack : OreDictionary.getOres(oreDictName)) {
				Block b = Block.getBlockFromItem(stack.getItem());

				// Geolosys also registers an entry in the ore dict, but it doesn't map to a specific block
				if (b == Blocks.AIR) {
					continue;
				}

				// Determine the appropriate block state for this ore; the meta is passed in the damage field
				// on the item stack. Also ensure that the meta is within bounds of 0..15
				int meta = stack.getItemDamage();
				ImmutableList<IBlockState> states = b.getBlockState().getValidStates();
				if (meta >= states.size()) {
					Prospects.logger.warn("Unexpected meta " + meta + " when processing ore " + oreDictName);
					meta = 0;
				}
				IBlockState bs = states.get(meta);
				String normalizedName = normalizeName(base);
				oreNames.put(Block.getStateId(bs), normalizedName);
				if (oreParticle != null) {
					oreParticles.put(normalizedName, oreParticle);
				}
			}
	}

	public static String getOreName(IBlockState bs) {
		return oreNames.get(Block.getStateId(bs));
	}

	public static ItemStack getParticle(String name) {
		return oreParticles.get(name);
	}

	public static String normalizeName(String name) {
		if (name.equals("Aluminium") || name.equals("Bauxite")) {
			return "Aluminum";
		} else if (name.equals("Ferrous")) {
			return "Nickel";
		}
		return name;
	}

	private static ItemStack getOres(String id) {
		NonNullList<ItemStack> s = OreDictionary.getOres(id);
		if (s.isEmpty()) {
			return null;
		} else {
			return s.get(0);
		}
	}

	private static void initGeolosysOres() {
		oreNames.put(getStateId("geolosys:ore_vanilla", 1), "Redstone"); // Cinnabar
		oreNames.put(getStateId("geolosys:ore_vanilla", 2), "Gold"); // Gold
		oreNames.put(getStateId("geolosys:ore_vanilla", 5), "Diamond"); // Kimberlite
		oreNames.put(getStateId("geolosys:ore", 0), "Iron"); // Hematite
		oreNames.put(getStateId("geolosys:ore", 1), "Nickel"); // Limonite
		oreNames.put(getStateId("geolosys:ore", 2), "Copper"); // Malachite
		oreNames.put(getStateId("geolosys:ore", 3), "Copper"); // Azurite
		oreNames.put(getStateId("geolosys:ore", 4), "Tin"); // Cassiterite
		oreNames.put(getStateId("geolosys:ore", 5), "Tin"); // Teallite
		oreNames.put(getStateId("geolosys:ore", 6), "Silver"); // Galena
		oreNames.put(getStateId("geolosys:ore", 6), "Lead"); // Galena
		oreNames.put(getStateId("geolosys:ore", 7), "Aluminum"); // Bauxite
		oreNames.put(getStateId("geolosys:ore", 9), "Uranium"); // Autunite
	}

	private static int getStateId(String name, int meta) {
		return Block.getStateId(Block.getBlockFromName(name).getStateFromMeta(meta));
	}
}
