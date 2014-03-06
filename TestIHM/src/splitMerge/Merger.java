package splitMerge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import utilities.Packet;

public class Merger {

	public static File merge(String name, Packet[] packets) throws IOException{
		File file = new File(name);
		file.createNewFile();
		FileOutputStream writer = new FileOutputStream(file, true);
		for(int i=0; i<packets.length; i++){
			writer.write(packets[i].getData());
			writer.flush();
		}
		writer.close();
		return file;
	}
}
