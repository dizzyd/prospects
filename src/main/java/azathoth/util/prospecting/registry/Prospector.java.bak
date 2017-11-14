package azathoth.util.prospecting.registry;

import azathoth.util.prospecting.Prospecting;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Prospector {
	private static HashMap<EntityPlayer, Long> cooldown = new HashMap<EntityPlayer, Long>();

	private static int cooldown_sec = 30;
	private static int nug_max = 2;
	private static int nug_min = 0;

	private Prospector() {
		// stub
	}

	public static void pumpNugs(EntityPlayer player, World world, int x, int y, int z) {
		if (!cooldown.containsKey(player) || world.getWorldTime() > cooldown.get(player)) {
			Prospecting.logger.info("Pumping nug...");
			HashMap<String, Integer> ores = ProspectingRegistry.getOres(world, x, z);

			int total = 0;
			for (Map.Entry<String, Integer> e : ores.entrySet()) {
				total += e.getValue();
			}

			int nugs_pumped = 0;
			int pump = ThreadLocalRandom.current().nextInt(nug_min, nug_max + 1);
			while (nugs_pumped < pump) {
				int r = ThreadLocalRandom.current().nextInt(0, total + 1);
				int c = 0;
				for (Map.Entry<String, Integer> e : ores.entrySet()) {
					c += e.getValue();
					if (c >= r) {
						List<ItemStack> nugs = OreDictionary.getOres("nugget" + e.getKey());
						if (nugs.size() > 0) {
							ItemStack nug = new ItemStack(nugs.get(0).getItem(), 1, nugs.get(0).getItemDamage());
							world.spawnEntityInWorld(new EntityItem(world, x, y + 1, z, nug));
							Prospecting.logger.info("Pumped out a nugget" + e.getKey());
							nugs_pumped++;
							break;
						}
					}
				}
			}
			cooldown.remove(player);
			cooldown.put(player, world.getWorldTime() + (20 * cooldown_sec));

		} else {
			Prospecting.logger.info("Pumped nug recently, not pumping nug again.");
		}
	}
}
