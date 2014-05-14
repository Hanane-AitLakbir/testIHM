package com.example.testihm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import metadata.JSonSerializer;
import metadata.Metadata;
import allocation.UploadStrategy;
import allocation.ChosenCloud;
import allocation.InvalidParameterException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import coding.Coder;
import coding.EmptyCoder;


public class ConfigUploadActivity extends Activity {

	String fileToUpload;
	int nbPackets=1; //default value 1 
	ArrayList<String> chosenCloudsList = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config_upload);
		//context=this;

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
			//populate the layout with one check box for each available storage location
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
		final Button send = (Button) findViewById(R.id.buttonSendFile);
		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				final ProgressDialog progress = ProgressDialog.show(ConfigUploadActivity.this, getResources().getString(R.string.messageSending),"We (actually I am alone...) are uploading the file ...", true);
				
				Thread t = new Thread(new Runnable() {
					
					@Override
					public void run() {
						EditText inputNbPacket = (EditText) findViewById(R.id.inputChoiceNbPackets);
						System.out.println("$"+inputNbPacket.getText().toString()+"$");
						if(inputNbPacket.getText().toString().length()!=0){
							nbPackets=Integer.parseInt(inputNbPacket.getText().toString());
						}
						System.out.println(nbPackets);
						Coder coder = new EmptyCoder();
						UploadStrategy strategy = new ChosenCloud();

						String[] clouds = new String[chosenCloudsList.size()];
						int i=0;
						for(String c : chosenCloudsList){
							clouds[i]=c;
							i++;
						}
						System.out.println(clouds.toString());

						try {
							strategy.upLoad(fileToUpload,clouds, coder);

						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (InvalidParameterException e) {
							e.printStackTrace();
						}
						progress.dismiss();
						finish();
					}
				});
				t.start();				
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
