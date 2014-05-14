package allocation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import metadata.JSonSerializer;
import metadata.Metadata;
import splitMerge.Splitter;
import utilities.ComputeChecksum;
import utilities.Packet;
import android.os.Environment;
import coding.Coder;
import connection.CloudNotAvailableException;
import connection.Provider;
import connection.ProviderFactory;

public class ChosenCloud implements UploadStrategy{

	public boolean upLoad(String fileName, String[] clouds, Coder coder) throws FileNotFoundException, IOException, InvalidParameterException{
		String simpleName = fileName.substring(fileName.lastIndexOf("/")+1);
		String extension = fileName.substring(fileName.lastIndexOf(".")+1);
		String checksum = ComputeChecksum.getChecksum(new File(fileName));
		
		if(clouds.length<1){
			throw new InvalidParameterException();
		}
		
		System.out.println(Arrays.toString(clouds));
//		MetadataSerializer metadata = new JSonSerializer();
		Packet[] splittedPackets = Splitter.split(fileName, coder.getInputSize());
		
		Packet[] codedPackets = coder.encode(splittedPackets);
		//Rename packets
		for(int i=0; i<codedPackets.length; i++){
			codedPackets[i].setName(simpleName+"/"+coder.getName()+"_"+i+"."+extension);
		}
		System.out.println("Packets renamed");
		
		int nbrOfClouds = clouds.length;
		Provider[] providers = new Provider[nbrOfClouds];
		int j = 0;
		while(j<nbrOfClouds){
			providers[j] = ProviderFactory.getProvider(clouds[j]);
			providers[j].createFolder(simpleName, checksum);
			System.out.println(j+" ok "+ (providers[j]==null));
			j++;
		}
		System.out.println("getProviders OK");
		int i=0;
		
		while(i<codedPackets.length){
			System.out.println("chosenCloud "+clouds[i%nbrOfClouds]);
			try {
				// Create the folder in the cloud and update metadata
				providers[i%nbrOfClouds].upload(codedPackets[i]);
				i++;
			} catch (CloudNotAvailableException e) {
				clouds[i%nbrOfClouds] = askForCloud();
				providers[i%nbrOfClouds] = ProviderFactory.getProvider(clouds[i%nbrOfClouds]);
			}
		}
		
		Metadata filesMetadata = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/file/files List.json").deserialize();
		filesMetadata.addContent(simpleName, "");
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
}
