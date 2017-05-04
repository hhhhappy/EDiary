package com.ediary.app;

import java.util.ArrayList;
import java.util.List;

public class Diary {
	private int id;
	private String date;
	private String context;
	private String emotion;
	private String weather;
	private String location;
	private boolean flagClose;
	private int numPhoto;
	private List<String> listPhotoName = new ArrayList<String>();
	public Diary(int id, String date, String context, String emotion, String weather, boolean flagClose, String location, int numPhoto){
		this.id = id;
		this.context = context;
		this.emotion = emotion;
		this.weather = weather;
		this.date = date;
		this.location = location;
		this.flagClose = flagClose;
		this.numPhoto = numPhoto;
		
	}
	public List<String> getListPhotoName(){
		return listPhotoName;
	}
	public void setListPhotoName(){
		for(int i = 1;i<=numPhoto;i++){
			listPhotoName.add(id+"_"+i+".jpg");
		}
	}
	public int getId(){
		return id;
	}
	public String getDate(){
		return date;
	}
	public String getEmotion(){
		return emotion;
	}
	public String getWeather(){
		return weather;
	}
	public String getContext(){
		return context;
	}
	public String getLocation(){
		return location;
	}
	public boolean getFlagClose(){
		return flagClose;
	}
	public void setFlagClose(boolean flagClose){
		this.flagClose = flagClose;
	}
}

