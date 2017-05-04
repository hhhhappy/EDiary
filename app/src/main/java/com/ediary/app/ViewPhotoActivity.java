package com.ediary.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;

/**
 * Created by lenovo on 2017/3/7.
 */
public class ViewPhotoActivity extends BaseActivity implements View.OnClickListener{
    private ImageView imageView;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_picture_layout);
        imageView = (ImageView) findViewById(R.id.view_picture);
        linearLayout = (LinearLayout) findViewById(R.id.view_linear_layout);

        //set listener
        imageView.setOnClickListener(this);
        linearLayout.setOnClickListener(this);

        //get intent

        Intent intent=getIntent();
        String photoPath=intent.getStringExtra("photoPath");
        Log.d("lisphotoname",photoPath);

        Bitmap imageBitmap = BitmapFactory.decodeFile(photoPath);
        imageView.setImageBitmap(imageBitmap);


    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.view_linear_layout:
                finish();
                break;
            case R.id.view_picture:
                finish();
                break;
        }
    }
}
