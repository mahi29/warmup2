package com.example.warmup2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	EditText user;
	EditText pwd;
	TextView messageBox;
	String username;
	String password;
	Context context;
	HttpURLConnection httpConnection;
	private final String BASE_URL = "http://ancient-spire-1285.herokuapp.com";
	protected final static String COUNT = "count";
	protected final static String ERR_CODE = "err_code";
	protected final static String USERNAME = "username";
	private final static int BAD_CREDENTIALS = -1;
	private final static int USER_EXISTS = -2;
	private final static int BAD_USERNAME = -3;
	private final static int BAD_PASSWORD = -4;
	private boolean ifAdd = false; //set to True if 'Add User'; false if 'Log In'
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		user = (EditText) findViewById(R.id.username);
		pwd = (EditText) findViewById(R.id.password);
		messageBox = (TextView) findViewById(R.id.messageBox);
		username = user.getText().toString().trim();
		password = pwd.getText().toString().trim();
		
	}

	//Method called when 'Add User' button clicked
	public void newUser(View v) {
		ifAdd = true;
		startJSON();
		
	}
	
	//Method called when 'Log In' button clicked
	public void loginUser(View v){
		ifAdd = false;
		startJSON();
	}
	
	private void startJSON() {
		try {
			getFields();
			if (username == null || username.equals("")) {
				messageBox.setTextColor(Color.RED);
				messageBox.setText(R.string.bad_username);
			} else {
				postTask task = new postTask();
				JSONObject json = new JSONObject();
				json.put("user",username);
				json.put("password",password);
				task.execute(json);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void getFields() {
		username = user.getText().toString().trim();
		password = pwd.getText().toString().trim();
	}
	
	protected void onResume() {
		super.onResume();
		messageBox.setText(R.string.welcome_text);
		messageBox.setTextColor(Color.BLACK);
		user.setText("");
		pwd.setText("");
	}

	private class postTask extends AsyncTask<JSONObject, Void, String> {

		@Override
		protected String doInBackground(JSONObject... jsonParam) {
			HttpURLConnection urlConn = null;
			String result = "-100";
			JSONObject json = jsonParam[0];
			try {
				URL url;
				String address = BASE_URL+"/users/login";
				if (ifAdd) address = BASE_URL+"/users/add";
				url = new URL (address);
				//Create the connection
				urlConn = (HttpURLConnection) url.openConnection();
				urlConn.setDoInput (true);
				urlConn.setDoOutput (true);
				urlConn.setUseCaches (false);
				urlConn.setRequestMethod("POST");
				urlConn.setChunkedStreamingMode(0);
				urlConn.setRequestProperty("Content-Type","application/json");   
				urlConn.connect();  
				//Send the POST request to the back-end
				byte[] outputBytes = json.toString().getBytes("UTF-8");
				OutputStream os = urlConn.getOutputStream();
				os.write(outputBytes);
				os.flush();
				os.close();
				//Read the incoming JSON from the back-end
				StringBuilder builder = new StringBuilder();
				InputStream is = urlConn.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String line;
		        while ((line = reader.readLine()) != null) {
		            builder.append(line);
		        }
		        result = builder.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}  finally {
				if(urlConn !=null)  urlConn.disconnect(); 
			}
			return result;
		}
		
		protected void onPostExecute(String result) {
			try {
				JSONObject out= new JSONObject(result); 
				int errCode = Integer.parseInt(out.getString("errCode"));
				messageBox.setTextColor(Color.RED);
				if (errCode == 1) {
					int count = Integer.parseInt(out.getString("count"));
					Intent i = new Intent(context, loginPortal.class);
					i.putExtra(USERNAME, username);
					i.putExtra(COUNT, count);
					startActivity(i);
				} else if (errCode == BAD_CREDENTIALS) {
					messageBox.setText(R.string.bad_credentials);
				} else if (errCode == USER_EXISTS) {
					messageBox.setText(R.string.user_exists);
				} else if (errCode == BAD_USERNAME) {
					messageBox.setText(R.string.bad_username);
				} else if (errCode == BAD_PASSWORD) {
					messageBox.setText(R.string.bad_password);
				}
			}
			catch (JSONException e) {
				e.printStackTrace();
			}			
		}	
	}
	
}
