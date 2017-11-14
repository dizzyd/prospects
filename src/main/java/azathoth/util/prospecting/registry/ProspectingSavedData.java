package azathoth.util.prospecting.registry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.oredict.OreDictionary;

public class ProspectingSavedData extends WorldSavedData {
	private final int cooldown_sec = 1;
	private final int nugget_amt = 5;

	private HashMap<List<Integer>, HashMap<String, Integer>> chunks; // [x, z]: { "ore": amt }
	private HashMap<List<Integer>, Long> cooldown;
	private HashMap<List<Integer>, Integer> nuggets;
	private World world;

	public ProspectingSavedData(World _world, String tag) {
		super(tag);
		world  = _world;
	}

	@Override
	public void readFromNBT(NBTTagCompound t) {
		chunks = new HashMap<List<Integer>, HashMap<String, Integer>>();

		for (Object x : t.func_150296_c()) {
			for (Object z : t.getCompoundTag((String) x).func_150296_c()) {
				List<Integer> chunk = Arrays.asList(Integer.valueOf((String) x), Integer.valueOf((String) z));

				HashMap<String, Integer> ores = new HashMap<String, Integer>();
				for (Object ore : t.getCompoundTag((String) x).getCompoundTag((String) z).func_150296_c()) {
					if (!((String) ore).equals("cooldown") && !((String) ore).equals("nuggets")) {
						ores.put((String) ore, t.getCompoundTag((String) x).getCompoundTag((String) z).getInteger((String) ore));
					}
				}

				chunks.put(chunk, ores);
				cooldown.put(chunk, t.getCompoundTag((String) x).getCompoundTag((String) z).getLong("cooldown"));
				nuggets.put(chunk, t.getCompoundTag((String) x).getCompoundTag((String) z).getInteger("nuggets"));
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound t) {
		for (Map.Entry<List<Integer>, HashMap<String, Integer>> chunk : chunks.entrySet()) {
			int x = chunk.getKey().get(0);
			int z = chunk.getKey().get(1);

			NBTTagCompound x_tag = new NBTTagCompound();
			NBTTagCompound z_tag = new NBTTagCompound();

			for (Map.Entry<String, Integer> ore : chunk.getValue().entrySet()) {
				z_tag.setInteger(ore.getKey(), ore.getValue());
			}
			z_tag.setLong("cooldown", cooldown.get(chunk));
			z_tag.setInteger("nuggets", nuggets.get(chunk));

			x_tag.setTag(Integer.toString(z), z_tag);
			t.setTag(Integer.toString(x), x_tag);
		}
	}

	public boolean hasChunk(int x, int z) {
		try {
			 return chunks.get(Arrays.asList(x, z)) != null;
		} catch (NullPointerException e) {
			return false;
		}
	}

	public boolean isStale(int x, int z) {
		try {
			return !hasChunk(x, z) || world.getWorldTime() > cooldown.get(Arrays.asList(x, z));
		} catch (NullPointerException e) {
			return true;
		}
	}

	public boolean hasNuggets(int x, int z) {
		try  {
			return nuggets.get(Arrays.asList(x, z)) > 0;
		} catch (NullPointerException e) {
			return false;
		}
	}
	
	public void scanChunk(int x, int z) {
		Block b;
		HashMap<String, Integer> ores = new HashMap<String, Integer>();
		int total = 0;
		List<Integer> chunk = Arrays.asList(x, z);

		if (isStale(x, z)) {
			for (int i = 1; i <= 256; i++) {
				for (int j = 0; j < 16; j++) {
					for (int k = 0; k < 16; k++) {
						b = world.getBlock(x + j, i, z + k);
						if (!b.equals(Blocks.air)) {
							String name = OreDictCache.getOreName(b, b.getDamageValue(world, x + j , i, z + k));
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
							total++;
						}
					}
				}
			}

			chunks.put(chunk, ores);
			cooldown.put(chunk, world.getWorldTime() + (20 * cooldown_sec));
			nuggets.put(chunk, nugget_amt);

			markDirty();
		}
	}

	// gets a nugget for chunk <x, z> and decrements that chunk's nugget count
	public ItemStack getNugget(int x, int z) {
		scanChunk(x, z); // make sure we're dealing with an initialized chunk

		if (hasNuggets(x, z)) {
			List<Integer> chunk = Arrays.asList(x, z);

			int total = 0;
			for (Map.Entry<String, Integer> o : chunks.get(chunk).entrySet()) {
				total += o.getValue();
			}

			int r = ThreadLocalRandom.current().nextInt(0, total + 1);
			int c = 0;
			ItemStack nugget = null;
			for (Map.Entry<String, Integer> o : chunks.get(chunk).entrySet()) {
				c += o.getValue();
				if (c >= r) {
					List<ItemStack> nugs = OreDictionary.getOres("nugget" + o.getKey());
					if (nugs.size() > 0) {
						nugget = new ItemStack(nugs.get(0).getItem(), 1, nugs.get(0).getItemDamage());
						break;
					}
				}
			}

			nuggets.put(chunk, nuggets.get(chunk) - 1);
			return nugget; // this is where we calculate a nugget
		} else {
			return null;
		}
	}
}
