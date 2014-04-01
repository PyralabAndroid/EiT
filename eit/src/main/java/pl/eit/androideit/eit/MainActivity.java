package pl.eit.androideit.eit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.eit.androideit.eit.chanel.ChannelsActivity;
import pl.eit.androideit.eit.schedule_fragment.ScheduleItem;
import pl.eit.androideit.eit.service.Parser;
import pl.eit.androideit.eit.service.ScheduleFinder;
import pl.eit.androideit.eit.service.model.BaseSchedule;


public class MainActivity extends Activity {

    SlidingMenu slidingMenu;

    private Button mMenuSchedule, mMenuNews, mMenuChat;
    private Parser mParser;
    private BaseSchedule mBaseSchedule;
    private ScheduleFinder mScheduleFinder;

    @InjectView(R.id.base_schedule_row_name)
    TextView mScheduleName;
    @InjectView(R.id.base_schedule_row_type)
    TextView mScheduleType;
    @InjectView(R.id.base_schedule_row_time)
    TextView mScheduleTime;
    @InjectView(R.id.base_schedule_row_place)
    TextView mSchedulePlace;
    @InjectView(R.id.main_text_lesson)
    TextView mTextLesson;
    @InjectView(R.id.schedule_frame)
    LinearLayout mScheduleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

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

        LinearLayout mBaseLayout = (LinearLayout)findViewById(R.id.main_ll_base);
        mBaseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.toggle();
            }
        });

        mMenuChat = (Button) findViewById(R.id.menu_bt_chat);
        mMenuChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), ChannelsActivity.class));
            }
        });

        mMenuNews = (Button) findViewById(R.id.menu_bt_news);
        mMenuNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getBaseContext(), NewsActivity.class));
            }
        });

        mMenuSchedule = (Button) findViewById(R.id.menu_bt_plan);
        mMenuSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), ScheduleActivity.class));
            }
        });

        mParser = new Parser(getBaseContext());
        mBaseSchedule = null;
        ScheduleItem item = null;
        try {
            item = setNextLesson();
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage());
        }
        if (item != null) {
            setItem(item);
        } else {
            mScheduleLayout.setVisibility(View.GONE);
            mTextLesson.setVisibility(View.VISIBLE);
        }
    }

    private void setItem(ScheduleItem item) {
        mScheduleName.setText(item.mName);
        mSchedulePlace.setText(item.mPlace);
        mScheduleTime.setText(item.mTime);
        mScheduleType.setText(item.mType);
    }

    private ScheduleItem setNextLesson() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        try {
            Reader reader = new InputStreamReader(getAssets().open("schedule.json"));
            mBaseSchedule = mParser.parseSchedule(reader);
        } catch (IOException e) {
            throw new RuntimeException("Error when parsing schedule!");
        }
        mScheduleFinder = new ScheduleFinder(this, mBaseSchedule, (calendar.get(Calendar.DAY_OF_WEEK) - 1));
        List<ScheduleItem> list = mScheduleFinder.getScheduleList();
        for (ScheduleItem item : list) {
            Date data = new SimpleDateFormat("HH:mm").parse(item.mTime);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.HOUR_OF_DAY, data.getHours());
            calendar1.set(Calendar.MINUTE, data.getMinutes());
            if (calendar1.after(calendar)) {
                return item;
            }
        }
        return null;
    }
}
