package com.example.testihm;

import metadata.JSonSerializer;
import metadata.Metadata;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class AddLocationStorage extends Activity{

	private AddLocationStorage thisActivity;
	private WebView webView; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		thisActivity = this;
		setContentView(R.layout.activity_add_location_storage);
		RadioButton cloudChoice = (RadioButton) findViewById(R.id.cloudChoice);
		RadioButton webdavChoice = (RadioButton) findViewById(R.id.webdavChoice);

		final LinearLayout ll = (LinearLayout) findViewById(R.id.choiceForm);

		//add the listener for radioButtons
		//----> if the user chooses Cloud
		cloudChoice.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				ll.removeAllViews();
				TextView tv=new TextView(getApplicationContext());
				tv.setText("Name");
				ll.addView(tv);

				final EditText name=new EditText(getApplicationContext());
				name.setHint("Please, enter the name");
				
				ll.addView(name); 

				//				TextView tv2=new TextView(getApplicationContext());
				//				tv2.setText("URL Authentication Oauth");
				//				ll.addView(tv2);
				//				final EditText requestToken=new EditText(getApplicationContext());
				//				requestToken.setHint("Enter the URL - Request Token");
				//				ll.addView(requestToken);
				//				final EditText accessToken=new EditText(getApplicationContext());
				//				accessToken.setHint("Enter the URL - Access Token");
				//				ll.addView(accessToken);
				//				final EditText authorizeToken=new EditText(getApplicationContext());
				//				authorizeToken.setHint("Enter the URL - Authorize Token");
				//				ll.addView(authorizeToken);
				//				
				//				TextView tv3=new TextView(getApplicationContext());
				//				tv3.setText("URL Upload/Download");
				//				ll.addView(tv3);
				//				final EditText uploadURL=new EditText(getApplicationContext());
				//				uploadURL.setHint("Enter the URL - Upload");
				//				ll.addView(uploadURL);
				//				final EditText downloadURL=new EditText(getApplicationContext());
				//				downloadURL.setHint("Enter the URL - Download");
				//				ll.addView(downloadURL);


				TextView tv4=new TextView(getApplicationContext());
				tv4.setText("Application id");
				ll.addView(tv4);
				final EditText appKey=new EditText(getApplicationContext());
				appKey.setHint("Enter the app key");
				ll.addView(appKey);
				final EditText appSecret=new EditText(getApplicationContext());
				appSecret.setHint("Enter the app secret");
				ll.addView(appSecret);

				//				webView = new WebView(getApplicationContext());
				//				LayoutParams params = new LayoutParams(10000,400);
				//				params.setMargins(10, 30, 10, 10);
				//				webView.setLayoutParams(params);
				//				webView.loadUrl("http://www.google.fr");
				//				ll.addView(webView);

				Button save = new Button(getApplicationContext());
				save.setText("Save");
				ll.addView(save);

				//add onClickListener to save
				save.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if(name.getText().toString().length()!=0){
							//						Cloud cloud = new Cloud();
							//						//add all characteristics typed by the user
							//						cloud.setName(name.getText().toString());
							//						cloud.setRequestToken(requestToken.getText().toString());
							//						cloud.setAccessToken(accessToken.getText().toString());
							//						cloud.setAuthorizeToken(authorizeToken.getText().toString());
							//						cloud.setDownload(downloadURL.getText().toString());
							//						cloud.setUpload(uploadURL.getText().toString());
							//						cloud.setAppKey(appKey.getText().toString());
							//						cloud.setAppSecret(appSecret.getText().toString());
							//						System.out.println(cloud.toString()); //to be replaced with metadata creation and serialization

							//create the metadata of the cloud
							Metadata metadata = new Metadata();
							metadata.addContent("name", name.getText().toString());
							//						metadata.addContent("requestToken", requestToken.getText().toString());
							//						metadata.addContent("accessToken", accessToken.getText().toString());
							//						metadata.addContent("authorize", authorizeToken.getText().toString());
							//						metadata.addContent("download", downloadURL.getText().toString());
							//						metadata.addContent("upload", uploadURL.getText().toString());
							metadata.addContent("app_key", appKey.getText().toString());
							metadata.addContent("app_secret", appSecret.getText().toString());
							metadata.serialize(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/"+name.getText().toString()+".json");

							//update the list of storage locations
							Metadata listCloud= new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/list.json").deserialize();
							if(listCloud==null){
								listCloud = new Metadata();
							}
							listCloud.addContent(name.getText().toString(), "dropbox");
							listCloud.serialize(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/list.json");

							//						Provider provider = ProviderFactory.getProvider(name.getText().toString());
							//						try {
							//							provider.connect(thisActivity);
							//						} catch (CloudNotAvailableException e) {
							//							e.printStackTrace();
							//						}

							Toast.makeText(getApplicationContext(), "metadata created", Toast.LENGTH_LONG).show();

							Intent intent = new Intent(AddLocationStorage.this, ConnectionActivity.class);
							intent.putExtra("name",name.getText().toString());
							startActivity(intent);
						}else{
							AlertDialog.Builder builder = new AlertDialog.Builder(AddLocationStorage.this);
							builder.setTitle(R.string.alert_title_add_location_storage);
							builder.setMessage((String)getResources().getString(R.string.alert_message_add_location_storage));
							builder.setNeutralButton("OK",new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_LONG).show();
								}
							});
							AlertDialog alert = builder.create();
							alert.show();

						}
					}

				});
			}
		});

		webdavChoice.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ll.removeAllViews();
				TextView tv=new TextView(getApplicationContext());
				tv.setText("Name");
				ll.addView(tv);
				final EditText name=new EditText(getApplicationContext());
				name.setHint("Please, enter the name");
				ll.addView(name); 

				TextView tv2=new TextView(getApplicationContext());
				tv2.setText("URL of the Webdav server");
				ll.addView(tv2);
				final EditText URLServer=new EditText(getApplicationContext());
				URLServer.setHint("http://");
				ll.addView(URLServer);

				TextView tv3=new TextView(getApplicationContext());
				tv3.setText("Authentication id");
				ll.addView(tv3);
				final EditText login=new EditText(getApplicationContext());
				login.setHint("Enter your login");
				ll.addView(login);
				final EditText password=new EditText(getApplicationContext());
				password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
				password.setHint("Enter your password");
				ll.addView(password);

				Button save = new Button(getApplicationContext());
				save.setText("Save");
				ll.addView(save);

				save.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						//create the metadata of the cloud
						Metadata metadata = new Metadata();
						metadata.addContent("name", name.getText().toString());
						metadata.addContent("URLServer", URLServer.getText().toString());
						metadata.addContent("login", login.getText().toString());
						metadata.addContent("password", password.getText().toString());
						metadata.serialize(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/"+name.getText().toString()+".json");

						//update the list of storage locations
						Metadata listCloud= new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/list.json").deserialize();
						if(listCloud==null){
							listCloud = new Metadata();
						}
						listCloud.addContent(name.getText().toString(), "webdav");
						listCloud.serialize(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/list.json");


						Toast.makeText(getApplicationContext(), "metadata created", Toast.LENGTH_LONG).show();

						Intent intent = new Intent(AddLocationStorage.this, OptionsActivity.class);
						startActivity(intent);

					}
				});

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_location_storage, menu);
		return true;
	}

}
