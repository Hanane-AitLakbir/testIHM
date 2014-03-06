package metadata;

import java.util.HashMap;


public interface MetadataSerializer {
	//public File serialize(MetadataElement element, String path);
	public Metadata deserialize();
	public boolean addContent(String key, String value);
	public String browse(String string);
	public void serialize(String metadataPath);
	public HashMap<String, String> getMap();
	public void delete(String key);
}
