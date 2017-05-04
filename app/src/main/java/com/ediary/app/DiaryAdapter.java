package com.ediary.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class DiaryAdapter extends ArrayAdapter<Diary> {

	private int resourceId;
	private final Context myContext;
	private DatabaseConnector dbhelper;
	public static final int MAX_NUM_PHOTO = 3;
	private PhotoTools photoTools = new PhotoTools();
	
	public DiaryAdapter(Context context, int textViewResourceId,
			List<Diary> objects) {
		super(context, textViewResourceId, objects);
		this.resourceId = textViewResourceId;
		this.myContext = context;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent){
		
		Diary diary = getItem(position);
		View view;
		final ViewHolder viewHolder;
		
		if(convertView == null){
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.diaryDate = (TextView)view.findViewById(R.id.diary_date);
			viewHolder.diaryContext = (TextView)view.findViewById(R.id.diary_context);
			viewHolder.diaryEmotion = (ImageView)view.findViewById(R.id.diary_emotion);
			viewHolder.diaryWeather = (ImageView)view.findViewById(R.id.diary_weather);
			viewHolder.diaryDeleteButton = (ImageButton) view.findViewById(R.id.diary_button_delete);
			viewHolder.diaryEditButton = (ImageButton) view.findViewById(R.id.diary_button_edit);
			viewHolder.diaryLocation = (TextView)view.findViewById(R.id.diary_item_location);
			viewHolder.diaryPhoto1 = (ImageView)view.findViewById(R.id.diary_photo_1);
			viewHolder.diaryPhoto2 = (ImageView)view.findViewById(R.id.diary_photo_2);
			viewHolder.diaryPhoto3 = (ImageView)view.findViewById(R.id.diary_photo_3);
			view.setTag(viewHolder);
		}
		else{
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		
		
		viewHolder.diaryDate.setText(diary.getDate());
		
		String context = diary.getContext();
		if(diary.getFlagClose() == true && context.length()>38){
			context = context.substring(0, 34) + "......";
		}
		
		viewHolder.diaryContext.setText(context);
		
		viewHolder.diaryLocation.setText(diary.getLocation());
		
		if(diary.getEmotion().equals("happy")){
			viewHolder.diaryEmotion.setImageResource(R.drawable.happy);
		}
		else if(diary.getEmotion().equals("smile")){
			viewHolder.diaryEmotion.setImageResource(R.drawable.smile);
		}
		else if(diary.getEmotion().equals("normal")){
			viewHolder.diaryEmotion.setImageResource(R.drawable.normal);
		}
		else{
			viewHolder.diaryEmotion.setImageResource(R.drawable.sad);
		}
		
		
		if(diary.getWeather().equals("sunshine")){
			viewHolder.diaryWeather.setImageResource(R.drawable.sun);
		}
		else if(diary.getWeather().equals("cloudy")){
			viewHolder.diaryWeather.setImageResource(R.drawable.cloudy);
		}
		else if(diary.getWeather().equals("rain")){
			viewHolder.diaryWeather.setImageResource(R.drawable.rain);
		}
		else{
			viewHolder.diaryWeather.setImageResource(R.drawable.snow);
		}
		
		/*diaryPHoto*/


		List<String> listPhotoName = diary.getListPhotoName();

		if(!listPhotoName.isEmpty()){
			Iterator<String> it = listPhotoName.iterator();
			int i = 1;
			while (it.hasNext()) {
				String photoName = it.next();

				Bitmap bmpPhoto = photoTools.getBitmapFromImage(photoName);
				if(i == 1){
					viewHolder.diaryPhoto1.setVisibility(View.VISIBLE);
					viewHolder.diaryPhoto1.setImageBitmap(bmpPhoto);
					viewHolder.diaryPhoto1.setMaxHeight(photoTools.photoMaxHeight);
					viewHolder.diaryPhoto1.setMaxWidth(photoTools.photoMaxWidth);
					viewHolder.diaryPhoto1.setTag(photoName);
					viewHolder.diaryPhoto2.setVisibility(View.GONE);
					viewHolder.diaryPhoto3.setVisibility(View.GONE);
				}
				else if(i == 2){
					viewHolder.diaryPhoto2.setVisibility(View.VISIBLE);
					viewHolder.diaryPhoto2.setImageBitmap(bmpPhoto);
					viewHolder.diaryPhoto2.setMaxHeight(photoTools.photoMaxHeight);
					viewHolder.diaryPhoto2.setMaxWidth(photoTools.photoMaxWidth);
					viewHolder.diaryPhoto2.setTag(photoName);
					viewHolder.diaryPhoto3.setVisibility(View.GONE);
				}
				else if(i == 3){
					viewHolder.diaryPhoto3.setVisibility(View.VISIBLE);
					viewHolder.diaryPhoto3.setImageBitmap(bmpPhoto);
					viewHolder.diaryPhoto3.setMaxHeight(photoTools.photoMaxHeight);
					viewHolder.diaryPhoto3.setMaxWidth(photoTools.photoMaxWidth);
					viewHolder.diaryPhoto3.setTag(photoName);
				}
				i++;
			}
		}
		else{
			viewHolder.diaryPhoto1.setVisibility(View.GONE);
			viewHolder.diaryPhoto2.setVisibility(View.GONE);
			viewHolder.diaryPhoto3.setVisibility(View.GONE);
		}
		viewHolder.diaryPhoto1.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(myContext, ViewPhotoActivity.class);
						intent.putExtra("photoPath",photoTools.getAbsolutePath()+ File.separator + viewHolder.diaryPhoto1.getTag().toString());
						myContext.startActivity(intent);
					}
				}
		);
		viewHolder.diaryPhoto2.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(myContext, ViewPhotoActivity.class);
						intent.putExtra("photoPath",photoTools.getAbsolutePath()+ File.separator + viewHolder.diaryPhoto2.getTag().toString());
						myContext.startActivity(intent);
					}
				}
		);
		viewHolder.diaryPhoto3.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(myContext, ViewPhotoActivity.class);
						intent.putExtra("photoPath",photoTools.getAbsolutePath()+ File.separator + viewHolder.diaryPhoto3.getTag().toString());
						myContext.startActivity(intent);
					}
				}
		);
		
		viewHolder.diaryDeleteButton.setTag(diary.getId());
		viewHolder.diaryDeleteButton.setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Log.d("delete test",v.getTag().toString());
						/*AlertDialog*/
						AlertDialog.Builder dialogDelete = new AlertDialog.Builder(myContext);
						dialogDelete.setTitle("Delete");
						dialogDelete.setMessage("Do you really want to delete it?");
						dialogDelete.setCancelable(false);
						dialogDelete.setPositiveButton("Delete", 
								new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										// TODO Auto-generated method stub
										int idDelete = getItem(position).getId();
										dbhelper = new DatabaseConnector(myContext, "EDiary.db", null, DatabaseConnector.versionDB); 
										//dbhelper.getReadableDatabase();
										SQLiteDatabase db = dbhelper.getWritableDatabase();
										String [] arrayId = new String[]{String.valueOf(idDelete)};
										
										db.execSQL("delete from photo where id_diary = ?", arrayId);
										db.execSQL("delete from diary where id = ?", arrayId);
										db.close();
										
										photoTools.deleteFilePhotoFromIdDiary(idDelete);
										/*remove the item from adapter*/
										remove(getItem(position));
										notifyDataSetChanged();
										/*
										Activity activityMain = (Activity) myContext;
										ListView listView = (ListView) activityMain.findViewById(R.id.list_diary);
										listView.*/
										
										
										//activityMain.recreate();
										Log.d("delete test","Sure");
										
									}
								});
						dialogDelete.setNegativeButton("Cancel", 
								new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface arg0, int arg1) {

									}
								});
						dialogDelete.show();
					}
					
				}
				);
		
		viewHolder.diaryEditButton.setTag(diary.getId());
		viewHolder.diaryEditButton.setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(myContext, Edit.class);
						intent.putExtra("idDiaryEdit", Integer.parseInt(v.getTag().toString()));
						myContext.startActivity(intent);
					}
					
				}
				);
		return view;
		
	}
	
	class ViewHolder{
		TextView diaryDate;
		TextView diaryContext;
		TextView diaryLocation;
		ImageView diaryEmotion;
		ImageView diaryWeather;
		ImageButton diaryDeleteButton;
		ImageButton diaryEditButton;
		ImageView diaryPhoto1;
		ImageView diaryPhoto2;
		ImageView diaryPhoto3;
	}
}
