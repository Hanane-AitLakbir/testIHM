package connection;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;

import metadata.JSonSerializer;
import metadata.Metadata;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.jackrabbit.webdav.client.methods.MkColMethod;
import org.apache.jackrabbit.webdav.client.methods.PutMethod;

import utilities.Packet;
import android.os.Environment;

public class ProviderWebdav implements Provider {

	private HttpClient client;
	private String serverName;

	public ProviderWebdav(String serverName){
		this.serverName = serverName;
	}

	@Override
	public void connect(WebBrowserOpener webBrowserOpener) throws CloudNotAvailableException {
		Metadata metadata = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/"+serverName+".json").deserialize();
		
		HostConfiguration hostConfig = new HostConfiguration();
		hostConfig.setHost(metadata.browse("URLServer")); 
		HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams params = new HttpConnectionManagerParams();
		int maxHostConnections = 20;
		params.setMaxConnectionsPerHost(hostConfig, maxHostConnections);
		connectionManager.setParams(params);    
		client = new HttpClient(connectionManager);
	
		Credentials creds = new UsernamePasswordCredentials(metadata.browse("login"), metadata.browse("password"));
		client.getState().setCredentials(AuthScope.ANY, creds);
		client.setHostConfiguration(hostConfig);
	}

	@Override
	public void upload(Packet packet) throws CloudNotAvailableException{
		Metadata metadata = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/"+serverName+".json").deserialize();
		File mFile;
		String simpleName=packet.getName();
//		if(packet.getName().contains("/")){
//			simpleName=packet.getName().substring(packet.getName().lastIndexOf("/")+1);
//		}else{
//			simpleName=packet.getName();
//		}
		
		PutMethod upload = new PutMethod(metadata.browse("URLServer") + simpleName);
		PutMethod uploadMeta = new PutMethod(metadata.browse("URLServer") + simpleName+".json");
		System.out.println("ok put method");
		File f;
		try {
			mFile = new File(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/file/meta.json");
			packet.getMetadata().serialize(mFile.getPath());

			f = new File(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/file/temp"+packet.getExtension());

			
			FileOutputStream output = new FileOutputStream(f);
			System.out.println("ok output stream");
			
			output.write(packet.getData());
			output.close();

			System.out.println("ok creation temp file");
			if(f.exists() && mFile.exists()) {
				//Sending of the packet file
				RequestEntity requestEntity = new FileRequestEntity(f,packet.getExtension());
				upload.setRequestEntity(requestEntity);
				client.executeMethod(upload);
				System.out.println(upload.getStatusCode() + " "+ upload.getStatusText());
				upload.releaseConnection();
				//Sending of the metadata file
				RequestEntity requestEntity2 = new FileRequestEntity(mFile,".json");
				uploadMeta.setRequestEntity(requestEntity2);
				client.executeMethod(uploadMeta);
				System.out.println(uploadMeta.getStatusCode() + " "+ uploadMeta.getStatusText());
				uploadMeta.releaseConnection();
			}
			f.delete();
			
		} catch (IOException e1) {
			System.out.println("problem upload");
			throw new CloudNotAvailableException();
		}


	}

	@Override
	public Packet download(String name) throws CloudNotAvailableException{
		Metadata metadata = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/"+serverName+".json").deserialize();
		Packet packet=null;
		String url = metadata.browse("URLServer")+name;
		System.out.println(url);
		GetMethod httpMethod = new GetMethod(url);
		try {
			client.executeMethod(httpMethod);
			System.out.println(httpMethod.getStatusCode() + " "+ httpMethod.getStatusText());
			System.out.println("content response : " + httpMethod.getResponseContentLength());
			ArrayList<Byte> dataList = new ArrayList<Byte>();

			if (httpMethod.getResponseContentLength() > 0) {
				InputStream inputStream = httpMethod.getResponseBodyAsStream();
				byte buf[]=new byte[1024];
				int len;
				while ( (len = inputStream.read(buf)) > 0 ) {
					for(byte b : buf){
						dataList.add(b);
					}
				}
				inputStream.close();
			}
			byte[] data = new byte[dataList.size()];
			for(int i = 0;i<data.length;i++){
				data[i] = dataList.get(i);
			}
			packet = new Packet(name.substring(0, name.lastIndexOf('.')),data);
		} catch (HttpException e) {
			throw new CloudNotAvailableException();
		} catch (IOException e) {
			throw new CloudNotAvailableException();
		}
		
		return packet;
	}

	@Override
	public String getUrl() {
		
		return null;
	}

	@Override
	public void createFolder(String nameFolder, String fileChecksum) {
		System.out.println("provider webdav : create_folder method");
		String safeName = nameFolder.replaceAll("/", "_").replaceAll(" ", "_").replace("\\", "_");
		Metadata metadata = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/"+serverName+".json").deserialize();
		MkColMethod uploadFolder = new MkColMethod(metadata.browse("URLServer")+safeName);
		Metadata list;
		
		System.out.println("providerwebdav : " + safeName + " " + fileChecksum);
		
		//Get the list of files
		try {
			Packet listPacket = download("list.json");
			InputStream stream = new ByteArrayInputStream(listPacket.getData());
			list = new JSonSerializer().deserializeStream(stream);
			if(list==null){
				list=new Metadata();
			}
		} catch (CloudNotAvailableException e1) {
			//if the file doesn't exist
			System.out.println("no list.json");
			list = new Metadata();
		}
		
		try {			
			//creates the folder and update the metadata
			client.executeMethod(uploadFolder);
			list.addContent(safeName, fileChecksum);
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Upload the metadata to the cloud
		String mPath =  Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/temp.json";
		list.serialize(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/temp.json");
		File mFile = new File(mPath);
		byte[] b = new byte[(int)mFile.length()];
		try {
			new FileInputStream(mFile).read(b);
			Packet listPacket = new Packet("list.json", b);
			listPacket.setMetadata(new Metadata());
			
			upload(listPacket);
			mFile.delete();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CloudNotAvailableException e) {
			e.printStackTrace();
		}
			
	}

	public String[] getFiles(){
		
		try {
			Packet listPacket = download("list.json");
			InputStream stream = new ByteArrayInputStream(listPacket.getData());
			String metadataPath = Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/temp.json";
			
			Metadata list = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/temp.json").deserializeStream(stream);
			list.serialize(metadataPath);
			
			Set<String> set = list.getMap().keySet();
			new File(metadataPath).delete();
			return set.toArray(new String[set.size()]);
			
		} catch (CloudNotAvailableException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}

