package com.ediary.app;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Edit extends BaseActivity implements OnClickListener{
	private int idDiaryEdit;
	private String emotion = "";
	private String weather = "";
	private String date = "";
	private String location = "";
	private String context = "";
	private Button buttonSave;
	private Button buttonCancel;
	private EditText dateChamp;
	private EditText locationChamp;
	private EditText contextChamp; 
	private DatabaseConnector dbhelper;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.edit_layout);
		/*Intent*/
		Intent intent = getIntent();
		idDiaryEdit = intent.getIntExtra("idDiaryEdit", 0);
		
		/*Database, get the datas of diary*/
		dbhelper = new DatabaseConnector(this, "EDiary.db", null, DatabaseConnector.versionDB);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		/*Cursor: select the data from database*/
		String sql = "select date, emotion, weather, context, location " +
				"from diary " +
				"where id = " + idDiaryEdit;
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do{
				date = cursor.getString(cursor.getColumnIndex("date"));
				emotion = cursor.getString(cursor.getColumnIndex("emotion"));
				weather = cursor.getString(cursor.getColumnIndex("weather"));
				context = cursor.getString(cursor.getColumnIndex("context"));
				location = cursor.getString(cursor.getColumnIndex("location"));
			}while(cursor.moveToNext());
		}
		cursor.close();
		/*Button*/
		buttonSave = (Button) findViewById(R.id.edit_button_save);
		buttonSave.setOnClickListener(this);
		buttonCancel = (Button) findViewById(R.id.edit_button_cancel);
		buttonCancel.setOnClickListener(this);
		
		/*WEATHER*/
		RadioGroup radioWeather = (RadioGroup)findViewById(R.id.edit_weather_radio);
		RadioButton rbWeather;
		if(weather.equals("sunshine")){
			rbWeather = (RadioButton) findViewById(R.id.radioSunshine);
			rbWeather.setChecked(true);
		}
		else if(weather.equals("cloudy")){
			rbWeather = (RadioButton) findViewById(R.id.radioCloudy);
			rbWeather.setChecked(true);
		}
		else if(weather.equals("rain")){
			rbWeather = (RadioButton) findViewById(R.id.radioRain);
			rbWeather.setChecked(true);
		}
		else{
			rbWeather = (RadioButton) findViewById(R.id.radioSnow);
			rbWeather.setChecked(true);
		}
		radioWeather.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
                int radioButtonId = arg0.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton)Edit.this.findViewById(radioButtonId);
                weather=rb.getTag().toString();
                //Toast.makeText(Edit.this, weather, Toast.LENGTH_SHORT).show();
            }
        });
		
		/*EMOTION*/
		RadioGroup radioEmotion = (RadioGroup)findViewById(R.id.edit_emotion_radio);
		RadioButton rbEmotion;
		if(emotion.equals("happy")){
			rbEmotion = (RadioButton) findViewById(R.id.radioHappy);
			rbEmotion.setChecked(true);
		}
		else if(emotion.equals("smile")){
			rbEmotion = (RadioButton) findViewById(R.id.radioSmile);
			rbEmotion.setChecked(true);
		}
		else if(emotion.equals("normal")){
			rbEmotion = (RadioButton) findViewById(R.id.radioNormal);
			rbEmotion.setChecked(true);
		}
		else{
			rbEmotion = (RadioButton) findViewById(R.id.radioSad);
			rbEmotion.setChecked(true);
		}
		radioEmotion.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
                int radioButtonId = arg0.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton)Edit.this.findViewById(radioButtonId);
                emotion=rb.getTag().toString();
                //Toast.makeText(Edit.this, emotion, Toast.LENGTH_SHORT).show();
            }
        });
		/*date*/
		dateChamp = (EditText)findViewById(R.id.edit_date);
		dateChamp.setText(date);
		/*context*/
		contextChamp = (EditText)findViewById(R.id.edit_context_edit);
		contextChamp.setText(context);
		
		/*Location*/
		locationChamp = (EditText)findViewById(R.id.edit_place);
		locationChamp.setText(location);
		//Log.d("idEdit", sql );
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		case R.id.edit_button_save:
			/*get date from dateChamp*/
			date = dateChamp.getText().toString();
			
			String eL = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
			Pattern p = Pattern.compile(eL);
			Matcher m = p.matcher(date);
			boolean dateFlag = m.matches();
			
			/*location*/
			location = locationChamp.getText().toString();
			
			/*get story from storyChamp*/
			context = contextChamp.getText().toString();
				
			if (date.equals("")||context.equals("")){
				Toast.makeText(Edit.this, "Date and context must not be empty!", Toast.LENGTH_SHORT).show();
			}
			else if(!dateFlag){
				Toast.makeText(Edit.this, "Date's format isn't correct!", Toast.LENGTH_SHORT).show();
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
				db.update("diary", values, "id = ?",new String[] {String.valueOf(idDiaryEdit)});
				db.close();
				finish();
			}
		break;
		case R.id.edit_button_cancel:
			finish();
		break;
		}
	}
	
}
