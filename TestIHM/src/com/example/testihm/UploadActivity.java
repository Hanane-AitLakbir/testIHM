package com.example.testihm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import utilities.Packet;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import connection.CloudNotAvailableException;
import connection.Provider;
import connection.ProviderCloud;

public class UploadActivity extends Activity {

	private List<String> item = null;
	private List<String> path = null;
	private String root;
	private TextView myPath;
	private String selectedFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);

		myPath = (TextView)findViewById(R.id.path);

		//button to test uploading
		Button testUpload = (Button) findViewById(R.id.testUpload);
		testUpload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				byte[] data = "Aujourd'hui il fait beau".getBytes();
				Packet packet = new Packet("YouGonnaToWorkF__kingAndroid2.txt", data);
				Provider provider = new ProviderCloud("dropbox","dropbox");
				try {
					provider.upload(packet);
					//System.out.println("packet uploaded");
				} catch (CloudNotAvailableException e) {
					Toast.makeText(getApplicationContext(), "not working", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}

			}
		});

		final ListView listFile = (ListView) findViewById(R.id.list);

		//		ListView listview = (ListView) findViewById(android.R.id.list);

		listFile.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
				//Toast.makeText(getApplicationContext(), path.get(position), Toast.LENGTH_LONG).show();
				Toast.makeText(getApplicationContext(),"long click",Toast.LENGTH_LONG).show();
				//useless for now : purpose = add the drag and drop effect
				return true;
			}
		});

		root = Environment.getExternalStorageDirectory().getPath();
		getDir(root,listFile);

		listFile.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				File file = new File(path.get(position));

				if (file.isDirectory())
				{
					if(file.canRead()){
						getDir(path.get(position),listFile);
					}else{
						Toast.makeText(getApplicationContext(), "You can't read this file.", Toast.LENGTH_SHORT).show();
					} 
				}else {
					Toast.makeText(getApplicationContext(), "["+file.getName()+"]", Toast.LENGTH_SHORT).show();
					TextView textView = (TextView) findViewById(R.id.fileToUpload);
					textView.setText(file.getName());
					selectedFile = file.getPath();
				}
			}
		});
		
		//Add the button "Next" listener to go to the config activity for uploading (allocation strategy, clouds name,...)
		Button toConfigUpload = (Button) findViewById(R.id.toUploadOptions);
		toConfigUpload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(UploadActivity.this,ConfigUploadActivity.class);
				intent.putExtra("fileToUpload",selectedFile);
				startActivity(intent);
			}
		});
		
		Button returnButton = (Button) findViewById(R.id.returnUpload);
		returnButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.upload, menu);
		return true;
	}

	private void getDir(String dirPath,ListView list)
	{
		myPath.setText("Location: " + dirPath);
		item = new ArrayList<String>();
		path = new ArrayList<String>();
		File f = new File(dirPath);
		File[] files = f.listFiles();

		if(!dirPath.equals(root))
		{
			item.add(root);
			path.add(root);
			item.add("../");
			path.add(f.getParent()); 
		}

		for(int i=0; i < files.length; i++)
		{
			File file = files[i];

			if(!file.isHidden() && file.canRead()){
				path.add(file.getPath());
				if(file.isDirectory()){
					item.add(file.getName() + "/");
				}else{
					item.add(file.getName());
				}
			} 
		}

		ArrayAdapter<String> fileList =	new ArrayAdapter<String>(getApplicationContext(), R.layout.row, item);
		list.setAdapter(fileList); 
	}

}
