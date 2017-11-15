package azathoth.util.prospecting.registry;

import azathoth.util.prospecting.Prospecting;

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
	private HashMap<List<Integer>, HashMap<String, Integer>> chunks; // [x, z]: { "ore": amt }
	private HashMap<List<Integer>, Long> expiry;
	private HashMap<List<Integer>, Integer> nuggets;
	private World world;

	public ProspectingSavedData(World world, String tag) {
		super(tag);
		this.world  = world;
		this.chunks = new HashMap<List<Integer>, HashMap<String, Integer>>();
		this.expiry = new HashMap<List<Integer>, Long>();
		this.nuggets = new HashMap<List<Integer>, Integer>();
	}

	public ProspectingSavedData(String tag) {
		super(tag);
		this.chunks = new HashMap<List<Integer>, HashMap<String, Integer>>();
		this.expiry = new HashMap<List<Integer>, Long>();
		this.nuggets = new HashMap<List<Integer>, Integer>();
	}

	public void setWorld(World world) {
		this.world = world;
	}

	@Override
	public void readFromNBT(NBTTagCompound t) {
		Prospecting.logger.debug("Reading from NBT...");
		this.chunks = new HashMap<List<Integer>, HashMap<String, Integer>>();
		this.expiry = new HashMap<List<Integer>, Long>();
		this.nuggets = new HashMap<List<Integer>, Integer>();

		for (Object x : t.func_150296_c()) {
			for (Object z : t.getCompoundTag((String) x).func_150296_c()) {
				List<Integer> chunk = Arrays.asList(Integer.valueOf((String) x), Integer.valueOf((String) z));

				HashMap<String, Integer> ores = new HashMap<String, Integer>();
				for (Object ore : t.getCompoundTag((String) x).getCompoundTag((String) z).func_150296_c()) {
					if (!((String) ore).equals("expiry") && !((String) ore).equals("nuggets")) {
						ores.put((String) ore, t.getCompoundTag((String) x).getCompoundTag((String) z).getInteger((String) ore));
					}
				}

				this.chunks.put(chunk, ores);
				this.expiry.put(chunk, t.getCompoundTag((String) x).getCompoundTag((String) z).getLong("expiry"));
				this.nuggets.put(chunk, t.getCompoundTag((String) x).getCompoundTag((String) z).getInteger("nuggets"));
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound t) {
		Prospecting.logger.debug("Writing to NBT...");
		for (Map.Entry<List<Integer>, HashMap<String, Integer>> chunk : this.chunks.entrySet()) {
			int x = chunk.getKey().get(0);
			int z = chunk.getKey().get(1);

			Prospecting.logger.debug("Writing chunk " + chunk.getKey().toString() + "...");

			NBTTagCompound x_tag = new NBTTagCompound();
			NBTTagCompound z_tag = new NBTTagCompound();

			for (Map.Entry<String, Integer> ore : chunk.getValue().entrySet()) {
				z_tag.setInteger(ore.getKey(), ore.getValue());
			}
			Prospecting.logger.debug("Cooldown: " + this.expiry.get(chunk.getKey()));
			Prospecting.logger.debug("Nuggets: " + this.nuggets.get(chunk.getKey()));
			z_tag.setLong("expiry", this.expiry.get(chunk.getKey()));
			z_tag.setInteger("nuggets", this.nuggets.get(chunk.getKey()));

			x_tag.setTag(Integer.toString(z), z_tag);
			t.setTag(Integer.toString(x), x_tag);
		}
	}

	public boolean hasChunk(int x, int z) {
		try {
			 return this.chunks.get(Arrays.asList(x, z)) != null;
		} catch (NullPointerException e) {
			return false;
		}
	}

	public boolean isStale(int x, int z) {
		try {
			return !hasChunk(x, z) || world.getWorldTime() > this.expiry.get(Arrays.asList(x, z));
		} catch (NullPointerException e) {
			return true;
		}
	}

	public boolean hasNuggets(int x, int z) {
		try  {
			return this.nuggets.get(Arrays.asList(x, z)) > 0;
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
			Prospecting.logger.debug("Scanning chunk [" + x + ", " + z + "]...");
			for (int i = 1; i <= 256; i++) {
				for (int j = 0; j < 16; j++) {
					for (int k = 0; k < 16; k++) {
						b = world.getBlock(x + j, i, z + k);
						if (!b.equals(Blocks.air)) {
							String name = OreDictCache.getOreName(b, b.getDamageValue(world, x + j , i, z + k));
							if (name != null) {
								int amt = 1;
								if (ores.containsKey(name)) {
									amt += ores.remove(name);
								}
								ores.put(name, amt);
							}
							total++;
						}
					}
				}
			}

			Prospecting.logger.debug("Scanned.");
			Prospecting.logger.debug("Total blocks scanned: " + total);
			Prospecting.logger.debug("Ore types found: " + ores.size());

			this.chunks.put(chunk, ores);
			this.expiry.put(chunk, world.getWorldTime() + (20 * Prospecting.config.chunk_expiry));
			int nugget_amount = (Prospecting.config.nugget_chance > ThreadLocalRandom.current().nextDouble(1)) ? Prospecting.config.nugget_amount : 0;
			if (!this.nuggets.containsKey(chunk)) {
				this.nuggets.put(chunk, ores.size() == 0 ? 0 : nugget_amount);
			}

			markDirty();
		}
	}

	// gets a nugget for chunk <x, z> and decrements that chunk's nugget count
	public ItemStack getNugget(int x, int z) {
		scanChunk(x, z); // make sure we're dealing with an initialized chunk
		logChunk(x, z);

		Prospecting.logger.debug("Getting nugget...");
		if (hasNuggets(x, z)) {
			Prospecting.logger.debug("Chunk has nuggets.");
			List<Integer> chunk = Arrays.asList(x, z);
			Prospecting.logger.debug("Amount: " + this.nuggets.get(chunk));

			int total = 0;
			for (Map.Entry<String, Integer> o : this.chunks.get(chunk).entrySet()) {
				total += o.getValue();
			}

			int r = ThreadLocalRandom.current().nextInt(0, total + 1);
			int c = 0;
			ItemStack nugget = null;
			for (Map.Entry<String, Integer> o : this.chunks.get(chunk).entrySet()) {
				c += o.getValue();
				if (c >= r) {
					List<ItemStack> nugs = OreDictionary.getOres("nugget" + o.getKey());
					if (nugs.size() > 0) {
						nugget = new ItemStack(nugs.get(0).getItem(), 1, nugs.get(0).getItemDamage());
						break;
					}
				}
			}

			Prospecting.logger.debug("Decrementing nuggets to " + (this.nuggets.get(chunk) - 1));
			this.nuggets.put(chunk, this.nuggets.get(chunk) - 1);
			markDirty();
			Prospecting.logger.debug("Chunk has " + this.nuggets.get(chunk) + " nuggets after markDirty()");
			return nugget;
		} else {
			Prospecting.logger.debug("Chunk has no nuggets left.");
			return null;
		}
	}

	private void logChunk(int x, int z) {
		List<Integer> chunk = Arrays.asList(x, z);
		Prospecting.logger.debug("Logging chunk " + chunk.toString() + "...");
		if (hasChunk(x, z)) {
			for (Map.Entry<String, Integer> o : this.chunks.get(chunk).entrySet()) {
				Prospecting.logger.debug(o.getKey() + ": " + o.getValue());
			}
		} else {
			Prospecting.logger.debug("No data for chunk.");
		}
	}
}
