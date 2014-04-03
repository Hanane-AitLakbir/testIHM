package com.example.testihm;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;
import connection.CloudNotAvailableException;
import connection.Provider;
import connection.ProviderCloud;
import connection.ProviderFactory;
import connection.WebBrowserOpener;

public class ConnectionActivity extends Activity implements WebBrowserOpener{

	private WebView webView;
	private WebBrowserOpener me;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connection);
		Bundle bundle = getIntent().getExtras();
		final String name = bundle.getString("name");
		me=this;
		
		//webView = (WebView) findViewById(R.id.webview);
		webView = new WebView(getApplicationContext());
		LayoutParams params = new LayoutParams(10000,400);
		params.setMargins(10, 30, 10, 10);
		webView.setLayoutParams(params);

		//webView.loadUrl("http://www.google.fr");
		LinearLayout ll = (LinearLayout) findViewById(R.id.layoutConnection);
		ll.addView(webView);
		
	
//		Provider provider = ProviderFactory.getProvider(name);
//		try {
//			provider.connect(this);
//		} catch (CloudNotAvailableException e) {
//			e.printStackTrace();
//		}
		
		Button okButton = (Button) findViewById(R.id.okButton);
		okButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Provider provider = ProviderFactory.getProvider(name);
				try {
					provider.connect(me);
				} catch (CloudNotAvailableException e) {
					e.printStackTrace();
				}
			}
		});
		
		Button connectionButton = (Button) findViewById(R.id.connectButton);
		connectionButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Provider provider = ProviderFactory.getProvider(name);
				String url = provider.getUrl();
				//System.out.println(url);
				webView.loadUrl(url);
				webView.setHorizontalScrollBarEnabled(true);
				webView.setVerticalScrollBarEnabled(true);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.connection, menu);
		return true;
	}

	public void openWebBrowser(String url){
//		Intent intent = new Intent(Intent.ACTION_VIEW);
//		intent.setData(Uri.parse(url));
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		getApplicationContext().startActivity(intent);
		webView.loadUrl(url);
	}
}
