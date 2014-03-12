package com.example.testihm;

import java.util.ArrayList;
import java.util.Set;

import metadata.JSonSerializer;
import metadata.Metadata;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ConfigUploadActivity extends Activity {

	private String fileToUpload;
	private ArrayList<String> chosenCloudsList = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config_upload);
		
		Bundle bundle = getIntent().getExtras();
		if(bundle!=null){
			fileToUpload = bundle.getString("fileToUpload");
		}
		
		TextView displayFileName = (TextView) findViewById(R.id.name_fileToUpload);
		displayFileName.setText(fileToUpload);;
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.layout_config_upload);
		
		Metadata metaCloud = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/list.json").deserialize();
		Set<String> listCloud = metaCloud.getMap().keySet();
		for(String c : listCloud){
			//populate the layout with one check box for each available storage ocation
			CheckBox checkbox = new CheckBox(getApplicationContext());
			checkbox.setText(c);
			//add the listener
			checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					//populate the arrayList of the chosen clouds 
					if(buttonView.isChecked()){
						chosenCloudsList.add(buttonView.getText().toString());
					}else{
						chosenCloudsList.remove(buttonView.getText().toString());
					}
				}
			});
			layout.addView(checkbox);
		}
		
		//TODO add the choice of allocationStrategy
		
		
		//send button : creation an allocationStrategy object
		Button send = (Button) findViewById(R.id.buttonSendFile);
		send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Toast.makeText(getApplicationContext(), chosenCloudsList.toString(), Toast.LENGTH_SHORT).show();
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.config_upload, menu);
		return true;
	}

}
