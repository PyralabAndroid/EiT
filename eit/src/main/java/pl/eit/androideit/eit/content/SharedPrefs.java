package pl.eit.androideit.eit.content;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {
	Context context;

	public SharedPrefs(Context context){
		this.context = context;
	}
	
	
	/******************** DANE UŻYTKOWNIKA *********************/
	
	public String getUserName(){
		final SharedPreferences prefs = context.getSharedPreferences("AccountInfo", Context.MODE_PRIVATE);
		String userName = prefs.getString("user", "");
		
		return userName;
	}
	
	public String getAccountEmail(){
		final SharedPreferences prefs = context.getSharedPreferences("AccountInfo", Context.MODE_PRIVATE);
		String email = prefs.getString("email", "");

		return email;
	}

	
	public void clearAccountInfo(){
		final SharedPreferences prefs = context.getSharedPreferences("AccountInfo", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("user", "");
		editor.putString("email", "");
		editor.apply();
	}
	
	/** Zapisuje nazwę uzytkownikaw SharedPreferences. */
	public void saveAccInfo(String name, String email) {
		final SharedPreferences prefs = context.getSharedPreferences("AccountInfo",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("user", name);
		editor.putString("email", email);
		editor.apply();
	}
	
	
	/************************************************************/
	
}
