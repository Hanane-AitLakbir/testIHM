package com.example.testihm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import metadata.JSonSerializer;
import metadata.Metadata;
import utilities.ComputeChecksum;
import utilities.Packet;
import allocation.Downloader;
import allocation.UploadStrategy;
import allocation.ChosenCloud;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import coding.Coder;
import coding.EmptyCoder;
import connection.CloudNotAvailableException;
import connection.Provider;
import connection.ProviderCloud;
import connection.ProviderFactory;
import connection.ProviderWebdav;

public class DownloadActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);

		Button testDownload = (Button) findViewById(R.id.testDownloadButton);
		testDownload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//ProviderCloud provider = new ProviderCloud("dropbox","dropbox");
				Provider provider = ProviderFactory.getProvider("account1");
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

		ListView listview = (ListView) findViewById(R.id.listDownload);
		//populate the listvew with the name of files stored onto clouds
		ArrayList<String> values = new ArrayList<String>();

		Metadata metadataListCloud = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/file/files List.json").deserialize();
		HashMap<String, String> map = metadataListCloud.getMap();
		Iterator<String> iterator = map.keySet().iterator();

		while(iterator.hasNext()){
			values.add(iterator.next());
		}

		System.out.println(values.toString());

		// Put the data in the list
		ListAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.row,values);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(final AdapterView<?> parent, View view, final int position,
					long id) {

				final ProgressDialog progress = ProgressDialog.show(DownloadActivity.this, getResources().getString(R.string.messageDownloading),"We (actually I am alone...) are downloading the file ...", true);
				
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						String fileName = parent.getItemAtPosition(position).toString();
						String directory = Environment.getExternalStorageDirectory().getPath() + "/downloadPIP";
						Coder coder = new EmptyCoder();

						try {
							Downloader.downLoad(fileName, directory);
						} catch (IOException e) {
							e.printStackTrace();
						}
						progress.dismiss();
					}
				});
				t.start();
			
				//Toast.makeText(getApplicationContext(), "Downloading is successful", Toast.LENGTH_SHORT).show();
			}
		});

		//add button : return to previous activity
		Button returnButton = (Button) findViewById(R.id.returnDownload);
		returnButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

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
