package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ComputeChecksum {
 public static String getChecksum(File file) throws IOException{
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA1");
			FileInputStream input = new FileInputStream(file);
			byte[] data = new byte[1024];

			int bytesRead = 0;
			while((bytesRead=input.read(data))!=-1){
				md.update(data, 0, bytesRead);
			}
			byte[] mdbytes = md.digest();
			input.close();
			
			StringBuffer sb = new StringBuffer("");
			for (int i = 0; i < mdbytes.length; i++) {
				sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			return sb.toString(); 
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
 }
 public static String getChecksum(byte[] data){
	 MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA1");
			md.update(data);
			byte[] mdbytes = md.digest();

			StringBuffer sb = new StringBuffer("");
			for (int i = 0; i < mdbytes.length; i++) {
				sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			return new String(sb);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
 }
}
