package com.example.testihm;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class OptionsActivity extends Activity {
	private ViewGroup ll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);

		Button cloud = (Button) findViewById(R.id.cloud);
		Button webdav = (Button) findViewById(R.id.webdav);

		final LinearLayout ll = (LinearLayout) findViewById(R.id.parentLayout);

		ll.removeAllViews();
		ListView listView = new ListView(getApplicationContext());
		ll.addView(listView);

		// Use the current directory as title
		String path = "/sdcard";
		if (getIntent().hasExtra("path")) {
			path = getIntent().getStringExtra("path");
		}
		setTitle(path);
		System.out.println(path);
		final String path2 = path;
		// Read all files sorted into the values-array
		List values = new ArrayList();
		File dir = new File(path);
		if (!dir.canRead()) {
			setTitle(getTitle() + " (inaccessible)");
		}
		String[] list = dir.list();
		if (list != null) {
			for (String file : list) {
				if (!file.startsWith(".")) {
					values.add(file);
				}
			}
		}
		Collections.sort(values);

		// Put the data into the list
		ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_2, android.R.id.text1, values);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parents, View view, int position, long id) {
				//WORKING
				String path ="click "+ parents.getItemAtPosition(position).toString();
				Toast toast = Toast.makeText(getApplicationContext(), path, 1000);
				toast.show();

//				String filename = parents.getItemAtPosition(position).toString();
//				if (path2.endsWith(File.separator)) {
//					filename = path2 + filename;
//				} else {
//					filename = path2 + File.separator + filename;
//				}
//				if (new File(filename).isDirectory()) {
//					Intent intent = new Intent(this, ListFileActivity.class);
//					intent.putExtra("path", filename);
//					startActivity(intent);
//					ll.removeAllViews();
//
//				} else {
//					Toast.makeText(getApplicationContext(), filename + " is not a directory", Toast.LENGTH_LONG).show();
//				}

			}
		});



		cloud.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ll.removeAllViews();
				TextView tv=new TextView(getApplicationContext());
				tv.setText("Name");
				ll.addView(tv);
				EditText et=new EditText(getApplicationContext());
				et.setText("Enter the name");
				ll.addView(et); 
				TextView tv2=new TextView(getApplicationContext());
				tv2.setText("URL");
				ll.addView(tv2);
				EditText et2=new EditText(getApplicationContext());
				et2.setText("Enter the URL");
				ll.addView(et2); 
			}
		}
				);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}

	private void updateListView(String path){
		ll.removeAllViews();
		ListView listView = new ListView(getApplicationContext());
		ll.addView(listView);

		// Use the current directory as title
		String path1 = "/sdcard";
		if (getIntent().hasExtra("path")) {
			path1 = getIntent().getStringExtra("path");
		}
		setTitle(path1);
		System.out.println(path1);
		// Read all files sorted into the values-array
		List values = new ArrayList();
		File dir = new File(path1);
		if (!dir.canRead()) {
			setTitle(getTitle() + " (inaccessible)");
		}
		String[] list = dir.list();
		if (list != null) {
			for (String file : list) {
				if (!file.startsWith(".")) {
					values.add(file);
				}
			}
		}
		Collections.sort(values);

		// Put the data into the list
		ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_2, android.R.id.text1, values);
		listView.setAdapter(adapter);
	}

}
