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
import connection.CloudNotAvailableException;
import connection.Provider;
import connection.ProviderFactory;

public class Downloader {
	static public boolean downLoad(String fileName, String directory, Coder coder) throws IOException {
		String simpleName = fileName.substring(0, fileName.lastIndexOf("."));
		String extension = fileName.substring(fileName.lastIndexOf(".")+1);

		Metadata listCloud= new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/list.json").deserialize();
		List<Provider> providersList  = new ArrayList<Provider>();
		Provider buffer;
		Metadata metadataBuffer;
		String checksum = null;
		for(String cloud : listCloud.getMap().keySet()){
			buffer = ProviderFactory.getProvider(cloud);
			try {
				metadataBuffer = new JSonSerializer().deserializeStream(new ByteArrayInputStream(buffer.download("list.json").getData()));
				if(metadataBuffer.getMap().containsKey(fileName)){
					checksum = metadataBuffer.browse(fileName); //get the checksum
					providersList.add(buffer);
				}
			} catch (CloudNotAvailableException e) {}
		}
		System.out.println("Providers found : "+providersList.size());
		Provider[] providers = (Provider[]) providersList.toArray();

		if(checksum==null) return false; //file not found -> maybe throw an exception

		int nbrOfPackets = coder.getInputSize();

		int nbrOfDownloaded=0;
		int packetIndex=0;
		Packet[] packets = new Packet[nbrOfPackets];
		int[] indices = new int[nbrOfPackets];
		int i=0;
		//Download packets
		while(nbrOfDownloaded<nbrOfPackets&&packetIndex<2*nbrOfPackets){
			while(i<providers.length){
				try {
					packets[nbrOfDownloaded] = providers[i].download(simpleName+"/"+coder.getName()+"_"+packetIndex+"."+extension);
					if(packets[nbrOfDownloaded]!=null&&packets[nbrOfDownloaded].getData()!=null&&packets[nbrOfDownloaded].getData().length!=0){
						//download succeeded
						if(packets[nbrOfDownloaded].getMetadata().browse("checksum").equals(ComputeChecksum.getChecksum(packets[nbrOfDownloaded].getData()))){
							indices[nbrOfDownloaded] = packetIndex;
							nbrOfDownloaded++;
							packetIndex++;
						}
					}else{
						//try to download from the next cloud
						i++;
					}
				} catch (CloudNotAvailableException e) {
					i++;
				}
			}
			//current index not found, try the next
			packetIndex++;
			i=0;
		}

		//Not enough packets downloaded
		if(nbrOfDownloaded<nbrOfPackets) return false;

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
