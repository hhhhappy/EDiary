package com.ediary.app;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Main extends BaseActivity implements OnClickListener {
	private DatabaseConnector dbhelper;

	public final static int ACTIVITY_CREATE = 0;
	public final static int ACTIVITY_LOAD_DIARY = 1;
	public final static int ACTIVITY_EDIT = 2;

	public final static int LOAD_DIARY_DELETED = 0;
	public final static int LOAD_DIARY_MODIFIED = 1;
	private List<Diary> diaryList = new ArrayList<Diary>();
	private ListView listView;
	private DiaryAdapter adapter;
	private int positionOfItem = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_layout);
		
		Button buttonAddDiary = (Button)findViewById(R.id.main_add_diary);
		buttonAddDiary.setOnClickListener(this);
		
		this.refreshList();
		adapter = new DiaryAdapter(Main.this, R.layout.diary_item, diaryList);
		listView = (ListView) findViewById(R.id.list_diary);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(
				new OnItemClickListener(){
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						/*Diary diary = diaryList.get(position);
						if(diary.getFlagClose() == true){
							diary.setFlagClose(false);
						}
						else{
							diary.setFlagClose(true);
						}
						diaryList.set(position, diary);
						adapter.notifyDataSetChanged();
						listView.setAdapter(adapter);
						Log.d("itemClick", "successful!");*/
						positionOfItem = position;
						Diary diary = diaryList.get(position);

						Intent intent = new Intent(Main.this, LoadDiary.class);
						intent.putExtra("idDiary", diary.getId());
						startActivityForResult(intent, ACTIVITY_LOAD_DIARY);
					}
				}
				);
		
	}
	@Override
	protected void onResume(){
		super.onResume();
		this.refreshList();
		adapter = new DiaryAdapter(Main.this, R.layout.diary_item, diaryList);
		listView = (ListView) findViewById(R.id.list_diary);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(
				new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						positionOfItem = position;
						Diary diary = diaryList.get(position);

						Intent intent = new Intent(Main.this, LoadDiary.class);
						intent.putExtra("idDiary", diary.getId());
						startActivityForResult(intent, ACTIVITY_LOAD_DIARY);
					}
				}
		);
	}
	@Override
	public void onBackPressed(){
		ActivityCollector.finishAll();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		case R.id.main_add_diary:
			Intent intent = new Intent(Main.this, Create.class);
			startActivityForResult(intent, ACTIVITY_CREATE);
		break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode){
			case ACTIVITY_CREATE:
				if(resultCode == RESULT_OK){
					this.refreshList();
					adapter = new DiaryAdapter(Main.this, R.layout.diary_item, diaryList);
					listView = (ListView) findViewById(R.id.list_diary);
					listView.setAdapter(adapter);
					listView.setOnItemClickListener(
							new OnItemClickListener(){

								@Override
								public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
									positionOfItem = position;
									Diary diary = diaryList.get(position);

									Intent intent = new Intent(Main.this, LoadDiary.class);
									intent.putExtra("idDiary", diary.getId());
									startActivityForResult(intent, ACTIVITY_LOAD_DIARY);
								}
							}
					);
				}
				break;
			case ACTIVITY_LOAD_DIARY:
				if(resultCode == LOAD_DIARY_DELETED || resultCode == LOAD_DIARY_MODIFIED){
					this.refreshList();
					adapter = new DiaryAdapter(Main.this, R.layout.diary_item, diaryList);
					listView = (ListView) findViewById(R.id.list_diary);
					listView.setAdapter(adapter);
					listView.setOnItemClickListener(
							new OnItemClickListener(){

								@Override
								public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
									positionOfItem = position;
									Diary diary = diaryList.get(position);

									Intent intent = new Intent(Main.this, LoadDiary.class);
									intent.putExtra("idDiary", diary.getId());
									startActivityForResult(intent, ACTIVITY_LOAD_DIARY);
								}
							}
					);
				}
				break;
		}
	}

	private void refreshList(){		//refresh the list of diary
		
		
		diaryList.clear();
		/*database connection*/
		
		dbhelper = new DatabaseConnector(this, "EDiary.db", null, DatabaseConnector.versionDB); 
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		/*Cursor: select the data from database*/
		String sql = "select diary.*, count(photo.name_photo) as num_photo from diary left outer join photo on diary.id=photo.id_diary group by diary.id";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do{
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				String date_diary = cursor.getString(cursor.getColumnIndex("date"));
				String emotion = cursor.getString(cursor.getColumnIndex("emotion"));
				String weather = cursor.getString(cursor.getColumnIndex("weather"));
				String context = cursor.getString(cursor.getColumnIndex("context"));
				String location = cursor.getString(cursor.getColumnIndex("location"));
				int num_photo = cursor.getInt(cursor.getColumnIndex("num_photo"));
				/*put diaries into the list*/
				Diary diary = new Diary(id, date_diary, context, emotion, weather, true, location, num_photo);
				//Diary diary = new Diary(id, date_diary, context, emotion, weather, true, location);
				if(num_photo!=0){
					//Log.d("lisphotoname", String.valueOf(id));
					diary.setListPhotoName();
				}
				diaryList.add(diary);
				
			}while(cursor.moveToNext());
		}
		cursor.close();
		/*end of cursor*/
		
		
	}
}
