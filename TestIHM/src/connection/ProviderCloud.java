package connection;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import com.example.testihm.AddLocationStorage;

import metadata.JSonSerializer;
import metadata.Metadata;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import utilities.Packet;
import android.os.Environment;


public class ProviderCloud implements Provider{

	private String nameCloud; 
	private String type;
	public static boolean connected = false;

	public ProviderCloud(String nameCloud,String type) {
		this.nameCloud = nameCloud;
		this.type = type;
	}

	public String getUrl(){
		Metadata metadata = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/"+nameCloud+".json").deserialize();
		OAuthConsumer consumer = new DefaultOAuthConsumer(metadata.browse("app_key"), metadata.browse("app_secret"));
		Metadata metaPattern = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/"+type+"Pattern.json").deserialize();

		OAuthProvider provider = new DefaultOAuthProvider(
				metaPattern.browse("requestToken"),
				metaPattern.browse("accessToken"),
				metaPattern.browse("authorize"));

		System.out.println("Fetching request token...");

		try {
			String authUrl = provider.retrieveRequestToken(consumer, "");
			//authUrl = "http://www.google.fr";
			return authUrl;
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthNotAuthorizedException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void connect(WebBrowserOpener webBrowserOpener) throws CloudNotAvailableException {
		Metadata metadata = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/"+nameCloud+".json").deserialize();
		//launch connection request only if the tokens are empty (ie first connection) 
		//if(metadata.browse("TokenS")==null || metadata.browse("TokenA")==null){
		OAuthConsumer consumer = new DefaultOAuthConsumer(metadata.browse("app_key"), metadata.browse("app_secret"));
		Metadata metaPattern = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/"+type+"Pattern.json").deserialize();

		OAuthProvider provider = new DefaultOAuthProvider(
				metaPattern.browse("requestToken"),
				metaPattern.browse("accessToken"),
				metaPattern.browse("authorize"));

		System.out.println("Fetching request token...");

		String authUrl;

		try {
			authUrl = provider.retrieveRequestToken(consumer, "");
			System.out.println("Now visit:\n" + authUrl + "\n... and grant this app authorization");
			//java.awt.Desktop.getDesktop().browse(java.net.URI.create(authUrl)); //to open the web browser and the website page ;p
			//add opening of the web browser in Android  
			//if(webBrowserOpener!=null) webBrowserOpener.openWebBrowser(authUrl);

			System.out.println("Hit ENTER when you're done:");
			//while(!connected){}

			String verificationCode = "";

			System.out.println("Fetching access token...");

			provider.retrieveAccessToken(consumer, verificationCode.trim());
			metadata.addContent("tokenA", consumer.getToken());
			metadata.addContent("tokenS", consumer.getTokenSecret());
			metadata.serialize(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/"+nameCloud+".json");

		} catch (OAuthMessageSignerException e) {
			throw new CloudNotAvailableException();
		} catch (OAuthNotAuthorizedException e) {
			throw new CloudNotAvailableException();
		} catch (OAuthExpectationFailedException e) {
			throw new CloudNotAvailableException();
		} catch (OAuthCommunicationException e) {
			throw new CloudNotAvailableException();
		}

	}

	@Override
	public void upload(Packet packet) throws CloudNotAvailableException {
		URL url,url2;
		Metadata metadata = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/"+nameCloud+".json").deserialize();
		OAuthConsumer consumer = new DefaultOAuthConsumer(metadata.browse("app_key"),metadata.browse("app_secret"));
		consumer.setTokenWithSecret(metadata.browse("tokenA"), metadata.browse("tokenS"));
		Metadata metaPattern = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/"+type+"Pattern.json").deserialize();
		long packetSize=0,metadataSize=0;

		try {
			//test if "/" is contained in packet.getName() because the folder will not be created in the cloud
			String simpleName=packet.getName();
//			if(packet.getName().contains("/")){
//				simpleName=packet.getName().substring(packet.getName().lastIndexOf("/")+1);
//			}else{
//				simpleName=packet.getName();
//			}
			//System.out.println("simple name "+simpleName);

			//System.out.println(metaPattern==null);
			url = new URL(metaPattern.browse("upload")+ simpleName+"?param=UTF-8");
			url2 = new URL(metaPattern.browse("upload")+ simpleName+".json?param=UTF-8");

			HttpURLConnection request = (HttpURLConnection) url.openConnection();
			HttpURLConnection request2 = (HttpURLConnection) url2.openConnection();

			request.setDoOutput(true);
			request2.setDoOutput(true);

			request.setRequestMethod("PUT");
			request.addRequestProperty("locale", "");
			request.addRequestProperty("overwrite", "");
			request.addRequestProperty("parent_rev", "");
			request.addRequestProperty("content-Type", "");

			request2.setRequestMethod("PUT");
			request2.addRequestProperty("locale", "");
			request2.addRequestProperty("overwrite", "");
			request2.addRequestProperty("parent_rev", "");
			request2.addRequestProperty("content-Type", "");

			consumer.sign(request);
			consumer.sign(request2);

			System.out.println("Sending request...");

			DataOutputStream outputStream = new DataOutputStream(request.getOutputStream());
			byte[] data = packet.getData();
			packetSize = (long)data.length; //get the size of the packet
			outputStream.write(data); //sends the rest of the file
			System.out.println("Response: " + request.getResponseCode() + " "+ request.getResponseMessage());
			outputStream.close();


			//File mFile = File.createTempFile("meta", ".tmp");
			File mFile = new File(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/file/meta.json");
			packet.getMetadata().serialize(mFile.getPath());

			DataOutputStream metaStream = new  DataOutputStream(request2.getOutputStream());
			FileInputStream mFileInput = new FileInputStream(mFile);

			byte[] buffer = new byte[1024];
			ByteArrayOutputStream ous = new ByteArrayOutputStream();
			int read = 0;
			while ( (read = mFileInput.read(buffer)) != -1 ) {
				ous.write(buffer, 0, read);
			}
			metadataSize=buffer.length;

			//metaStream.write(Files.readAllBytes(Paths.get(mFile.getPath()))); //sends the rest of the file
			metaStream.write(ous.toByteArray()); //sends the rest of the file
			System.out.println("Response: " + request2.getResponseCode() + " "+ request2.getResponseMessage());
			metaStream.close();
			System.out.println("OK");

			//update metadata : space available
			System.out.println(metadata.browse("space"));
			long previousAvailableSpace=Long.parseLong(metadata.browse("space"));
			long newAvailableSpace = previousAvailableSpace-packetSize-metadataSize;
			metadata.addContent("space", String.valueOf(newAvailableSpace));

		} catch (MalformedURLException e) {
			System.out.println("MalformedURLException");
			throw new CloudNotAvailableException();
		} catch (IOException e) {
			System.out.println("IOException");
			throw new CloudNotAvailableException();
		} catch (OAuthMessageSignerException e) {
			System.out.println("OAuthMessageSignerException");
			throw new CloudNotAvailableException();
		} catch (OAuthExpectationFailedException e) {
			System.out.println("OAuthExpectationFailedException");
			throw new CloudNotAvailableException();
		} catch (OAuthCommunicationException e) {
			System.out.println("OAuthCommunicationException");
			throw new CloudNotAvailableException();
		}


	}

	@Override
	public Packet download(String name) throws CloudNotAvailableException{

		Metadata metadata = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/"+nameCloud+".json").deserialize();
		URL url,url2;
		Packet packet=null;
		Metadata metaPattern = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/"+type+"Pattern.json").deserialize();
		try {
			System.out.println("Provider Cloud name "+name);
			url = new URL(metaPattern.browse("download")+name);
			url2 =new URL(metaPattern.browse("download")+name+".json");

			HttpURLConnection request = (HttpURLConnection) url.openConnection();
			HttpURLConnection request2 = (HttpURLConnection) url2.openConnection();

			OAuthConsumer consumer = new DefaultOAuthConsumer(metadata.browse("app_key"),metadata.browse("app_secret"));
			consumer.setTokenWithSecret(metadata.browse("tokenA"), metadata.browse("tokenS"));

			request.setDoInput(true);
			request2.setDoInput(true);

			request.setRequestMethod("GET");
			request.addRequestProperty("rev", "");

			request2.setRequestMethod("GET");
			request2.addRequestProperty("rev", "");

			consumer.sign(request);
			consumer.sign(request2);

			System.out.println("Sending request...");

			request.connect();
			request2.connect();
			System.out.println("Connect ok...");

			System.out.println("Response: " + request.getResponseCode() + " "
					+ request.getResponseMessage());

			//			InputStreamReader readerError = new InputStreamReader(request.getErrorStream());
			//			BufferedReader bufferError = new BufferedReader(readerError);
			//			String line;
			//			while((line=bufferError.readLine())!=null){
			//				System.out.println(line);	
			//			}

			DataInputStream inputStream = new DataInputStream(request.getInputStream());
			DataInputStream metaStream = new DataInputStream(request2.getInputStream());
			System.out.println("inputStream created ok...");

			packet = new Packet(name,toByteArray(inputStream));
			packet.setMetadata(new JSonSerializer().deserializeStream(metaStream));

			System.out.println("Response: " + request.getResponseCode() + " "
					+ request.getResponseMessage());

		} catch (MalformedURLException e) {
			System.out.println("MalformedURLException");
			throw new CloudNotAvailableException();

		} catch (ProtocolException e) {
			System.out.println("ProtocolException");
			throw new CloudNotAvailableException();

		} catch (OAuthMessageSignerException e) {
			System.out.println("OAuthMessageSignerException");
			throw new CloudNotAvailableException();

		} catch (OAuthExpectationFailedException e) {
			System.out.println("OAuthExpectationFailedException");
			throw new CloudNotAvailableException();

		} catch (OAuthCommunicationException e) {
			System.out.println("OAuthCommunicationException");
			throw new CloudNotAvailableException();

		}
		catch (IOException e) {
			System.out.println("IOException");
			throw new CloudNotAvailableException();

		}

		//System.out.println("ProviderCloud : " + packet.getData().length);
		return packet;
	}

	private byte[] toByteArray(DataInputStream is) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		int reads = is.read();
		while(reads != -1){ 
			baos.write(reads); 
			reads = is.read();
		}
		return baos.toByteArray();
	}

	/**
	 * Creates a folder in the root "dropbox". If the folder with the same name exists, the method will do nothing.
	 * Nota : if folder exists, if you call folder/subfolder, the folder "subfolder" will be created in "folder"
	 */
	public void createFolder(String nameFolder){

		String safeName = nameFolder.replaceAll("/", "_").replaceAll(" ", "_").replace("\\", "_");

		Metadata metadata = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/"+nameCloud+".json").deserialize();
		OAuthConsumer consumer = new DefaultOAuthConsumer(metadata.browse("app_key"),metadata.browse("app_secret"));
		consumer.setTokenWithSecret(metadata.browse("tokenA"), metadata.browse("tokenS"));
		Metadata metaPattern = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/"+type+"Pattern.json").deserialize();

		Metadata list;

		String nameFile = safeName.substring(0, nameFolder.lastIndexOf("@"));
		String checksum = safeName.substring(nameFolder.lastIndexOf("@")+1);

		System.out.println("providerwebdav : " + nameFile + " " + checksum);

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
		

		URL url;
		try {
			//creation of a folder
			url = new URL(metaPattern.browse("create_folder")+safeName);
			HttpURLConnection request = (HttpURLConnection) url.openConnection();

			request.setDoOutput(true);

			request.setRequestMethod("POST");

			consumer.sign(request);
			request.connect();
			System.out.println("Response: " + request.getResponseCode() + " "+ request.getResponseMessage());
			
			//update of the list
			list.addContent(nameFile, checksum);
			
			//Upload the metadata to the cloud
			String mPath =  Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/temp.json";
			list.serialize(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/temp.json");
			File mFile = new File(mPath);
			byte[] b = new byte[(int)mFile.length()];
			new FileInputStream(mFile).read(b);
			Packet listPacket = new Packet("list.json", b);
			listPacket.setMetadata(new Metadata());
			upload(listPacket);
			mFile.delete();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CloudNotAvailableException e) {
			e.printStackTrace();
		}
	}
}
