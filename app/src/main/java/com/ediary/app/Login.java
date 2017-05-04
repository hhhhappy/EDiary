package com.ediary.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Login extends BaseActivity implements OnClickListener{

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_layout);
		
		LinearLayout li_First = (LinearLayout) findViewById(R.id.interface_first);
		LinearLayout li_Second = (LinearLayout) findViewById(R.id.interface_second);
		
		SharedPreferences sharedPreferences = this.getSharedPreferences("share", MODE_PRIVATE);
		boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		if (isFirstRun)
		{
			/*first interface*/
			editor.putBoolean("isFirstRun", false);
			
			li_Second.setVisibility(View.GONE);
			Button button_save_password = (Button)findViewById(R.id.login_button_save);
			button_save_password.setOnClickListener(this);
			editor.commit();
		       
		} 
		else
		{
			/*normal interface*/
			li_First.setVisibility(View.GONE);
			Button button_login = (Button)findViewById(R.id.login_button_login);
			button_login.setOnClickListener(this);
		}
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		SharedPreferences sharedPreferences = this.getSharedPreferences("share", MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		switch (v.getId()){
		case R.id.login_button_login:
			EditText editPassword = (EditText)findViewById(R.id.login_password);
			String password = editPassword.getText().toString();
			String passwordStored = sharedPreferences.getString("password", "");
			
			
			
			if(password.equals(passwordStored)){
				Intent intent = new Intent(Login.this, Main.class);
				startActivity(intent);
			}
			else{
				Toast.makeText(Login.this, "Wrong password!", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.login_button_save:
			EditText editPasswordSave = (EditText)findViewById(R.id.login_password_save);
			EditText editPasswordVerifier = (EditText)findViewById(R.id.login_password_verifier);
			String passwordSave = editPasswordSave.getText().toString();
			String passwordVerifier = editPasswordVerifier.getText().toString();
			if(passwordSave.equals(passwordVerifier)){
				
				editor.putString("password", passwordSave);
				editor.commit();
				Intent intent = new Intent(Login.this, Main.class);
				startActivity(intent);
			}
			else{
				Toast.makeText(Login.this, "Please enter the same passwords!", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}
}
