package com.example.testihm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import metadata.JSonSerializer;
import metadata.Metadata;
import allocation.AllocationStrategy;
import allocation.ChosenCloud;
import allocation.InvalidParameterException;
import android.app.Activity;
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
import android.widget.Toast;
import coding.Coder;
import coding.EmptyCoder;


public class ConfigUploadActivity extends Activity {

	String fileToUpload;
	int nbPackets=1; //default value 1 
	ArrayList<String> chosenCloudsList = new ArrayList<String>();
	//Context context;
	//Activity me = this;
	//attributes to manage the progress bar
	//ProgressDialog progress;



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
		Button send = (Button) findViewById(R.id.buttonSendFile);
		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//Toast.makeText(getApplicationContext(), chosenCloudsList.toString(), Toast.LENGTH_SHORT).show();
				//System.out.println(chosenCloudsList);
				//System.out.println(fileToUpload);
				//progress.show(context, "Please wait","We (actually I am alone...) are uploading the file ...", true);
				//Toast.makeText(getApplicationContext(), "Please wait. \nWe (actually I am alone...) are uploading the file ...", Toast.LENGTH_LONG).show();
				EditText inputNbPacket = (EditText) findViewById(R.id.inputChoiceNbPackets);
				System.out.println("$"+inputNbPacket.getText().toString()+"$");
				if(inputNbPacket.getText().toString().length()!=0){
					nbPackets=Integer.parseInt(inputNbPacket.getText().toString());
				}
				System.out.println(nbPackets);
				Coder coder = new EmptyCoder();
				AllocationStrategy strategy = new ChosenCloud();

				String[] clouds = new String[chosenCloudsList.size()];
				int i=0;
				for(String c : chosenCloudsList){
					clouds[i]=c;
					i++;
				}
				System.out.println(clouds.toString());

				try {
					strategy.upLoad(fileToUpload, nbPackets,clouds, coder);
					//System.out.println("boolean Config Upload :"+finished);

					//sends message "Finished"
					//					msg = mHandler.obtainMessage(MSG_UPLOAD_END);
					//					mHandler.sendMessage(msg);

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InvalidParameterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//				Thread threadUpload = new Thread(new Runnable() {
				//					Message msg = null;
				//					public static final int MSG_UPLOAD = 0;
				//					public static final int MSG_UPLOAD_END = 1;
				//
				//					final Handler mHandler = new Handler() {
				//						public void handleMessage(Message msg) {
				//							switch (msg.what) {
				//							case MSG_UPLOAD:
				//								progress = ProgressDialog.show(me, "Please wait","We (actually I am alone...) are uploading the file ...", true);
				//								//				if (progress.isShowing()) {
				//								//					progress.setMessage(((String) msg.obj));
				//								//				}
				//								//progress.show();
				//								System.out.println("progress is showing ? ConfigUploadActivity :" + progress.isShowing());
				//								break;
				//							case MSG_UPLOAD_END:
				//progress.dismiss();
				//								break;
				//							default:
				//								break;
				//							}
				//						}
				//					};
				//
				//					@Override
				//					public void run() {
				//
				//						//sends message upload is launched
				//						//Message msg = null;
				//						msg = mHandler.obtainMessage(MSG_UPLOAD);
				//						mHandler.sendMessage(msg);
				//
				//						EditText inputNbPacket = (EditText) findViewById(R.id.inputChoiceNbPackets);
				//
				//						nbPackets=Integer.parseInt(inputNbPacket.getText().toString());
				//						System.out.println(nbPackets);
				//						Coder coder = new EmptyCoder();
				//						AllocationStrategy strategy = new ChosenCloud();
				//
				//						String[] clouds = new String[chosenCloudsList.size()];
				//						int i=0;
				//						for(String c : chosenCloudsList){
				//							clouds[i]=c;
				//							i++;
				//						}
				//						System.out.println(clouds.toString());
				//
				//						try {
				//							boolean finished = strategy.upLoad(fileToUpload, nbPackets,clouds, coder);
				//							System.out.println("boolean Config Upload :"+finished);
				//
				//							//sends message "Finished"
				//							msg = mHandler.obtainMessage(MSG_UPLOAD_END);
				//							mHandler.sendMessage(msg);
				//
				//						} catch (FileNotFoundException e) {
				//							e.printStackTrace();
				//						} catch (IOException e) {
				//							e.printStackTrace();
				//						} catch (InvalidParameterException e) {
				//							// TODO Auto-generated catch block
				//							e.printStackTrace();
				//						}
				//
				//					}
				//				});
				//				threadUpload.start();
				//				try {
				//					threadUpload.join();
				//				} catch (InterruptedException e) {
				//					e.printStackTrace();
				//				}

				//progress.dismiss();
				Toast.makeText(getApplicationContext(), "uploading is succesful", Toast.LENGTH_LONG).show();

				finish();
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
