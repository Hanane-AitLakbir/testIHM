package com.example.testihm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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

		final Metadata metadataStorageLocation= new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/"+ name + ".json").deserialize();

		final LinearLayout ll = (LinearLayout) findViewById(R.id.layout_detailed);
		//ll.removeAllViews();

		ListView listView = new ListView(getApplicationContext());
		ll.addView(listView);
		final ArrayList<HashMap<String, String>> values = new ArrayList<HashMap<String,String>>();

		HashMap<String, String> map = metadataStorageLocation.getMap();
		//values.add(map);
		
		HashMap<String, String> mapTemp = new HashMap<String, String>();
		mapTemp.put("key", "Name");
		mapTemp.put("value", map.get("name"));
		values.add(mapTemp);
		
		HashMap<String, String> mapTemp2 = new HashMap<String, String>();
		mapTemp2.put("key", "Available storage space");
		mapTemp2.put("value", map.get("space"));
		values.add(mapTemp2);

		System.out.println("values Detailed Location Storage" + values.toString());
		// Put the data into the list
		//ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_2, android.R.id.text1,values);
		ListAdapter adapter = new SimpleAdapter(getApplicationContext(), values, android.R.layout.simple_list_item_2, new String[] {"key","value"}, new int[] {android.R.id.text1,android.R.id.text2});
		listView.setAdapter(adapter);


		Button delete = (Button) findViewById(R.id.deleteDetailed);
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
				finish();
			}
		});

		Button back = (Button) findViewById(R.id.backDetailed);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

		Button edit = (Button) findViewById(R.id.editDetailed);
		edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final LinearLayout ll = (LinearLayout) findViewById(R.id.layout_detailed2);
				
				final EditText nameEdit = (EditText) findViewById(R.id.nameEdit);
				nameEdit.setHint(values.get(0).get("value"));
				
				final EditText availableSpaceEdit = (EditText) findViewById(R.id.availableSpaceEdit);
				availableSpaceEdit.setHint(values.get(1).get("value"));
				
				final RadioButton gigaByte = (RadioButton) findViewById(R.id.gigaByteEdit);
				final RadioButton megaByte = (RadioButton) findViewById(R.id.megaByteEdit);
				
				Button update = (Button) findViewById(R.id.updateDetailed);
				update.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						String inputName = nameEdit.getText().toString(); 
						String inputSpace = availableSpaceEdit.getText().toString();
						//update the metadata of the cloud
						
						if(inputSpace.length()!=0){
							long availableSpaceLong=0;
							if(megaByte.isChecked()){
								availableSpaceLong = Long.parseLong(inputSpace)*((long)1024*1024);
							}else if(gigaByte.isChecked()){
								availableSpaceLong = Long.parseLong(inputSpace)*((long)1024*1024*1024);
							}
							metadataStorageLocation.addContent("space", String.valueOf(availableSpaceLong));
						}
						if(inputName.length()!=0){
							metadataStorageLocation.addContent("name", inputName);
							
							//update the name in the list of clouds
							Metadata metadataListCloud = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/list.json").deserialize();
							String type = metadataListCloud.browse(nameFinal);
							
							metadataListCloud.delete(nameFinal);
							metadataListCloud.addContent(inputName, type);
							metadataListCloud.serialize(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/list.json");
						}else{
							inputName = nameFinal;
						}
						
						
						metadataStorageLocation.serialize(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/"+ inputName + ".json");
						
						Toast.makeText(getApplicationContext(), "Update is succesful", Toast.LENGTH_LONG).show();
						finish();
					}
				});
				
				ll.setVisibility(LinearLayout.VISIBLE);
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
