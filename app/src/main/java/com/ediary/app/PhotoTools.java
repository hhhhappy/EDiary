package com.ediary.app;

import java.io.File;
import java.io.IOException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class PhotoTools {
	private Uri imageUri;
	private String directoryName = "pictureEDiary";
	private String absolutePath = "";
	private File outputImage;
	public final static int photoMaxHeight = 70;
	public final static int photoMaxWidth = 70;
	
	PhotoTools(){
		/*create directory for the pictures*/
		absolutePath = Environment.getExternalStorageDirectory().getPath() + File.separator + directoryName;
		File destDir = new File(absolutePath);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
	}
	public Bitmap getBitmapFromImage(String fileName){
		Bitmap imageBitmap = BitmapFactory.decodeFile(absolutePath + File.separator + fileName);
		return imageBitmap;
		
	}
	public boolean deleteFilePhotoFromIdDiary(int id){
		File file1 = new File(this.absolutePath+File.separator+ id+"_1"+".jpg");
		File file2 = new File(this.absolutePath+File.separator+ id+"_2"+".jpg");
		File file3 = new File(this.absolutePath+File.separator+ id+"_3"+".jpg");
	    if (file1.isFile() && file1.exists()) {
	    	file1.delete();
	    }
	    if (file2.isFile() && file2.exists()) {
	    	file2.delete();
	    }
	    if (file3.isFile() && file3.exists()) {
	    	file3.delete();
	    }
	    return true;
	}
	public File getFilePhoto(){
		return outputImage;
	}
	public Intent getIntentToCamera(String imageName){
		
		outputImage = new File(absolutePath, imageName+".jpg");
		//Log.d("directory", Environment.getExternalStorageDirectory().toString());
		try{
			if(outputImage.exists()){
				outputImage.delete();
			}
			outputImage.createNewFile();
		}catch(IOException e){
			e.printStackTrace();
		}
		imageUri = Uri.fromFile(outputImage);
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		return intent;
		
	}
	public Intent getIntentToAlbum(String imageName){

		outputImage = new File(absolutePath, imageName+".jpg");
		//Log.d("directory", Environment.getExternalStorageDirectory().toString());
		try{
			if(outputImage.exists()){
				outputImage.delete();
			}
			outputImage.createNewFile();
		}catch(IOException e){
			e.printStackTrace();
		}
		imageUri = Uri.fromFile(outputImage);
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_PICK);//Pick an item from the data
		intent.setType("image/*");//从所有图片中进行选择
		intent.putExtra("crop", "true");//设置为裁切
		intent.putExtra("aspectX", 1);//裁切的宽比例
		intent.putExtra("aspectY", 1);//裁切的高比例
		intent.putExtra("outputX", 600);//裁切的宽度
		intent.putExtra("outputY", 600);//裁切的高度
		intent.putExtra("scale", true);//支持缩放
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将裁切的结果输出到指定的Uri
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//裁切成的图片的格式
		intent.putExtra("noFaceDetection", true); // no face detection
		return intent;

	}
	public Intent getIntentToCrop(){
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(imageUri, "image/*");
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 600);
		intent.putExtra("outputY", 600);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		return intent;
		
	}
	public Uri getImageUri(){
		return imageUri;
		
	}
	public String getAbsolutePath(){
		return this.absolutePath;
	}
}
