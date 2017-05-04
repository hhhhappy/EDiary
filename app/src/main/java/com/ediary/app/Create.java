package com.ediary.app;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Create extends BaseActivity implements OnClickListener{
/*Create a new diary*/
	/*photo paramettre*/
	public static final int TAKE_PHOTO = 1;
	public static final int CROP_PHOTO = 2;
	public static final int MAX_NUM_PHOTO = 3;
	private int numPhoto = 0;
	private String bufferPhotoName = "";
	private List<File> listPhoto=new ArrayList<File>();
	
	private int idDiary;
	
	private String emotion = "happy";
	private String weather = "sunshine";
	private String date = "";
	private String location = "";
	private String context = "";
	private Button buttonSave;
	private Button buttonCancel;
	private Button buttonGetLocation;
	private ImageButton buttonAddPhoto;
	private EditText dateChamp;
	private EditText locationChamp;
	private AlertDialog menuPhotoDialog; 
	private PhotoTools photoTools;
	private ImageView photoAdd_1;
	private ImageView photoAdd_2;
	private ImageView photoAdd_3;
	
	
	private LocationManager locationManager;
	private String provider;
	private Location locationClass;
	private double latitude;
	private double longitude;
	private String currentPostion ="";
	public static final int SHOW_LOCATION = 0;
	
	
	private DatabaseConnector dbhelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.create_layout);
		
		//Log.d("OncreateCreate", "here");
		/*database*/
		dbhelper = new DatabaseConnector(this, "EDiary.db", null, DatabaseConnector.versionDB);
		idDiary = getIdDiary();
		
		/*Photo*/
		photoAdd_1 = (ImageView)findViewById(R.id.photo_add_1);
		photoAdd_2 = (ImageView)findViewById(R.id.photo_add_2);
		photoAdd_3 = (ImageView)findViewById(R.id.photo_add_3);
		
		/*Date*/
		dateChamp = (EditText)findViewById(R.id.create_date);
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd"); 
		Date curDate = new Date(System.currentTimeMillis()); 
		String str = formatter.format(curDate); 
		dateChamp.setText(str);
		
		
		/*Button*/
		buttonSave = (Button) findViewById(R.id.create_button_save);
		buttonSave.setOnClickListener(this);
		buttonCancel = (Button) findViewById(R.id.create_button_cancel);
		buttonCancel.setOnClickListener(this);
		buttonGetLocation = (Button) findViewById(R.id.create_button_getlocation);
		buttonGetLocation.setOnClickListener(this);
		buttonAddPhoto = (ImageButton)findViewById(R.id.create_button_photo);
		buttonAddPhoto.setOnClickListener(this);
		
		/*WEATHER*/
		RadioGroup radioWeather = (RadioGroup)findViewById(R.id.create_weather_radio);
		radioWeather.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
                int radioButtonId = arg0.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton)Create.this.findViewById(radioButtonId);
                weather=rb.getTag().toString();
                //Toast.makeText(Create.this, weather, Toast.LENGTH_SHORT).show();
            }
        });
		
		/*EMOTION*/
		RadioGroup radioEmotion = (RadioGroup)findViewById(R.id.create_emotion_radio);
		radioEmotion.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
                int radioButtonId = arg0.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton)Create.this.findViewById(radioButtonId);
                emotion=rb.getTag().toString();
                //Toast.makeText(Create.this, emotion, Toast.LENGTH_SHORT).show();
            }
        });
		
		/*Location*/
		locationChamp = (EditText)findViewById(R.id.create_place);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		List<String> providerList = locationManager.getProviders(true);
		
		if(providerList.contains(LocationManager.GPS_PROVIDER)){
			provider = LocationManager.GPS_PROVIDER;
		}
		else if(providerList.contains(LocationManager.NETWORK_PROVIDER)){
			provider = LocationManager.NETWORK_PROVIDER;
		}
		else{
			Toast.makeText(this, "No location provider", Toast.LENGTH_SHORT).show();
			return;
		}
		Location location = locationManager.getLastKnownLocation(provider);
		if (location != null){
			getLocation(location);
		}
		locationManager.requestLocationUpdates(provider, 5000, 1, locationListener);
		
		
	}
	
	
	LocationListener locationListener = new LocationListener(){

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			getLocation(location);
		}

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}
		
	};
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		case R.id.create_button_save:
			/*dateChamp*/
			date = dateChamp.getText().toString();
			
			String eL = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
			Pattern p = Pattern.compile(eL);
			Matcher m = p.matcher(date);
			boolean dateFlag = m.matches();
			
			
			/*location*/
			location = locationChamp.getText().toString();
			
			/*ContextChamp*/
			EditText contextChamp = (EditText)findViewById(R.id.create_context_edit);
			context = contextChamp.getText().toString();
				
			if (date.equals("")||context.equals("")){
				Toast.makeText(Create.this, "Date and context must not be empty!", Toast.LENGTH_SHORT).show();
			}
			else if(!dateFlag){
				Toast.makeText(Create.this, "Date's format isn't correct!", Toast.LENGTH_SHORT).show();
			}
			else{
				//Toast.makeText(Create.this, emotion + weather + date + context, Toast.LENGTH_SHORT).show();
				/*database insert*/
				SQLiteDatabase db = dbhelper.getWritableDatabase();
				ContentValues values = new ContentValues();
				values.put("date", date);
				values.put("emotion", emotion);
				values.put("weather", weather);
				values.put("context", context);
				values.put("location", location);
				db.insert("diary", null, values);
				/*stock info of photos*/
				Iterator<File> it = listPhoto.iterator();
				int i = 0;
				while (it.hasNext()) {
					File f = it.next();
					
					Log.d("photos", f.getName());
					values.clear();
					values.put("id_diary", idDiary);
					values.put("name_photo", f.getName());
					db.insert("photo", null, values);
					i++;
				}
				setResult(RESULT_OK);
				finish();
			}
		break;
		case R.id.create_button_getlocation:
			
			if(currentPostion == ""){
				Toast.makeText(Create.this, "Location service isn't avaliable", Toast.LENGTH_SHORT).show();
				
			}
			else{
				//Toast.makeText(Create.this, currentPostion, Toast.LENGTH_SHORT).show();
				//String[] arrayPosition = currentPostion.split(",");
				locationChamp.setText(currentPostion);
				
			}
			
			
			//Log.d("location", String.valueOf(altitude));
		break;
		case R.id.create_button_cancel:
			/*Delete the photos*/
			/*
			Iterator<File> it = listPhoto.iterator();
			int i = 0;
			while (it.hasNext()) {
				File f = it.next();
				
				Log.d("PhotoDelete", f.getName());
				f.delete();
				
				i++;
			}*/
			if(photoAdd_1.getVisibility() != View.GONE){
				photoTools.deleteFilePhotoFromIdDiary(idDiary);
			}
			setResult(RESULT_CANCELED);
			finish();
		break;
		case R.id.create_button_photo:
			
			if(numPhoto == 0){
				bufferPhotoName = idDiary + "_1";
			}
			else if(numPhoto == 1){
				bufferPhotoName = idDiary + "_2";
			}
			else if(numPhoto == 2){
				bufferPhotoName = idDiary + "_3";
			}
			else if(numPhoto >= MAX_NUM_PHOTO){
				break;
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(Create.this);
	        View view = View.inflate(Create.this, R.layout.photo_menu, null);
	        builder.setView(view);
	        builder.setCancelable(true);
	        TextView title= (TextView) view.findViewById(R.id.menu_photo_title);
	        Button buttonCamera=(Button)view.findViewById(R.id.menu_photo_camera);
	        buttonCamera.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.d("testPhotoMenu", "camera");
					photoTools = new PhotoTools();
					Intent intent = photoTools.getIntentToCamera(bufferPhotoName);
					startActivityForResult(intent, TAKE_PHOTO);
				}
	        	
	        });
	        Button buttonAlbum=(Button)view.findViewById(R.id.menu_photo_album);
	        buttonAlbum.setOnClickListener(
	        			new OnClickListener(){

	    				@Override
	    				public void onClick(View v) {
	    					// TODO Auto-generated method stub
	    					Log.d("testPhotoMenu", "album");
							photoTools = new PhotoTools();
							Intent intent = photoTools.getIntentToAlbum(bufferPhotoName);
							startActivityForResult(intent, CROP_PHOTO);
	    				}
	        			}
	        		);
	        Button buttonCancel = (Button)view.findViewById(R.id.menu_photo_cancel);
	        buttonCancel.setOnClickListener(
        			new OnClickListener(){
    				@Override
    				public void onClick(View v) {
    					// TODO Auto-generated method stub
    					menuPhotoDialog.dismiss();
    				}
        			}
        		);
	        menuPhotoDialog = builder.create();
	        menuPhotoDialog.show(); 
		break;
		}
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		if(locationManager !=null){
			locationManager.removeUpdates(locationListener);
		}
	}
	@Override
	public void onBackPressed(){
		/*Delete the photos*/
		/*
		Iterator<File> it = listPhoto.iterator();
		int i = 0;
		while (it.hasNext()) {
			File f = it.next();
			
			Log.d("PhotoDelete", f.getName());
			f.delete();
			
			i++;
		}*/
		if(photoAdd_1.getVisibility() != View.GONE){
			photoTools.deleteFilePhotoFromIdDiary(idDiary);
		}
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		switch (requestCode){
		case TAKE_PHOTO:
			if (resultCode == RESULT_OK){
				Intent intent = photoTools.getIntentToCrop();
				startActivityForResult(intent, CROP_PHOTO);
			}
			break;
		case CROP_PHOTO:
			//if(resultCode == RESULT_OK){
				try{
					Log.d("CROP_PHOTO", "OK");
					Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(photoTools.getImageUri()));
					
					if(numPhoto == 0){
						photoAdd_1.setImageBitmap(bitmap);
						photoAdd_1.setMaxHeight(photoTools.photoMaxHeight);
						photoAdd_1.setMaxWidth(photoTools.photoMaxWidth);
						photoAdd_1.setVisibility(View.VISIBLE);
						listPhoto.add(photoTools.getFilePhoto());
						numPhoto++;
					}
					else if(numPhoto == 1){
						photoAdd_2.setImageBitmap(bitmap);
						photoAdd_2.setMaxHeight(photoTools.photoMaxHeight);
						photoAdd_2.setMaxWidth(photoTools.photoMaxWidth);
						photoAdd_2.setVisibility(View.VISIBLE);
						listPhoto.add(photoTools.getFilePhoto());
						numPhoto++;
					}
					else if(numPhoto == 2){
						photoAdd_3.setImageBitmap(bitmap);
						photoAdd_3.setMaxHeight(photoTools.photoMaxHeight);
						photoAdd_3.setMaxWidth(photoTools.photoMaxWidth);
						photoAdd_3.setVisibility(View.VISIBLE);
						listPhoto.add(photoTools.getFilePhoto());
						numPhoto++;
						buttonAddPhoto.setVisibility(View.GONE);
					}
					else{
						Toast.makeText(Create.this, "You can only add 3 photo maximum", Toast.LENGTH_SHORT);
					}
					
				}catch(FileNotFoundException e){
					e.printStackTrace();
				}
				menuPhotoDialog.dismiss();
			//}
			break;
		default:
			break;
		}
	}
	private void getLocation(final Location location) {
		// TODO Auto-generated method stub
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		new Thread(
				new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try{
							StringBuilder url = new StringBuilder();
							url.append("http://maps.googleapis.com/maps/api/geocode/json?latlng=");
							url.append(location.getLatitude()).append(",");
							url.append(location.getLongitude());
							url.append("&sensor=false");
							HttpClient httpClient = new DefaultHttpClient();
							HttpGet httpGet = new HttpGet(url.toString());
							
							httpGet.addHeader("Accept-Language", "EN");
							HttpResponse httpResponse = httpClient.execute(httpGet);
							
							if(httpResponse.getStatusLine().getStatusCode() == 200){
								HttpEntity entity = httpResponse.getEntity();
								String response = EntityUtils.toString(entity, "utf-8");
								JSONObject jsonObject = new JSONObject(response);
								
								JSONArray resultArray = jsonObject.getJSONArray("results");
								if(resultArray.length()> 0){
									JSONObject subObject = resultArray.getJSONObject(0);
									String address = subObject.getString("formatted_address");
									Message message = new Message();
									message.what = SHOW_LOCATION;
									message.obj = address;
									handler.sendMessage(message);
								}
							}
							
						} catch(Exception e){
							e.printStackTrace();
						}
					}
					
				}
		).start();
		
	}
	
	private int getIdDiary(){
		//get the next id of Diary
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		/*Cursor: select the data from database*/
		String sql = "select seq from sqlite_sequence WHERE name = 'diary'";
		int id = 0;
		
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do{
				id = cursor.getInt(cursor.getColumnIndex("seq"));
			}while(cursor.moveToNext());
		}
		cursor.close();
		return id+1;
	}
	private Handler handler = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			case SHOW_LOCATION:
				currentPostion = (String) msg.obj;
				
				break;
			default:
				break;
			}
		}
	};
}
