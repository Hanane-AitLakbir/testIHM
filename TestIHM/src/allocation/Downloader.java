package allocation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import metadata.JSonSerializer;
import metadata.Metadata;
import splitMerge.Merger;
import utilities.ComputeChecksum;
import utilities.Packet;
import android.os.Environment;
import coding.Coder;
import coding.CoderFactory;
import connection.CloudNotAvailableException;
import connection.Provider;
import connection.ProviderFactory;

public class Downloader {
	static public boolean downLoad(String fileName, String directory) throws IOException {
		String simpleName = fileName.substring(0, fileName.lastIndexOf("."));
		String extension = fileName.substring(fileName.lastIndexOf(".")+1);

		Metadata listCloud= new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/list.json").deserialize();
		List<Provider> providersList  = new ArrayList<Provider>();
		Provider buffer;
		//Metadata metadataBuffer;
		String info = null;
		HashMap<String, String> map_fileName_checksum;
		for(String cloud : listCloud.getMap().keySet()){
			buffer = ProviderFactory.getProvider(cloud);
			map_fileName_checksum = Downloader.getFiles(buffer);
			//metadataBuffer = new JSonSerializer().deserializeStream(new ByteArrayInputStream(buffer.download("list.json").getData()));
			if(map_fileName_checksum.containsKey(fileName)){
				//checksum = metadataBuffer.browse(fileName); //get the checksum
				info = map_fileName_checksum.get(fileName);
				providersList.add(buffer);
			}
		}
		System.out.println("Providers found : "+providersList.size());
		//Provider[] providers = (Provider[]) providersList.toArray();

		if(info==null) return false; //file not found -> maybe throw an exception

		String[] infos = info.split("[_]");
		if(infos.length<4) return false; //Not enough info
		String checksum = infos[0];
		String coderName = infos[1];
		int inputPackets = Integer.parseInt(infos[2]);
		int outputPackets = Integer.parseInt(infos[3]);

		Coder coder = CoderFactory.getCoder(coderName);

		int nbrOfDownloaded=0;
		int packetIndex=0;
		Packet[] packets = new Packet[inputPackets];
		int[] indices = new int[inputPackets];
		int i=0;
		//Download packets
		while(nbrOfDownloaded<inputPackets&&packetIndex<outputPackets){
			for(Provider p : providersList){
				for(int j=0; j<outputPackets; j++){
					try {
						if(nbrOfDownloaded<inputPackets){
							packets[nbrOfDownloaded] = p.download(fileName +"/"+coder.getName()+"_"+j+"."+extension);
							if(packets[nbrOfDownloaded]!=null){
								//download succeeded
								if(packets[nbrOfDownloaded].getMetadata().browse("checksum").equals(ComputeChecksum.getChecksum(packets[nbrOfDownloaded].getData()))){
									indices[nbrOfDownloaded] = j;
									nbrOfDownloaded++;
								}
							}else{
								//try to download from the next cloud
								i++;
							}
						}else{
							//if enough packets have been downloaded
							break;
						}
					} catch (CloudNotAvailableException e) {
						i++;
					}
				}
			}
		}

		//Not enough packets downloaded
		if(nbrOfDownloaded<inputPackets) return false;

		Packet[] decodedPackets = coder.decode(packets, indices);
		File file = Merger.merge(directory+"/"+fileName, decodedPackets);
		return checksum.equals(ComputeChecksum.getChecksum(file));
	}

	/**
	 * 
	 * @return HashMap object with file name and checksum
	 */
	static public HashMap<String, String> getFiles(Provider provider){

		try {
			Packet listPacket = provider.download("list.json");
			InputStream stream = new ByteArrayInputStream(listPacket.getData());
			String metadataPath = Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/temp.json";

			Metadata list = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/temp.json").deserializeStream(stream);
			list.serialize(metadataPath);

			return list.getMap();

		} catch (CloudNotAvailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
