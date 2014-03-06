package metadata;

import java.util.HashMap;

/*
 * Replaces metadataElement and metadataPacket 
 */
public class Metadata {
	private MetadataSerializer serializer;
	
	public Metadata(){
		serializer = new JSonSerializer();
	}
	
	public Metadata(MetadataSerializer serializer){
		this.serializer = serializer;
	}
	
	public boolean addContent(String key, String value){
		return serializer.addContent(key,value);
	}

	public String browse(String string){
		return serializer.browse(string);
	}
	
	public void serialize(String metadataPath){
		serializer.serialize(metadataPath);
	}
	
	public HashMap<String,String> getMap(){
		return serializer.getMap();
	}
	
	public void delete(String key){
		serializer.delete(key);
	}
}
