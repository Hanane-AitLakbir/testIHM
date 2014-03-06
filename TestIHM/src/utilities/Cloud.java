package utilities;

public enum Cloud {
	DBX("https://api.dropbox.com/1/oauth/request_token",
			"https://api.dropbox.com/1/oauth/access_token",
			"https://www.dropbox.com/1/oauth/authorize","fg5jq4pn2dc6vk3","8xs4ulixs6pii08");
	
	private final String requestToken,accessToken,authorize,appKey, appSecret;
	private String accessTok, tokenSecret;

	Cloud(String requestToken,String accessToken,String authorize,String appKey,String appSecret){
		this.requestToken = requestToken;
		this.accessToken = accessToken;
		this.authorize = authorize;
		this.appKey = appKey;
		this.appSecret = appSecret;
	}
	
	public String[] getURL(){
		String[] urls = new String[3];
		urls[0] = requestToken;
		urls[1] = accessToken;
		urls[2] = authorize;
		return urls;
	}
	
	public void setAccesTok(String accesTok){
		this.accessTok = accesTok;
	}
	
	public void setTokenSecret(String secret){
		this.tokenSecret = secret;
	}
	
	public boolean isAuthenticated(){
		return (tokenSecret==null && accessTok==null) ? false : true;
	}
	
	
}
