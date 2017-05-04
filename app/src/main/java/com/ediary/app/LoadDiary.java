package com.ediary.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lenovo on 2017/4/23.
 */
public class LoadDiary extends BaseActivity {
    //views
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

    //variables
    private int idDiaryEdit;
    private String emotion = "";
    private String weather = "";
    private String date = "";
    private String location = "";
    private String context = "";
    private int num_photo;

    private int isModified = 0;

    private DatabaseConnector dbhelper;
    PhotoTools photoTools = new PhotoTools();
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loaddiary_layout);

        diaryDate = (TextView) findViewById(R.id.loaddiary_date);
        diaryContext = (TextView) findViewById(R.id.loaddiary_context_text);
        diaryLocation = (TextView) findViewById(R.id.loaddiary_location);
        diaryEmotion = (ImageView) findViewById(R.id.loaddiary_emotion);
        diaryWeather = (ImageView) findViewById(R.id.loaddiary_weather);
        diaryDeleteButton = (ImageButton) findViewById(R.id.loaddiary_button_delete);
        diaryEditButton = (ImageButton) findViewById(R.id.loaddiary_button_edit);
        diaryPhoto1 = (ImageView) findViewById(R.id.loaddiary_photo_1);
        diaryPhoto2 = (ImageView) findViewById(R.id.loaddiary_photo_2);
        diaryPhoto3 = (ImageView) findViewById(R.id.loaddiary_photo_3);

        /*Intent*/
        Intent intent = getIntent();
        idDiaryEdit = intent.getIntExtra("idDiary", 0);

        refesh();

        if(num_photo != 0){
            List<String> listPhotoName = new ArrayList<String>();
            for(int i = 1;i<=num_photo;i++){
                listPhotoName.add(idDiaryEdit+"_"+i+".jpg");
            }
            Iterator<String> it = listPhotoName.iterator();
            int i = 1;
            while (it.hasNext()) {
                String photoName = it.next();
                Bitmap bmpPhoto = photoTools.getBitmapFromImage(photoName);
                if(i == 1){
                    diaryPhoto1.setVisibility(View.VISIBLE);
                    diaryPhoto1.setImageBitmap(bmpPhoto);
                    diaryPhoto1.setMaxHeight(photoTools.photoMaxHeight);
                    diaryPhoto1.setMaxWidth(photoTools.photoMaxWidth);
                    diaryPhoto1.setTag(photoName);
                    diaryPhoto2.setVisibility(View.GONE);
                    diaryPhoto3.setVisibility(View.GONE);
                }
                else if(i == 2){
                    diaryPhoto2.setVisibility(View.VISIBLE);
                    diaryPhoto2.setImageBitmap(bmpPhoto);
                    diaryPhoto2.setMaxHeight(photoTools.photoMaxHeight);
                    diaryPhoto2.setMaxWidth(photoTools.photoMaxWidth);
                    diaryPhoto2.setTag(photoName);
                    diaryPhoto3.setVisibility(View.GONE);
                }
                else if(i == 3){
                    diaryPhoto3.setVisibility(View.VISIBLE);
                    diaryPhoto3.setImageBitmap(bmpPhoto);
                    diaryPhoto3.setMaxHeight(photoTools.photoMaxHeight);
                    diaryPhoto3.setMaxWidth(photoTools.photoMaxWidth);
                    diaryPhoto3.setTag(photoName);
                }
                i++;
            }
            diaryPhoto1.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(LoadDiary.this, ViewPhotoActivity.class);
                            intent.putExtra("photoPath",photoTools.getAbsolutePath()+ File.separator + diaryPhoto1.getTag().toString());
                            startActivity(intent);
                        }
                    }
            );
            diaryPhoto2.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(LoadDiary.this, ViewPhotoActivity.class);
                            intent.putExtra("photoPath",photoTools.getAbsolutePath()+ File.separator + diaryPhoto2.getTag().toString());
                            startActivity(intent);
                        }
                    }
            );
            diaryPhoto3.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(LoadDiary.this, ViewPhotoActivity.class);
                            intent.putExtra("photoPath",photoTools.getAbsolutePath()+ File.separator + diaryPhoto3.getTag().toString());
                            startActivity(intent);
                        }
                    }
            );
        }

        diaryEditButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LoadDiary.this, Edit.class);
                        intent.putExtra("idDiaryEdit", idDiaryEdit);
                        isModified = 1;
                        startActivity(intent);
                    }

                }
        );

        diaryDeleteButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
						/*AlertDialog*/
                        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(LoadDiary.this);
                        dialogDelete.setTitle("Delete");
                        dialogDelete.setMessage("Do you really want to delete it?");
                        dialogDelete.setCancelable(false);
                        dialogDelete.setPositiveButton("Delete",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        dbhelper = new DatabaseConnector(LoadDiary.this, "EDiary.db", null, DatabaseConnector.versionDB);
                                        //dbhelper.getReadableDatabase();
                                        SQLiteDatabase db = dbhelper.getWritableDatabase();
                                        String [] arrayId = new String[]{String.valueOf(idDiaryEdit)};

                                        db.execSQL("delete from photo where id_diary = ?", arrayId);
                                        db.execSQL("delete from diary where id = ?", arrayId);
                                        db.close();

                                        photoTools.deleteFilePhotoFromIdDiary(idDiaryEdit);
                                        Intent intent = new Intent();
                                        intent.putExtra("idDeleted", idDiaryEdit);
                                        setResult(Main.LOAD_DIARY_DELETED, intent);
                                        finish();
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

    }
    @Override
    protected void onResume(){
        super.onResume();
        refesh();
    }

    @Override
    public void onBackPressed(){
        if(isModified == 1){
            setResult(Main.LOAD_DIARY_MODIFIED);
        }
        else{
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    private void refesh(){
        /*Database, get the datas of diary*/
        dbhelper = new DatabaseConnector(this, "EDiary.db", null, DatabaseConnector.versionDB);
        SQLiteDatabase db = dbhelper.getReadableDatabase();
		/*Cursor: select the data from database*/
        String sql = "select diary.*, count(photo.name_photo) as num_photo from diary left outer join photo on diary.id=photo.id_diary " +
                "where id = " + idDiaryEdit + " group by diary.id";

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()){
            do{
                date = cursor.getString(cursor.getColumnIndex("date"));
                emotion = cursor.getString(cursor.getColumnIndex("emotion"));
                weather = cursor.getString(cursor.getColumnIndex("weather"));
                context = cursor.getString(cursor.getColumnIndex("context"));
                location = cursor.getString(cursor.getColumnIndex("location"));
                num_photo = cursor.getInt(cursor.getColumnIndex("num_photo"));
            }while(cursor.moveToNext());
        }
        cursor.close();

        //Weather
        if(weather.equals("sunshine")){
            diaryWeather.setImageResource(R.drawable.sun);
        }
        else if(weather.equals("cloudy")){
            diaryWeather.setImageResource(R.drawable.cloudy);
        }
        else if(weather.equals("rain")){
            diaryWeather.setImageResource(R.drawable.rain);
        }
        else{
            diaryWeather.setImageResource(R.drawable.snow);
        }

        //Emotion
        if(emotion.equals("happy")){
            diaryEmotion.setImageResource(R.drawable.happy);
        }
        else if(emotion.equals("smile")){
            diaryEmotion.setImageResource(R.drawable.smile);
        }
        else if(emotion.equals("normal")){
            diaryEmotion.setImageResource(R.drawable.normal);
        }
        else{
            diaryEmotion.setImageResource(R.drawable.sad);
        }

        diaryDate.setText(date);
        diaryContext.setText(context);
        diaryLocation.setText(location);
    }
}
