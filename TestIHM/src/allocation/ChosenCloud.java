package allocation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import android.os.Environment;
import metadata.JSonSerializer;
import metadata.Metadata;
import metadata.MetadataSerializer;
import connection.CloudNotAvailableException;
import connection.Provider;
import connection.ProviderCloud;
import connection.ProviderFactory;
import coding.Coder;


import splitMerge.Merger;
import splitMerge.Splitter;
import utilities.Cloud;
import utilities.ComputeChecksum;
import utilities.Packet;

public class ChosenCloud implements AllocationStrategy{

	public boolean upLoad(String fileName, int nbrOfPackets, String[] clouds, Coder coder) throws FileNotFoundException, IOException, InvalidParameterException{
		if(nbrOfPackets<=0){
			throw new InvalidParameterException();
		}
		System.out.println(Arrays.toString(clouds));
		MetadataSerializer metadata = new JSonSerializer();
		Packet[] splittedPackets = Splitter.split(fileName, nbrOfPackets);
		
		Packet[] codedPackets = coder.encode(splittedPackets);
		
		int nbrOfClouds = clouds.length;
		Provider[] providers = new Provider[nbrOfClouds];
		for(int i=0; i<nbrOfClouds; i++){
			providers[i] = ProviderFactory.getProvider(clouds[i]);
			System.out.println(i+" ok "+ (providers[i]==null));
		}
		System.out.println("getProviders OK");
		int i=0;
		metadata.addContent("name", fileName);
		metadata.addContent("checksum", ComputeChecksum.getChecksum(new File(fileName)));
		metadata.addContent("number of packets", ""+codedPackets.length);
		System.out.println("meta OK");
		
		while(i<codedPackets.length){
			System.out.println("chosenCloud "+clouds[i%nbrOfClouds]);
			try {
				providers[i%nbrOfClouds].upload(codedPackets[i]);
				metadata.addContent("cloud"+i, clouds[i%nbrOfClouds]);
				i++;
			} catch (CloudNotAvailableException e) {
				clouds[i%nbrOfClouds] = askForCloud();
				providers[i%nbrOfClouds] = ProviderFactory.getProvider(clouds[i%nbrOfClouds]);
			}
		}
		String name = fileName.substring(fileName.lastIndexOf("/")+1); //fileName gets the name of the folder and +1 to remove the slash
		metadata.serialize(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/file/"+name+".json");
		Metadata filesMetadata = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/file/files List.json").deserialize();
		filesMetadata.addContent(name, "");
		filesMetadata.serialize(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/file/files List.json");
		return true;
	}

	public String askForCloud() {
		System.out.println("Specify another cloud :");
		try {
			return new BufferedReader(new InputStreamReader(System.in)).readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean downLoad(String fileName, String directory, Coder coder) throws IOException {
		Metadata metadata = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/file/"+fileName+".json").deserialize();
		String checksum = metadata.browse("checksum");
		int nbrOfPackets = Integer.valueOf(metadata.browse("number of packets"));
		Packet[] packets = new Packet[nbrOfPackets];
		TreeMap<Integer,String> packetsToClouds = new TreeMap<Integer,String>();
		List<String> clouds = new ArrayList<String>();
		String cloud;
		//Getting the clouds used
		for(int i=0; i<nbrOfPackets; i++){
			cloud = metadata.browse("cloud"+i);
			if(!clouds.contains(cloud)){
				clouds.add(cloud);
			}
			packetsToClouds.put(i, cloud);
		}
		//Creating the providers
		Provider[] providers = new Provider[clouds.size()];
		for(int i=0; i<providers.length; i++){
			providers[i] = ProviderFactory.getProvider(clouds.get(i));
		}
		//Downloading the packets
		String simpleName = fileName.substring(0, fileName.lastIndexOf(".")); // 0 for start index missed in parameters
		int providerIndex;
		for(int i=0; i<nbrOfPackets; i++){
			try {
				providerIndex = clouds.indexOf(packetsToClouds.get(i));
				if(providerIndex>=0){
					packets[i] = providers[providerIndex].download(simpleName+"_"+i+fileName.substring(fileName.lastIndexOf(".")));
				}
			} catch (CloudNotAvailableException e) {
				e.printStackTrace();
			}
		}

		Packet[] decodedPackets = coder.decode(packets);
		File file = Merger.merge(directory+"/"+fileName, decodedPackets);
		return checksum.equals(ComputeChecksum.getChecksum(file));
	}
}
