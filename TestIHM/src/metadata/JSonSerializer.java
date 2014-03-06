package metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The object Metadata manage the access to a file JSON with the list of storage devices used (Cloud and Webdav server) and their characteristics.
 * @author hanane
 *
 */
public class JSonSerializer implements MetadataSerializer{

	private String metadataPath;
	private ObjectMapper mapper;
	private JsonNode root;
	public JSonSerializer() {
		mapper = new ObjectMapper();
		root = mapper.createObjectNode();
		//((ObjectNode) root).putObject("content");
	}
	
	@Override
	public Metadata deserialize() {
		FileInputStream fileStream;
		try {
			fileStream = new FileInputStream(this.metadataPath);
			mapper = new ObjectMapper();
			root = mapper.readTree(fileStream);
			return new Metadata(this);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Metadata deserializeStream(InputStream stream){
		try {
			mapper = new ObjectMapper();
			root = mapper.readTree(stream);
			return new Metadata(this);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public boolean addContent(String key, String value) {
		((ObjectNode) root).put(key, value);
		return true;
	}
	@Override
	public String browse(String string) {
		return root.findValue(string).asText();
	}
	@Override
	public void serialize(String metadataPath) {
		try {
			mapper.writeValue(new File(metadataPath), root);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public JSonSerializer(String metadataPath){
		this.metadataPath = metadataPath;
	}

	@Override
	public HashMap<String, String> getMap() {
		FileInputStream fileStream;
		try {
			fileStream = new FileInputStream(this.metadataPath);
			mapper = new ObjectMapper();
			root = mapper.readTree(fileStream);
			Iterator<String> iterator = root.fieldNames();
			String key,value;
			HashMap<String, String> map = new HashMap<String, String>();
			while(iterator.hasNext()){
				key = iterator.next();
				value = root.findValue(key).asText();
				map.put(key, value);	
			}
			return map;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public void delete(String key) {
		FileInputStream fileStream;
		try {
			fileStream = new FileInputStream(this.metadataPath);
			mapper = new ObjectMapper();
			root = mapper.readTree(fileStream);
			((ObjectNode) root).remove(key);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//----------------------------------------------------------------------
/*
	public TreeMap<String,String> searchWebdav(String name){
		TreeMap<String,String> result = new TreeMap<String,String>();
		JsonNode aNode = root.findPath(name);
		Iterator<String> list = aNode.fieldNames();
		JsonNode current;
		String st;
		while(list.hasNext()){
			st = list.next();
			current = aNode.findValue(st);
			System.out.println("\t\t" + st + " " + current.asText());
			result.put(st, current.asText());
		}
		return result;

	}

	public TreeMap<String,String> searchCloud(String name){
		return null;

	}
	public boolean addFile(String nameFile, Packet[] packets){
		int numOfFiles;
		try {

			JsonNode fileNode = root.findPath("file");
			numOfFiles = fileNode.path("numOfFiles").intValue();

			ObjectNode newFile = ((ObjectNode) fileNode).putObject(nameFile);

			newFile.put("id", (numOfFiles+1));
			newFile.put("checksum","");

			for(Packet p : packets ){
				createNodeFromPacket(newFile, p);
			}

			((ObjectNode) fileNode).put("numOfFiles", (numOfFiles+1));

			mapper.writeValue(new File(metadataPath), root);

			return true;

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	public boolean addCloud(TreeMap<String,String> proprieties){

		try {

			JsonNode fileNode = root.findPath("cloud");

			mapper.writeValue(new File(metadataPath), root);

			return true;

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}
//	private void createNodeFromPacket(JsonNode parentNode, Packet packet){
//		ObjectNode newPacket = ((ObjectNode) parentNode).putObject(packet.getName());
//		//newPacket.put("checksum", packet.getChecksum());
//	}

	@Override
	public File serialize(MetadataElement element, String path) {
		File file = null;
		try {
			file = File.createTempFile(path.substring(0, path.lastIndexOf(".")), ".tmp");
			FileInputStream fileStream = new FileInputStream(this.metadataPath);
			mapper = new ObjectMapper();
			root = mapper.readTree(fileStream);
			TreeMap<String, String> content = element.getContent();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return file;
	}

	
*/
}
