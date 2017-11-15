package azathoth.util.prospecting.config;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;

public class ProspectingConfiguration {
	public int nugget_amount;
	public int chunk_expiry;

	public float nugget_chance;

	public ProspectingConfiguration(FMLPreInitializationEvent e) {
		final Configuration config = new Configuration(e.getSuggestedConfigurationFile());

		config.load();

		this.nugget_amount = config.getInt("Nuggets Per Chunk", "General", 1, 0, 999999999, "The number of nuggets that can be prospected in a chunk, if it has applicable ore in it.");
		this.nugget_chance = config.getFloat("Nugget Chance", "General", 0.1f, 0f, 1f, "The chance that a chunk will have nuggets.");
		this.chunk_expiry = config.getInt("Chunk Expiry", "General", 300, 1, 999999999, "The number of seconds until a chunk's cache expires. After the cache expires, the chunk will be re-scanned for ore when it is prospected.");

		config.save();
	}
}
