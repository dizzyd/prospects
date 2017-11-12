package azathoth.util.prospecting.registry;

import azathoth.util.prospecting.Prospecting;

import java.util.HashMap;
import java.util.List;

public class ProspectingRegistry {
	private static final ProspectingRegistry instance = new ProspectingRegistry();

	private static HashMap<List, HashMap<String, Integer>> registry = new HashMap<List, HashMap<String, Integer>>();

	private ProspectingRegistry() {
		// stub
	}

	public static ProspectingRegistry getInstance() {
		return instance;
	}

	// Checks if chunk has been registered
	public static boolean isChunkRegistered(int x, int z) {
		// stub
	}
	
	// Get 
	public static HashMap<String, Integer> getOres(int x, int z) {

	}
}
