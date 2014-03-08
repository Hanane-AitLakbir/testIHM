package com.example.testihm;

import utilities.ComputeChecksum;
import utilities.Packet;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import connection.CloudNotAvailableException;
import connection.ProviderCloud;

public class DownloadActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);
		
		Button testDownload = (Button) findViewById(R.id.testDownloadButton);
		testDownload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ProviderCloud provider = new ProviderCloud("dropbox");
				Packet packet;
				try {
					packet = provider.download("2032.txt");
					TextView zoneText = (TextView) findViewById(R.id.textView1);
					zoneText.setText(new String(packet.getData()));
					System.out.println("computed checksum from downloaded data " + ComputeChecksum.getChecksum(packet.getData()));
					System.out.println("checksum from the metadata "+packet.getMetadata().browse("checksum"));
				} catch (CloudNotAvailableException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.download, menu);
		return true;
	}

}
