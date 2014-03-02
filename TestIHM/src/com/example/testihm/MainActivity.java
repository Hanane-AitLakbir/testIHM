package com.example.testihm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button download = (Button) findViewById(R.id.download);
		Button upload = (Button) findViewById(R.id.upload);
		Button options = (Button) findViewById(R.id.options);
		
		download.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
				startActivity(intent);
			}
		});
		
		upload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this, UploadActivity.class);
				startActivity(intent);
			}
		});
		
		options.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this, OptionsActivity.class);
				startActivity(intent);
			}
		});
	}


}
