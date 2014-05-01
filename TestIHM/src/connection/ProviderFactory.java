package connection;

import android.os.Environment;
import metadata.JSonSerializer;
import metadata.Metadata;
import utilities.Packet;

public class ProviderFactory {

	static public Provider getProvider(String name){
		Metadata meta = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/list.json").deserialize();
		//System.out.println((meta.browse(name)));
		if(meta.browse(name).equals("dropbox")){
			System.out.println("Provider factory "+name);
			Provider provider =  new ProviderCloud(name,"dropbox");
			//provider.connect();
			return provider;
		}else if(meta.browse(name).equals("webdav")){
			Provider provider = new ProviderWebdav(name);
			try {
				provider.connect(null);
				return provider;
			} catch (CloudNotAvailableException e) {
				e.printStackTrace();
			}
		}
		return null;
		
		//		else if(meta.browse(name).equals("webdav")){
		//			Provider provider = new ProviderWebdav(name);
		//			try {
		//				provider.connect();
		//				return provider;
		//			} catch (CloudNotAvailableException e) {
		//				e.printStackTrace();
		//			}
		//			
		//		}

	}

	public static void main(String[] args) {
		Provider provider = ProviderFactory.getProvider("account1");
		Packet packet;
		try {
			provider.connect(null);
			packet = provider.download("firstFileUploaded.txt");
			System.out.println( new String(packet.getData()));
		} catch (CloudNotAvailableException e) {
			e.printStackTrace();
		}
	}

}
