package splitMerge;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import utilities.ComputeChecksum;
import utilities.Packet;

public class SplitterAndMergerTest {

	@Test
	public void testMerge() {
		/*
		 * I create 6 packets '0000' '1111' '2222' etc. In the final file, you should get '000011112222....5555'
		 */
		Packet[] packets = new Packet[6];
		String word;
		for(int i=0;i<6;i++){
			word = ""+i+i+i+i;
			packets[i] = new Packet("packet"+i,word.getBytes());
		}
		
		try {
			File file = new File("tests/testMerger.txt");
			if(file.exists()) file.delete();
			Merger.merge("tests/testMerger.txt", packets);
			FileReader reader = new FileReader(file);
			for(int i=0; i<6; i++){
				for(int j=0; j<4; j++){
					assertEquals((""+i).getBytes()[0], reader.read());
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSplit() {
			try {
				File file = new File("tests/testSplitter.txt");
				if(!file.exists()){
					file.createNewFile();
					FileWriter writer = new FileWriter(file);
					for(int j=0; j<6; j++){
						for(int i=0; i<4; i++){
							writer.write(j);
						}
					}
					writer.close();
				}
				Packet[] packets = Splitter.split("tests/testSplitter.txt", 6);
				for(int j=0; j<6; j++){
					for(int i=0; i<4; i++){
						assertEquals(j, packets[j].getData()[i]);
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	@Test
	public void testSplitAndMerge(){
		try {
			File file = new File("tests/testSplitterAndMerger.txt");
			if(!file.exists()){
				file.createNewFile();
				FileWriter writer = new FileWriter(file);
				for(int j=0; j<6; j++){
					for(int i=0; i<4; i++){
						writer.write(j);
					}
				}
				writer.close();
			}
			String checksum = ComputeChecksum.getChecksum(file);
			Packet[] packets = Splitter.split("tests/testSplitterAndMerger.txt", 6);
			
			file = new File("tests/testMergerAndSplitter.txt");
			if(file.exists()) file.delete();
			Merger.merge("tests/testMergerAndSplitter.txt", packets);
			
			assertEquals(true, checksum.equals(ComputeChecksum.getChecksum(file)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
