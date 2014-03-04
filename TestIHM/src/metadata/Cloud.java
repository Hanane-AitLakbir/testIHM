package metadata;

/**
 * Mock class to represent the metadata which will be created in the final project
 * @author hanane
 *
 */
public class Cloud {
	private String name,requestToken, accessToken,authorizeToken,upload,download,appKey,appSecret;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRequestToken() {
		return requestToken;
	}

	public void setRequestToken(String requestToken) {
		this.requestToken = requestToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAuthorizeToken() {
		return authorizeToken;
	}

	public void setAuthorizeToken(String authorizeToken) {
		this.authorizeToken = authorizeToken;
	}

	public String getUpload() {
		return upload;
	}

	public void setUpload(String upload) {
		this.upload = upload;
	}

	public String getDownload() {
		return download;
	}

	public void setDownload(String download) {
		this.download = download;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
	
	public String toString(){
		return "name : "+name+"\nrequestToken : " + requestToken +
				"\naccessToken : "+accessToken+"\nauthorizeToken : " +
				authorizeToken + "\nupload : "+ upload+"\n download : " + 
				download + "\n appKey : " + appKey + "\n appSecret : " + 
				appSecret;
	}
	
}
