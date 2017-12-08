package upload;

import java.util.HashMap;

/** Class with constants used over multiple classes
 * 
 *
 * @author Simon Lacis
 *
 */
import java.util.Map;

public class UploadConstants {
	// REAL SERVER: https://app.alveo.edu.au/catalog/
	// STAGING SERVER: https://alveo-staging.sol1.net/catalog/
	// Note: If using the staging server, you will need to add the certificate manually
	// to avoid SSL certificate errors when uploading
	public static final String CATALOG_URL = "https://staging.alveo.edu.au/catalog/";
	public static final Map<String, String> EXT_MAP = new HashMap<String, String>(){{
		put(".wav", "Audio");
		put(".txt", "Text");
		put(".sf0", "Pitch Track");
		put( ".sfb", "Formant Track");
		put(".lab", "Annotation");
		put( ".trg", "Annotation");
		put(".hlb", "Annotation");
	}};
}
