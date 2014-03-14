package allocation;


import java.io.FileNotFoundException;
import java.io.IOException;

import coding.Coder;


import utilities.Cloud;

public interface AllocationStrategy {

	public boolean upLoad(String fileName, int nbrOfPackets, String[] clouds, Coder coder) throws FileNotFoundException, IOException;
}
