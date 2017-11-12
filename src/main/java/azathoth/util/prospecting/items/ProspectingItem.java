package azathoth.util.prospecting.items;

import azathoth.util.prospecting.Prospecting;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ProspectingPickItem extends Item {
	private static HashMap<Block, int[]> ore_dict_cache = new HashMap<Block, int[]>();

	/*
	 * {
	 *   chunk_x: {
	 *     chunk_z: {
	 *       "Iron": 10,
	 *       "Copper": 20,
	 *       ...
	 *     },
	 *     ...
	 *   },
	 *   ...
	 * }
	 */
	private static HashMap<List, HashMap<String, Integer>> registry = new HashMap<List, HashMap<String, Integer>>();

	private static HashMap<List, Long> cooldown = new HashMap<List, Long>();
	private static HashMap<List, Long> nug_cooldown = new HashMap<List, Long>();

	private static int nug_count = 3;

	private static int cooldown_sec = 10;
	private static int nug_cooldown_sec = 5;

	protected ProspectingPickItem() {
		super();
	}

	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitx, float hity, float hitz) {
		if (!world.isRemote) {
			int cx = x >> 4 << 4;
			int cz = z >> 4 << 4;
			Block b;
			int[] ids;
			String name;

			Prospecting.logger.info("Prospecting...");

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

			List chunk = Arrays.asList(x >> 4, z >> 4);
			Prospecting.logger.info("Chunk: " + chunk);

			if (registry.containsKey(chunk) && cooldown.containsKey(chunk) && world.getWorldTime() < cooldown.get(chunk)) {
					Prospecting.logger.info("This chunk has already been prospected recently.");
			} else {

			HashMap<String, Integer> tmp = new HashMap<String, Integer>();

			Prospecting.logger.info("Scanning chunk " + chunk + "...");

				for (int i = 1; i <= y; i++) {
					for (int j = 0; j < 16; j++) {
						for (int k = 0; k < 16; k++) {
							b = world.getBlock(cx + j, i, cz + k);

							if (!ore_dict_cache.containsKey(b)) {
								ore_dict_cache.put(b, OreDictionary.getOreIDs(new ItemStack(b, 1, b.getDamageValue(world, cx + j, i, cz + k))));
							}

							ids = ore_dict_cache.get(b);
							
							String log_str = "[" + (cx + j) + ", " + i + ", " + (cz + k) + "]: ";

							if (ids.length > 0) {
								name = OreDictionary.getOreName(ids[0]);
								if (name.substring(0, 3).equals("ore")) {
									String o = name.substring(3);
									int amt = 1;
									if (tmp.containsKey(o)) {
										amt += tmp.remove(o);
									}
									tmp.put(o, amt);
								}
							}
						}
					}
				}
				registry.remove(chunk);
				registry.put(chunk, tmp);
				cooldown.remove(chunk);
				cooldown.put(chunk, world.getWorldTime() + (20 * cooldown_sec));
				Prospecting.logger.info("Done.");
			}

			if (registry.containsKey(chunk) && nug_cooldown.containsKey(chunk) && world.getWorldTime() < nug_cooldown.get(chunk)) {
				Prospecting.logger.info("Pumped nug recently, not pumping nug again.");
			} else {
				Prospecting.logger.info("Pumping nug...");
				HashMap<String, Integer> ores = registry.get(chunk);

				int total = 0;
				for (Map.Entry<String, Integer> e : ores.entrySet()) {
					total += e.getValue();
				}

				int pumped_nugs = 0;
				while (pumped_nugs < ThreadLocalRandom.current().nextInt(1, nug_count + 1)) {
					int r = ThreadLocalRandom.current().nextInt(0, total + 1);
					Prospecting.logger.info("Rolled a " + r);
					int c = 0;
					for (Map.Entry<String, Integer> e : ores.entrySet()) {
						c += e.getValue();
						if (c >= r) {
							List<ItemStack> nugs = OreDictionary.getOres("nugget" + e.getKey());
							if (nugs.size() > 0) {
								ItemStack nug = new ItemStack(nugs.get(0).getItem(), 1, nugs.get(0).getItemDamage());
								world.spawnEntityInWorld(new EntityItem(world, x, y + 1, z, nug));
								Prospecting.logger.info("Pumped out a nugget" + e.getKey());
								pumped_nugs++;
								break;
							}
						}
					}
				}
				nug_cooldown.remove(chunk);
				nug_cooldown.put(chunk, world.getWorldTime() + (20 * nug_cooldown_sec));
			}
		}
		return true;
	}
}
