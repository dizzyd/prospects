package com.dizzyd.prospects.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;

public class OreDictCache {

	private static HashMap<String, ItemStack> oreParticles = new HashMap<>();
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
					// on the item stack. Also ensure that the meta is within bounds of 0..15
					int meta = stack.getItemDamage() % 15;
					IBlockState bs = b.getBlockState().getValidStates().get(meta);
					String normalizedName = normalizeName(name.substring(3));
					oreNames.put(bs, normalizedName);
					if (oreParticle != null) {
						oreParticles.put(normalizedName, oreParticle);
					}
				}
			}
		}
	}

	public static String getOreName(IBlockState bs) {
		return oreNames.get(bs);
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
}
