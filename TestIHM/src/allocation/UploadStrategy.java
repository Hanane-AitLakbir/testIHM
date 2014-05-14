package allocation;


import java.io.FileNotFoundException;
import java.io.IOException;

import coding.Coder;

public interface UploadStrategy {

	public boolean upLoad(String fileName, String[] clouds, Coder coder) throws FileNotFoundException, IOException, InvalidParameterException;
}