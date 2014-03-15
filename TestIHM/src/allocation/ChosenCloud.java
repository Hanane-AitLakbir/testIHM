package allocation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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
		Provider provider;
		int i=0;
		metadata.addContent("name", fileName);
		metadata.addContent("checksum", ComputeChecksum.getChecksum(new File(fileName)));
		metadata.addContent("number of packets", ""+codedPackets.length);
		System.out.println("meta OK");
		
		while(i<codedPackets.length){
			System.out.println("chosenCloud "+clouds[i%nbrOfClouds]);
			provider = ProviderFactory.getProvider(clouds[i%nbrOfClouds]);
			System.out.println("getProvider OK");
			try {
				provider.upload(codedPackets[i]);
				i++;
				metadata.addContent("cloud"+i, clouds[i%nbrOfClouds]);
			} catch (CloudNotAvailableException e) {
				clouds[i%nbrOfClouds] = askForCloud();
			}
		}
		String name = fileName.substring(fileName.lastIndexOf("/")); //fileName gets the name of the folder 
		metadata.serialize(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/file/"+name+".json");
		Metadata filesMetadata = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/files List.json").deserialize();
		//TODO I don't know what to put in here...
		filesMetadata.addContent("", "");
		filesMetadata.serialize(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/files List.json");
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
		String simpleName = fileName.substring(fileName.lastIndexOf("."));
		int providerIndex;
		for(int i=0; i<nbrOfPackets; i++){
			try {
				providerIndex = clouds.indexOf(packetsToClouds.get(i));
				if(providerIndex>=0){
					packets[i] = providers[providerIndex].download(simpleName+""+i);
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
