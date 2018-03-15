package azathoth.util.prospecting.registry;

import azathoth.util.prospecting.Prospecting;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


public class ProspectingSavedData extends WorldSavedData {
	private HashMap<Long, ChunkInfo> chunks = new HashMap<>();
	private World world;

	public class ChunkInfo {
		public HashMap<String, Float> ores = new HashMap<>();
		public long expiry = 0;
		public HashMap<String, Integer> nuggets = new HashMap<>();
	}

	public ProspectingSavedData(World world, String tag) {
		super(tag);
		this.world  = world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	@Override
	public void readFromNBT(NBTTagCompound t) {
		Prospecting.logger.debug("Reading from NBT...");

		NBTTagList chunkList = t.getTagList("chunks", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < chunkList.tagCount(); i++) {
			NBTTagCompound chunkData = chunkList.getCompoundTagAt(i);
			int cx = chunkData.getInteger("cx");
			int cz = chunkData.getInteger("cz");

			ChunkInfo chunk = new ChunkInfo();
			chunk.expiry = chunkData.getLong("expiry");

			NBTTagCompound oresData = chunkData.getCompoundTag("ores");
			for (String ore: oresData.getKeySet()) {
				chunk.ores.put(ore, oresData.getFloat(ore));
			}

			NBTTagCompound nuggetsData = chunkData.getCompoundTag("nuggets");
			for (String ore: nuggetsData.getKeySet()) {
				chunk.nuggets.put(ore, oresData.getInteger(ore));
			}

			chunks.put(coordsToLong(cx, cz), chunk);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		Prospecting.logger.debug("Writing to NBT...");

		NBTTagList chunkList = new NBTTagList();
		for (Map.Entry<Long, ChunkInfo> c : this.chunks.entrySet()) {

			int cx = longToCx(c.getKey());
			int cz = longToCz(c.getKey());
			ChunkInfo chunk = c.getValue();

			NBTTagCompound chunkData = new NBTTagCompound();
			chunkData.setInteger("cx", cx);
			chunkData.setInteger("cz", cz);
			chunkData.setLong("expiry", chunk.expiry);

			Prospecting.logger.debug("Writing chunk: " + cx + "," + cz);

			NBTTagCompound oreData = new NBTTagCompound();
			for (Map.Entry<String, Float> ores : chunk.ores.entrySet()) {
				oreData.setFloat(ores.getKey(), ores.getValue());
			}
			chunkData.setTag("ores", oreData);

			NBTTagCompound nuggetData = new NBTTagCompound();
			for (Map.Entry<String, Integer> nuggets : chunk.nuggets.entrySet()) {
				oreData.setInteger(nuggets.getKey(), nuggets.getValue());
			}
			chunkData.setTag("nuggets", nuggetData);

			chunkList.appendTag(chunkData);
		}

		compound.setTag("chunks", chunkList);
		return compound;
	}

	public boolean hasChunk(int cx, int cz) {
		return this.chunks.containsKey(coordsToLong(cx, cz));
	}

	public boolean isStale(int cx, int cz) {
		ChunkInfo c = this.chunks.getOrDefault(coordsToLong(cx, cz), null);
		if (c != null) {
			return world.getWorldTime() > c.expiry;
		}
		return true;
	}

	public boolean hasNuggets(int cx, int cz) {
		ChunkInfo c = this.chunks.getOrDefault(coordsToLong(cx, cz), null);
		if (c != null) {
			for (int count : c.nuggets.values()) {
				if (count > 0) {
					return true;
				}
			}
		}
		return false;
	}

	public void scanChunk(int cx, int cz) {
		IBlockState bs;
		Block b;
		int total = 0;
		int x = cx << 4;
		int z = cz << 4;

		if (isStale(cx, cz)) {
			// Create a new chunk info object
			ChunkInfo cinfo = new ChunkInfo();

			Prospecting.logger.info("Scanning chunk [" + cx + ", " + cz + "]...");
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
							float count = cinfo.ores.getOrDefault(name, 0.0f);
							cinfo.ores.put(name, count+1);
						}
					}
				}
			}

			Prospecting.logger.debug("Total blocks scanned: " + total);
			Prospecting.logger.debug("Ore types found: " + cinfo.ores.size());

			cinfo.expiry = world.getWorldTime() + Prospecting.config.chunk_expiry;

			// For each of the ores, setup a counter to track number of prospecting nuggets
			for (Map.Entry<String, Float> ore : cinfo.ores.entrySet()) {
				int nuggets = getNuggetAmount(ore.getValue());
				if (nuggets > 0) {
					cinfo.nuggets.put(ore.getKey(), nuggets);
				}
			}

			// Save it
			chunks.put(coordsToLong(cx, cz), cinfo);

			markDirty();
		}
	}

	private int getNuggetAmount(float amt) {
		int r = (int) Math.ceil(amt / Prospecting.config.ore_per_nugget);
		if (r > Prospecting.config.max_nuggets) {
			return Prospecting.config.max_nuggets;
		} else if (r < 0) {
			return 0;
		}
		return r;
	}



	// gets a nugget for chunk <cx, cz> and decrements that chunk's nugget count
	public ItemStack getNugget(int cx, int cz) {
		// Make sure the chunk is initialized
		scanChunk(cx, cz);

		// Get the chunk info
		ChunkInfo c = chunks.get(coordsToLong(cx, cz));

		// No nuggets in this chunk
		if (c.nuggets.isEmpty()) {
			return ItemStack.EMPTY;
		}

		System.out.println(c.nuggets);

		// Choose an ore at random
		int index = ThreadLocalRandom.current().nextInt(0, c.nuggets.size());
		String ore = c.nuggets.keySet().toArray(new String[c.nuggets.size()])[index];

		int count = c.nuggets.getOrDefault(ore, 0);
		if (count > 0) {
			count--;
			if (count > 0) {
				c.nuggets.put(ore, count);
			} else {
				c.nuggets.remove(ore);
			}

			markDirty();

			return OreDictCache.getParticle(ore);
		}

		return ItemStack.EMPTY;
	}

	public HashMap<String, Float> getOreCounts(int cx, int cz) {
		ChunkInfo c = chunks.get(coordsToLong(cx, cz));
		if (c != null) {
			return c.ores;
		}
		return new HashMap<String, Float>();
	}

	public ChunkInfo getChunkInfo(int cx, int cz) {
		return chunks.get(coordsToLong(cx, cz));
	}

	private static Long coordsToLong(int cx, int cz) {
		return (long)cx << 32 | cz & 0xFFFFFFFFL;
	}

	private static int longToCx(long coord) {
		return (int)(coord >> 32);
	}

	private static int longToCz(long coord) {
		return (int)coord ;
	}
}
