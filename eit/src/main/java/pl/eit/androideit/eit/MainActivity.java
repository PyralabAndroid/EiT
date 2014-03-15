package pl.eit.androideit.eit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import butterknife.OnClick;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @OnClick(R.id.imageView)
    public void onImageClick(){
        startActivity(new Intent(getBaseContext(), ScheduleActivity.class));
    }
}
