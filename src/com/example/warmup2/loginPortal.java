package com.example.warmup2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class loginPortal extends Activity {
	
	String username;
	int count;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_portal);
		Intent intent = getIntent();
		username = intent.getStringExtra(MainActivity.USERNAME);
		count = intent.getIntExtra(MainActivity.COUNT,1);
		TextView message = (TextView) findViewById(R.id.welcomeText);
		TextView number = (TextView) findViewById(R.id.numberLogin);
		String msg = String.format("You have logged in %d times",count);
		message.setText("Welcome " + username);
		number.setText(msg);		
	}
	
	public void logout(View v) {
		finish();
	}

}
