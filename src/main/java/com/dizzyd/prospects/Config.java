package com.dizzyd.prospects;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class Config {

	public int chunk_expiry;
	public int ore_per_nugget;
	public int max_nuggets;
	public float flower_chance;
	public float flower_false_chance;
	public int ore_per_flower;
	public int max_flowers;

	private File configFile;
	private Configuration configRoot;

	public Config(FMLPreInitializationEvent e) {
		this.configFile = e.getSuggestedConfigurationFile();
		this.reload();
	}

	public void reload() {
		configRoot = new Configuration(configFile);
		configRoot.load();

		chunk_expiry = configRoot.getInt("Chunk Expiry", "Core",
				7200, 0, 72000,
		"The number of seconds before resetting the nuggets in a chunk; 3 Minecraft days by default");

		ore_per_nugget = configRoot.getInt("Ore Per Nugget", "Probabilities",
				50, 0, 4096,
				"The number of ore, on average, that will produce 1 nugget in a chunk. For example, if this value is 50, and a chunk has 100 iron ore, you can expect to get 2 nuggets from the chunk through prospecting.");

		max_nuggets = configRoot.getInt("Maximum Nugget Count", "Probabilities",
				5, 0, 4096,
				"The maximum number of nuggets to spawn in a given chunk for each ore.");

		flower_chance = configRoot.getFloat("Flower Chance", "Probabilities",
				0.95f, 0f, 1f,
				"The chance that a flower will be generated, when a chunk contains ore. The number of flowers produced is determined by the \"Ore Per Flower\" setting.");

		flower_false_chance = configRoot.getFloat("Flower False Positive Chance", "Probabilities",
				0f, 0f, 1f,
				"This chance that a chunk will have some indicator flowers spawn despite having no ore.");

		ore_per_flower = configRoot.getInt("Ore Per Flower", "Probabilities",
				30, 0, 4096,
				"The number of ore, on average, that it takes to produce 1 flower on the surface.");

		max_flowers = configRoot.getInt("Maximum Flower Count", "Probabilities",
				10, 0, 4096,
				"The maximum number of flowers to spawn in a given chunk for each type of ore.");

		if (configRoot.hasChanged()) {
			configRoot.save();
		}
	}

	public Configuration getConfigRoot() {
		return configRoot;
	}
}
