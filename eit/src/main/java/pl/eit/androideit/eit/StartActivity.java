package pl.eit.androideit.eit;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class StartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		ActionBar ab = getActionBar();
		ab.setDisplayShowHomeEnabled(false);
		ab.setTitle("Start");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.start_register_window) {
			Intent intent = new Intent(this, LogInOrSignUp.class);
			startActivity(intent);
			return true;
		} else {
			return false;
		}
	}



	

}
