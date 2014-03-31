package pl.eit.androideit.eit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.eit.androideit.eit.content.SharedPrefs;


public class StartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
        ButterKnife.inject(this);
//        checkIsUserLogIn();
	}

    private void checkIsUserLogIn() {
        SharedPrefs sharedPrefs = new SharedPrefs(this);
        // TODO: check this
        if (!sharedPrefs.getUserName().isEmpty()) {
            startApp();
        }
    }

    @OnClick(R.id.home)
    public void startApp(){
        startActivity(new Intent(this, MainActivity.class));
    }

}
