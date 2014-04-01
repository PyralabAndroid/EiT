package pl.eit.androideit.eit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import android.widget.LinearLayout;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import butterknife.ButterKnife;
import pl.eit.androideit.eit.chanel.ChannelsActivity;


public class MainActivity extends Activity {

    SlidingMenu slidingMenu;
    private Button mScheduleButton;
    private Button mChanelButton;
    private Button mRegistrationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        getActionBar().hide();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        int menuWidth = 300;
        if (width < 300) {
            menuWidth = (int) (width * 0.8);
        }
        if (width * 0.65 > 300)
            menuWidth = (int) (width * 0.65);

        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        slidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.attachToActivity(this, SlidingMenu.TOUCHMODE_MARGIN);
        slidingMenu.setMenu(R.layout.menu);
        slidingMenu.setBehindWidth(menuWidth);

        mScheduleButton = (Button) findViewById(R.id.menu_bt_plan);
        mScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), ScheduleActivity.class));
            }
        });
        mChanelButton = (Button) findViewById(R.id.menu_bt_hotNews);
        mChanelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), ChannelsActivity.class));
            }
        });
        mRegistrationBtn = (Button) findViewById(R.id.menu_bt_register);
        mRegistrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), LogInOrSignUp.class));
            }
        });

        LinearLayout mBaseLayout = (LinearLayout)findViewById(R.id.main_ll_base);
        mBaseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.toggle();
            }
        });
    }
}
