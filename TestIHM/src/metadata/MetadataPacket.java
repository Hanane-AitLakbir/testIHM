package metadata;

import java.util.TreeMap;

/*
 * Deprecated, use Metadata instead
 */
public class MetadataPacket implements MetadataElement{
	String checksum,id;
	public MetadataPacket(String checksum,String id) {
		this.checksum= checksum;
		this.id = id;
	}
	@Override
	public TreeMap<String, String> getContent() {
		TreeMap<String, String> content = new TreeMap<String,String>();
		content.put("id", id);
		content.put("checksum", checksum);
		return content;
	}
	
}
