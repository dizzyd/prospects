package azathoth.util.prospecting;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;

public class Config {

	public int chunk_expiry;
	public int ore_per_nugget;
	public int max_nuggets;
	public float flower_chance;
	public float flower_false_chance;
	public int ore_per_flower;
	public int max_flowers;

	public Config(FMLPreInitializationEvent e) {
		final Configuration config = new Configuration(e.getSuggestedConfigurationFile());

		config.load();

		this.chunk_expiry = config.getInt("Chunk Expiry", "",
				7200, 0, 72000,
		"The number of seconds before resetting the nuggets in a chunk; 3 Minecraft days by default");

		this.ore_per_nugget = config.getInt("Ore Per Nugget", "Probabilities",
				50, 0, 4096,
				"The number of ore, on average, that will produce 1 nugget in a chunk. For example, if this value is 50, and a chunk has 100 iron ore, you can expect to get 2 nuggets from the chunk through prospecting.");

		this.max_nuggets = config.getInt("Maximum Nugget Count", "Probabilities",
				5, 0, 4096,
				"The maximum number of nuggets to spawn in a given chunk for each ore.");

		this.flower_chance = config.getFloat("Flower Chance", "Probabilities",
				0.8f, 0f, 1f,
				"The chance that a given chunk will produce flowers, if it contains ore. The number of flowers produced is determined by the \"Ore Per Flower\" setting.");

		this.flower_false_chance = config.getFloat("Flower False Positive Chance", "Probabilities",
				-1, -1f, 1f,
				"This chance that a chunk will have some indicator flowers spawn despite having no ore.");

		this.ore_per_flower = config.getInt("Ore Per Flower", "Probabilities",
				50, 0, 4096,
				"The number of ore, on average, that it takes to produce 1 flower on the surface.");

		this.max_flowers = config.getInt("Maximum Flower Count", "Probabilities",
				10, 0, 4096,
				"The maximum number of flowers to spawn in a given chunk for each type of ore.");

		config.save();
	}
}
