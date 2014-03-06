package splitMerge;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import utilities.Packet;

public class Splitter {

	public static Packet[] split(String fileName, int nbrOfPackets) throws FileNotFoundException, IOException{
		Packet[] packets = new Packet[nbrOfPackets];
		File file = new File(fileName);
		String name = fileName.substring(0,fileName.lastIndexOf("."));
		int size = (int) (file.length()/nbrOfPackets);
		FileInputStream stream = new FileInputStream(file);
		byte[] data;
		for(int i=0; i<nbrOfPackets-1; i++){
			data = new byte[size];
			stream.read(data, 0, size);
			packets[i] = new Packet(name+"_"+i,data);
		}
		
		data = new byte[(int) (file.length()-size*(nbrOfPackets-1.))];
		stream.read(data, 0, data.length);
		packets[nbrOfPackets-1] = new Packet(name+"_"+(nbrOfPackets-1),data);
		stream.close();
		return packets;
	}
	
}
