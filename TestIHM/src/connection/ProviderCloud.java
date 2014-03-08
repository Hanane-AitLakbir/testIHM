package connection;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

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

	public ProviderCloud(String nameCloud) {
		this.nameCloud = nameCloud;
	}

	@Override
	public void connect() throws CloudNotAvailableException {
		Metadata metadata = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/"+nameCloud+".json").deserialize();
		//launch connection request only if the tokens are empty (ie first connection) 
		if(metadata.browse("TokenS")==null || metadata.browse("TokenA")==null){
			OAuthConsumer consumer = new DefaultOAuthConsumer(metadata.browse("app_key"), metadata.browse("app_secret"));

			OAuthProvider provider = new DefaultOAuthProvider(
					metadata.browse("requestToken"),
					metadata.browse("accessToken"),
					metadata.browse("authorize"));

			System.out.println("Fetching request token...");

			String authUrl;

			try {
				authUrl = provider.retrieveRequestToken(consumer, "");
				System.out.println("Now visit:\n" + authUrl + "\n... and grant this app authorization");
				//java.awt.Desktop.getDesktop().browse(java.net.URI.create(authUrl)); //to open the web browser and the website page ;p
				//add opening of the web browser in Android  

				System.out.println("Hit ENTER when you're done:");

				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				String verificationCode = br.readLine();

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
			} catch (IOException e) {
				throw new CloudNotAvailableException();
			}
		}

	}

	@Override
	public void upload(Packet packet) throws CloudNotAvailableException {
		URL url,url2;
		Metadata metadata = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/"+nameCloud+".json").deserialize();
		OAuthConsumer consumer = new DefaultOAuthConsumer(metadata.browse("app_key"),metadata.browse("app_secret"));
		consumer.setTokenWithSecret(metadata.browse("tokenA"), metadata.browse("tokenS"));
		try {
			url = new URL(metadata.browse("upload")+ packet.getName()+"?param=UTF-8");
			url2 = new URL(metadata.browse("upload")+ packet.getName()+".json?param=UTF-8");

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

			//			System.out.println(request.getRequestProperties().toString());
			//			System.out.println(request2.getRequestProperties().toString());

			System.out.println("Sending request...");

			System.out.println("request.getOS == null : " + request.getOutputStream()==null);
			DataOutputStream outputStream = new DataOutputStream(request.getOutputStream());

			System.out.println("outputStream == null : "+ outputStream==null);

			System.out.println("packet content : "+new String(packet.getData()));

			outputStream.write(packet.getData()); //sends the rest of the file
			System.out.println("Response: " + request.getResponseCode() + " "
					+ request.getResponseMessage());
//			InputStreamReader readerError = new InputStreamReader(request.getErrorStream());
//			BufferedReader bufferError = new BufferedReader(readerError);
//			String line;
//			while((line=bufferError.readLine())!=null){
//				System.out.println(line);	
//			}
			outputStream.close();

			File mFile = File.createTempFile("meta", ".tmp");
			//File mFile = new File(Environment.getExternalStorageDirectory().getPath()+"/pip/meta.json");
			packet.getMetadata().serialize(mFile.getPath());

			DataOutputStream metaStream = new  DataOutputStream(request2.getOutputStream());
			FileInputStream mFileInput = new FileInputStream(mFile);

			byte[] buffer = new byte[4096];
			ByteArrayOutputStream ous = new ByteArrayOutputStream();
			int read = 0;
			while ( (read = mFileInput.read(buffer)) != -1 ) {
				ous.write(buffer, 0, read);
			}

			//metaStream.write(Files.readAllBytes(Paths.get(mFile.getPath()))); //sends the rest of the file
			metaStream.write(ous.toByteArray()); //sends the rest of the file
			System.out.println("Response: " + request2.getResponseCode() + " "
					+ request2.getResponseMessage());
			metaStream.close();

		} catch (MalformedURLException e) {
			throw new CloudNotAvailableException();
		} catch (IOException e) {
			throw new CloudNotAvailableException();
		} catch (OAuthMessageSignerException e) {
			
			throw new CloudNotAvailableException();
		} catch (OAuthExpectationFailedException e) {
			
			throw new CloudNotAvailableException();
		} catch (OAuthCommunicationException e) {
			
			throw new CloudNotAvailableException();
		}


	}

	@Override
	public Packet download(String name) throws CloudNotAvailableException{

		Metadata metadata = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/"+nameCloud+".json").deserialize();
		URL url,url2;
		Packet packet=null;
		try {
			url = new URL(metadata.browse("download")+name);
			url2 =new URL(metadata.browse("download")+name+".json");

			HttpURLConnection request = (HttpURLConnection) url.openConnection();
			HttpURLConnection request2 = (HttpURLConnection) url2.openConnection();

			OAuthConsumer consumer = new DefaultOAuthConsumer(metadata.browse("app_key"),metadata.browse("app_secret"));
			consumer.setTokenWithSecret(metadata.browse("tokenA"), metadata.browse("tokenS"));

			request.setDoOutput(true);
			request2.setDoOutput(true);

			request.setRequestMethod("GET");
			request.addRequestProperty("rev", "");

			request2.setRequestMethod("GET");
			request2.addRequestProperty("rev", "");

			consumer.sign(request);
			consumer.sign(request2);
			System.out.println("Sending request...");
			request.connect();
			request2.connect();

			DataInputStream inputStream = new DataInputStream(request.getInputStream());
			DataInputStream metaStream = new DataInputStream(request2.getInputStream());

			packet = new Packet(name,toByteArray(inputStream));
			packet.setMetadata(new JSonSerializer().deserializeStream(metaStream));

			System.out.println("Response: " + request.getResponseCode() + " "
					+ request.getResponseMessage());

		} catch (MalformedURLException e) {
			throw new CloudNotAvailableException();
		} catch (ProtocolException e) {
			throw new CloudNotAvailableException();
		} catch (OAuthMessageSignerException e) {
			throw new CloudNotAvailableException();
		} catch (OAuthExpectationFailedException e) {
			throw new CloudNotAvailableException();
		} catch (OAuthCommunicationException e) {
			throw new CloudNotAvailableException();
		} catch (IOException e) {
			throw new CloudNotAvailableException();
		}


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
}
