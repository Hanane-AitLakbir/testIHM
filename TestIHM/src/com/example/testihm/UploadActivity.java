package com.example.testihm;

import utilities.Packet;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import connection.CloudNotAvailableException;
import connection.Provider;
import connection.ProviderCloud;

public class UploadActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);
		Button testUpload = (Button) findViewById(R.id.testUpload);
		testUpload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				byte[] data = "Aujourd'hui il fait beau".getBytes();
				Packet packet = new Packet("YouGonnaToWorkF__kingAndroid.txt", data);
				Provider provider = new ProviderCloud("dropbox");
				try {
					provider.upload(packet);
					//System.out.println("packet uploaded");
				} catch (CloudNotAvailableException e) {
					Toast.makeText(getApplicationContext(), "not working", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.upload, menu);
		return true;
	}

}
