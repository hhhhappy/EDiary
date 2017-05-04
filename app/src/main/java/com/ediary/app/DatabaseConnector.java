package com.ediary.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseConnector extends SQLiteOpenHelper{
	public static final int versionDB = 3;
	public static final String CREATE_TABLE_DIARY = "create table diary (" +
			"id integer primary key autoincrement," +
			"date date," +
			"emotion text," +
			"weather text," +
			"context text," +
			"location text)";
	public static final String CREATE_TABLE_PHOTO = "create table photo(" +
			"id_photo integer primary key autoincrement, " +
			"id_diary integer references diary(id)," +
			"name_photo text" +
			")";
	public DatabaseConnector(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_TABLE_DIARY);
		db.execSQL(CREATE_TABLE_PHOTO);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
		if(oldVersion == 1 && newVersion == 2){
			db.execSQL("ALTER TABLE diary ADD COLUMN location text");
		}
		else if(oldVersion == 2 && newVersion == 3){
			db.execSQL(CREATE_TABLE_PHOTO);
		}
		else if(oldVersion == 1 && newVersion == 3){
			db.execSQL("ALTER TABLE diary ADD COLUMN location text");
			db.execSQL(CREATE_TABLE_PHOTO);
		}
	}

}
