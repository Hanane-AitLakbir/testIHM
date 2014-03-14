package allocation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import android.os.Environment;
import metadata.JSonSerializer;
import metadata.Metadata;
import metadata.MetadataSerializer;
import connection.CloudNotAvailableException;
import connection.Provider;
import connection.ProviderCloud;
import connection.ProviderFactory;
import coding.Coder;


import splitMerge.Splitter;
import utilities.Cloud;
import utilities.ComputeChecksum;
import utilities.Packet;

public class ChosenCloud implements AllocationStrategy{

	public boolean upLoad(String fileName, int nbrOfPackets, String[] clouds, Coder coder) throws FileNotFoundException, IOException{
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
