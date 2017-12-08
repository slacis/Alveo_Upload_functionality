package mapping;

import net.sf.json.JSONObject;

public class JSONMappings {
	// Adds JSONObject one to JSONObject two
	public static void combineJSONObject(JSONObject one, JSONObject two) {
		for (Object key: one.keySet()) {
			two.put(key, one.get(key));
		}
	}

}
