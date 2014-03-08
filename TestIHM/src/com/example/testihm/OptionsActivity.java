package com.example.testihm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import metadata.JSonSerializer;
import metadata.Metadata;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class OptionsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);

		Button cloud = (Button) findViewById(R.id.cloud);
		Button back = (Button) findViewById(R.id.back);

		final LinearLayout ll = (LinearLayout) findViewById(R.id.parentLayout);

		ll.removeAllViews();
		ListView listView = new ListView(getApplicationContext());
		ll.addView(listView);

		//		// Use the current directory as title
		//		String path = "/sdcard";
		//		if (getIntent().hasExtra("path")) {
		//			path = getIntent().getStringExtra("path");
		//		}
		//		setTitle(path);
		//		System.out.println(path);
		//		final String path2 = path;
		//		// Read all files sorted into the values-array
		//		List values = new ArrayList();
		//		File dir = new File(path);
		//		if (!dir.canRead()) {
		//			setTitle(getTitle() + " (inaccessible)");
		//		}
		//		String[] list = dir.list();
		//		if (list != null) {
		//			for (String file : list) {
		//				if (!file.startsWith(".")) {
		//					values.add(file);
		//				}
		//			}
		//		}
		ArrayList<HashMap<String, String>> values = new ArrayList<HashMap<String,String>>();



		//			FileInputStream input = new FileInputStream("/sdcard/pip/metadata/cloud/list.json");
		//			BufferedReader reader = new BufferedReader(new InputStreamReader(input,"iso-8859-1"),8);
		//			StringBuilder sb = new StringBuilder();
		//			String line = null;
		//			while ((line = reader.readLine()) != null) {
		//				System.out.println(line);
		//				sb.append(line + "\n");
		//			}
		//			String content = sb.toString();
		//			input.close();
		////			Metadata metadata = new JSonSerializer("metadata/cloud.list.json").deserialize();
		//			System.out.println(content);
		//			//values.add(content);
		//			JSONObject json = new JSONObject(content);
		//			Iterator iterator =json.keys();
		String current;
		Metadata metadataListCloud = new JSonSerializer(Environment.getExternalStorageDirectory().getPath()+"/pip/metadata/cloud/list.json").deserialize();
		HashMap<String, String> map = metadataListCloud.getMap();
		Iterator<String> iterator = map.keySet().iterator();

		HashMap<String, String> map2;
		while(iterator.hasNext()){
			map2 = new HashMap<String, String>();
			current = iterator.next().toString();
			map2.put("nameCloud",current);
			map2.put("type", map.get(current));
			//values.add(current+" "+ json.getString(current));
			values.add(map2);
			//				ListAdapter adapter = new SimpleAdapter(getApplicationContext(), values, R.layout.list_clouds, new String[] {"nameCloud","type"}, new int[] {R.id.nameCloud,R.id.type});
			//				listView.setAdapter(adapter);
		}





		System.out.println(values.toString());

		//Collections.sort(values);

		// Put the data into the list
		//ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_2, android.R.id.text1, values);
		//listView.setAdapter(adapter);
		ListAdapter adapter = new SimpleAdapter(getApplicationContext(), values, R.layout.list_clouds, new String[] {"nameCloud","type"}, new int[] {R.id.nameCloud,R.id.type});
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parents, View view, int position, long id) {
				//WORKING
				String selectedItem = parents.getItemAtPosition(position).toString();
				String delims = "[ ,{}=]+";
				String[] words = selectedItem.split(delims); //Be careful: it begins with "" because selectedItem begins with the delimiter "{"
				System.out.println(Arrays.toString(words));
				String path ="click "+ parents.getItemAtPosition(position).toString();
				Toast toast = Toast.makeText(getApplicationContext(), path, Toast.LENGTH_SHORT);
				toast.show();

				System.out.println(words[4]);
				Intent intent = new Intent(getApplicationContext(), DetailedLocationStorage.class);
				intent.putExtra("name",words[4]);
				intent.putExtra("type", words[2]);
				startActivity(intent);

				//				File internalFile = new File(parents.getItemAtPosition(position)+".txt");
				//				try {
				//					FileOutputStream fos = new FileOutputStream(internalFile);
				//					fos.write(parents.getItemAtPosition(position).toString().getBytes());
				//					fos.close();
				//				} catch (IOException e) {
				//					
				//					e.printStackTrace();
				//				}
				//				
				//				try {
				//					System.out.println(internalFile.getCanonicalPath());
				//				} catch (IOException e) {
				//					
				//					e.printStackTrace();
				//				}
				//				Toast.makeText(getApplicationContext(), "file saved "+internalFile.getName(), Toast.LENGTH_LONG).show();


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
				Intent intent = new Intent(OptionsActivity.this, AddLocationStorage.class);
				intent.putExtra("type", "cloud");
				startActivity(intent);
			}
		}
				);
		
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),MainActivity.class);
				startActivity(intent);
			}
		});


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}


}
