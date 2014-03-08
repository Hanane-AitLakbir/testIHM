package com.example.testihm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import metadata.JSonSerializer;
import metadata.Metadata;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class DetailedLocationStorage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detailed_location_storage);
		Bundle bundle = getIntent().getExtras();
		//gets the data sent by the previous activity (OptionsActivity)
		String name = null;
		if(bundle!=null){
			name = bundle.getString("name");
			String type = bundle.getString("type");
		}

		final String nameFinal = name;
		
		Metadata metadataStorageLocation= new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/"+ name + ".json").deserialize();
		
		
		final LinearLayout ll = (LinearLayout) findViewById(R.id.layout_detailed);
		ll.removeAllViews();
		ListView listView = new ListView(getApplicationContext());
		ll.addView(listView);
		ArrayList<HashMap<String, String>> values = new ArrayList<HashMap<String,String>>();

		HashMap<String, String> map = metadataStorageLocation.getMap();
		values.add(map);
		System.out.println(values.toString());
		// Put the data into the list
		ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_2, android.R.id.text1, values);
		listView.setAdapter(adapter);
		
		Button delete = (Button) findViewById(R.id.delete);
		delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//remove the storage location from the list of clouds
				Metadata metadataListCloud = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/list.json").deserialize();
				metadataListCloud.delete(nameFinal);
				metadataListCloud.serialize(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/list.json");
				
				//remove the metadata file
				File file = new File(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/"+nameFinal+".json");
				file.delete();
				
				//return to the previous activity
				Intent intent = new Intent(getApplicationContext(), OptionsActivity.class);
				startActivity(intent);
			}
		});
	
		Button back = (Button) findViewById(R.id.backDetailed);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),OptionsActivity.class);
				startActivity(intent);
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detailed_location_storage, menu);
		return true;
	}

}
