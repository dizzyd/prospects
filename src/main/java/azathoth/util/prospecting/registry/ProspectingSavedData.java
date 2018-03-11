package azathoth.util.prospecting.registry;

import azathoth.util.prospecting.Prospecting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;


public class ProspectingSavedData extends WorldSavedData {
	private HashMap<List<Integer>, HashMap<String, Float>> chunks; // [cx, cz]: { "ore": amt }
	private HashMap<List<Integer>, Long> expiry;
	private HashMap<List<Integer>, HashMap<String, Integer>> nuggets;
	private World world;

	public ProspectingSavedData(World world, String tag) {
		super(tag);
		this.world  = world;
		this.chunks = new HashMap<List<Integer>, HashMap<String, Float>>();
		this.expiry = new HashMap<List<Integer>, Long>();
		this.nuggets = new HashMap<List<Integer>, HashMap<String, Integer>>();
	}

	public ProspectingSavedData(String tag) {
		super(tag);
		this.chunks = new HashMap<List<Integer>, HashMap<String, Float>>();
		this.expiry = new HashMap<List<Integer>, Long>();
		this.nuggets = new HashMap<List<Integer>, HashMap<String, Integer>>();
	}

	public void setWorld(World world) {
		this.world = world;
	}

	@Override
	public void readFromNBT(NBTTagCompound t) {
		Prospecting.logger.debug("Reading from NBT...");
		this.chunks = new HashMap<List<Integer>, HashMap<String, Float>>();
		this.expiry = new HashMap<List<Integer>, Long>();
		this.nuggets = new HashMap<List<Integer>, HashMap<String, Integer>>();

		for (String x : t.getKeySet()) {
			for (String z : t.getCompoundTag(x).getKeySet()) {
				List<Integer> chunk = Arrays.asList(Integer.valueOf(x), Integer.valueOf(z));

				HashMap<String, Float> ores = new HashMap<String, Float>();
				for (Object ore : t.getCompoundTag((String) x).getCompoundTag(z).getKeySet()) {
					if (!((String) ore).equals("expiry") && !((String) ore).equals("nuggets")) {
						ores.put((String) ore, t.getCompoundTag((String) x).getCompoundTag((String) z).getFloat((String) ore));
					}
				}

				HashMap<String, Integer> nug_list = new HashMap<String, Integer>();
				for (String ore : t.getCompoundTag((String) x).getCompoundTag((String) z).getCompoundTag("nuggets").getKeySet()) {
					nug_list.put(ore, t.getCompoundTag(x).getCompoundTag(z).getCompoundTag("nuggets").getInteger(ore));
				}

				this.chunks.put(chunk, ores);
				this.expiry.put(chunk, t.getCompoundTag(x).getCompoundTag(z).getLong("expiry"));
				this.nuggets.put(chunk, nug_list);
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		Prospecting.logger.debug("Writing to NBT...");
		for (Map.Entry<List<Integer>, HashMap<String, Float>> chunk : this.chunks.entrySet()) {
			int cx = chunk.getKey().get(0);
			int cz = chunk.getKey().get(1);

			Prospecting.logger.debug("Writing chunk " + chunk.getKey().toString() + "...");

			NBTTagCompound x_tag = new NBTTagCompound();
			NBTTagCompound z_tag = new NBTTagCompound();
			NBTTagCompound nug_tag = new NBTTagCompound();

			for (Map.Entry<String, Float> ore : chunk.getValue().entrySet()) {
				z_tag.setFloat(ore.getKey(), ore.getValue());
			}

			for (Map.Entry<String, Integer> ore : nuggets.get(chunk.getKey()).entrySet()) {
				nug_tag.setInteger(ore.getKey(), ore.getValue());
			}

			// Prospecting.logger.debug("Cooldown: " + this.expiry.get(chunk.getKey()));
			// Prospecting.logger.debug("Nuggets: " + this.nuggets.get(chunk.getKey()));
			z_tag.setLong("expiry", this.expiry.get(chunk.getKey()));
			z_tag.setTag("nuggets", nug_tag);

			x_tag.setTag(Integer.toString(cz), z_tag);
			compound.setTag(Integer.toString(cx), x_tag);
		}
		return compound;
	}

	public boolean hasChunk(int cx, int cz) {
		try {
			 return this.chunks.get(Arrays.asList(cx, cz)) != null;
		} catch (NullPointerException e) {
			return false;
		}
	}

	public boolean isStale(int cx, int cz) {
		try {
			return !hasChunk(cx, cz) || world.getWorldTime() > this.expiry.get(Arrays.asList(cx, cz));
		} catch (NullPointerException e) {
			return true;
		}
	}

	public boolean hasNuggets(int cx, int cz) {
		try  {
			for (Map.Entry<String, Integer> ore : this.nuggets.get(Arrays.asList(cx, cz)).entrySet()) {
				if (ore.getValue() > 0)
					return true;
			}
			return false;
		} catch (NullPointerException e) {
			return false;
		}
	}

	public void scanChunk(int cx, int cz) {
		IBlockState bs;
		Block b;
		HashMap<String, Float> ores = new HashMap<>();
		int total = 0;
		List<Integer> chunk = Arrays.asList(cx, cz);
		int x = cx << 4;
		int z = cz << 4;


		if (isStale(cx, cz)) {
			Prospecting.logger.debug("Scanning chunk [" + cx + ", " + cz + "]...");
			for (int i = 1; i <= 256; i++) {
				for (int j = 0; j < 16; j++) {
					for (int k = 0; k < 16; k++) {
						bs = world.getBlockState((new BlockPos(x + j, i, z+ k)));
						b = bs.getBlock();

						// Fast-path on common blocks
						if (b == Blocks.AIR || b == Blocks.STONE || b == Blocks.DIRT) {
							continue;
						}

						total++;

						// If the block is an ore, we need to increment the counter for this chunk
						String name = OreDictCache.getOreName(bs);
						if (name != null) {
							float count = ores.getOrDefault(name, 0.0f);
							ores.put(name, count+1);
						}
					}
				}
			}

			Prospecting.logger.debug("Total blocks scanned: " + total);
			Prospecting.logger.debug("Ore types found: " + ores.size());

			this.chunks.put(chunk, ores);
			this.expiry.put(chunk, world.getWorldTime() + (20 * Prospecting.config.chunk_expiry));

			if (!this.nuggets.containsKey(chunk)) {
				this.nuggets.put(chunk, new HashMap<String, Integer>());
				for (Map.Entry<String, Float> ore : this.chunks.get(chunk).entrySet()) {
					if (!this.nuggets.get(chunk).containsKey(ore.getKey())) {
						int amount = getNuggetAmount(ore.getValue());
						if (amount > 0) {
							this.nuggets.get(chunk).put(ore.getKey(), amount);
						}
					}
				}
			}


			markDirty();
		}
	}

	private int getNuggetAmount(float amt) {
		int divisor = Prospecting.config.ore_per_nugget + (ThreadLocalRandom.current().nextInt(0, (Prospecting.config.ore_per_nugget_deviation * 2) + 1)) - Prospecting.config.ore_per_nugget_deviation;
		int r = (int) (amt / divisor);
		if (r > Prospecting.config.max_nuggets) {
			return Prospecting.config.max_nuggets;
		} else if (r < 0) {
			return 0;
		}
		return r;
	}

	public int getFlowerCount(String ore, int cx, int cz) {
		List<Integer> chunk = Arrays.asList(cx, cz);
		if (this.chunks.containsKey(chunk) && this.chunks.get(chunk).containsKey(ore)) {
			return 1;
//			int divisor = Prospecting.config.ore_per_flower + (ThreadLocalRandom.current().nextInt(0, (Prospecting.config.ore_per_flower_deviation * 2) + 1)) - Prospecting.config.ore_per_flower_deviation;
//			return (int) (this.chunks.get(chunk).get(ore) / divisor);
		}

		return 0;
	}

	// gets a nugget for chunk <cx, cz> and decrements that chunk's nugget count
	public ItemStack getNugget(int cx, int cz) {
		scanChunk(cx, cz); // make sure we're dealing with an initialized chunk

		if (hasNuggets(cx, cz)) {
			List<Integer> chunk = Arrays.asList(cx, cz);
			ArrayList<String> ores;

			Prospecting.logger.info("Getting nuggets for " + chunk.toString());

			try {
				ores = new ArrayList<String>(this.nuggets.get(chunk).keySet());
			} catch (NullPointerException e) {
				return null;
			}

			String ore = ores.get(ThreadLocalRandom.current().nextInt(0, ores.size()));
			ItemStack nugget = OreDictCache.getNuggetFromName(ore);

			Prospecting.logger.info("Selected " + ore);

			if (nugget != null) {
				Prospecting.logger.info("Nugget found.");
				int amt = this.nuggets.get(chunk).get(ore) - 1;
				Prospecting.logger.info("Chunk has " + amt + " nuggets after spawning.");

				if (amt <= 0) {
					this.nuggets.get(chunk).remove(ore);
				} else {
					this.nuggets.get(chunk).put(ore, amt);
				}

				markDirty();

				return new ItemStack(nugget.getItem(), 1, nugget.getItemDamage());
			}

			Prospecting.logger.info("Nugget was null.");

			return null;
		} else {
			Prospecting.logger.info("Chunk has no nuggets left.");
			return null;
		}
	}

	public void logChunk(int cx, int cz) {
		List<Integer> chunk = Arrays.asList(cx, cz);
		if (hasChunk(cx, cz)) {
			Prospecting.logger.info("Chunk info " + chunk.toString() + ":");
			for (Map.Entry<String, Float> o : this.chunks.get(chunk).entrySet()) {
				Prospecting.logger.info("\"" + o.getKey() + "\": " + o.getValue());
			}
		} else {
			Prospecting.logger.info("No data for chunk " + chunk.toString() + ".");
		}
	}

	public Set<String> getOres(int cx, int cz) {
		try {
			return chunks.get(Arrays.asList(cx, cz)).keySet();
		} catch (NullPointerException e) {
			return null;
		}
	}
}
