package allocation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import splitMerge.Splitter;
import utilities.ComputeChecksum;
import utilities.Packet;
import metadata.JSonSerializer;
import metadata.Metadata;
import metadata.MetadataSerializer;
import android.os.Environment;
import connection.CloudNotAvailableException;
import connection.Provider;
import connection.ProviderFactory;
import coding.Coder;

public class Equitable implements UploadStrategy{

	public boolean upLoad(String fileName, String[] clouds, Coder coder) 
			throws FileNotFoundException, IOException, InvalidParameterException {
		if(clouds.length<1){
			throw new InvalidParameterException();
		}

		Packet[] splittedPackets = Splitter.split(fileName, coder.getInputSize());
		Packet[] codedPackets = coder.encode(splittedPackets);
		long minimalSpace = codedPackets[0].getData().length;

		TreeMap<String, Provider> providers = new TreeMap<String, Provider>();
		TreeMap<String, Long> spaces = new TreeMap<String, Long>();
		Provider buffer;
		Metadata metadata;
		long space;
		for(String str : clouds){
			buffer = ProviderFactory.getProvider(str);
			if(buffer!=null){
				metadata = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/"+str+".json").deserialize();
				space = Long.parseLong(metadata.browse("space"));
				if(space>=minimalSpace){
					spaces.put(str, space);
					providers.put(str, buffer);
				}
			}
		}

		MetadataSerializer fileMetadata = new JSonSerializer();
		fileMetadata.addContent("name", fileName);
		fileMetadata.addContent("checksum", ComputeChecksum.getChecksum(new File(fileName)));
		fileMetadata.addContent("number of packets", ""+codedPackets.length);

		String current = "";
		String previous = "";
		int i=0;
		while(i<codedPackets.length){
			current = getEmptier(spaces, previous);
			try {
				providers.get(current).upload(codedPackets[i]);
				fileMetadata.addContent("cloud"+i, current);
				previous = current;
				i++;
			} catch (CloudNotAvailableException e) {
				spaces.remove(current);
				providers.remove(current);
			}
		}

		String name = fileName.substring(fileName.lastIndexOf("/")+1); //fileName gets the name of the folder and +1 to remove the slash
		fileMetadata.serialize(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/file/"+name+".json");
		Metadata filesMetadata = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/file/files List.json").deserialize();
		filesMetadata.addContent(name, "");
		filesMetadata.serialize(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/file/files List.json");

		return true;
	}

	public boolean downLoad(String fileName, String directory, Coder coder)	throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	private String getEmptier(TreeMap<String, Long> map, String exception){
		long max = Long.MIN_VALUE;
		String emptier = "";
		for(String str : map.keySet()){
			if(map.get(str)>max&&str!=exception){
				max = map.get(str);
				emptier = str;
			}
		}
		return emptier;
	}
}