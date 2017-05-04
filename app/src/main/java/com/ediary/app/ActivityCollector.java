package com.ediary.app;

import java.util.ArrayList;

import android.app.Activity;

public class ActivityCollector{
	public static ArrayList<Activity> activities = new ArrayList<Activity>();
	public static void add(Activity a){
		activities.add(a);
	}
	public static void remove(Activity a){
		activities.remove(a);
	}
	public static void finishAll(){
		for (Activity a : activities){
			if(!a.isFinishing()){
				a.finish();
			}
		}
	}
}
