package pl.eit.androideit.eit;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.json.JSONException;
import org.json.JSONObject;

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
import pl.eit.androideit.eit.channel.ChannelActivity;
import pl.eit.androideit.eit.content.AppPreferences;
import pl.eit.androideit.eit.schedule_fragment.ScheduleItem;
import pl.eit.androideit.eit.service.GCMRegister;
import pl.eit.androideit.eit.service.Parser;
import pl.eit.androideit.eit.service.ScheduleFinder;
import pl.eit.androideit.eit.service.ServerConnection;
import pl.eit.androideit.eit.service.model.BaseSchedule;

import static pl.eit.androideit.eit.service.GCMRegister.PROPERTY_APP_VERSION;

public class MainActivity extends ActionBarActivity implements CustomDismissDialogListener {

    SlidingMenu slidingMenu;

    private Button mMenuSchedule, mMenuNews, mMenuChat, mMenuLogoutBt;
    private Parser mParser;
    private BaseSchedule mBaseSchedule;
    private ScheduleFinder mScheduleFinder;
    private AppPreferences mPreferences;
    private ProgressDialog pDialog;

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

    RelativeLayout mLogoutLayout;
    TextView mLogoutUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        pDialog = new ProgressDialog(MainActivity.this);

        /** Jeśli jest net sprawdź aktualność reg_id GCM-a **/
        if(ServerConnection.isOnline(getBaseContext())){
            checkGCMRegId();
        }

        getSupportActionBar().hide();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        int menuWidth = 300;
        if (width < 300) {
            menuWidth = (int) (width * 0.8);
        }
        if (width * 0.65 > 300)
            menuWidth = (int) (width * 0.65);

        slidingMenu = new SlidingMenu(getBaseContext());
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
                startActivity(new Intent(getBaseContext(), ChannelActivity.class));
            }
        });
        mMenuNews = (Button) findViewById(R.id.menu_bt_news);
        mMenuNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), NewsActivity.class));
            }
        });
        mMenuSchedule = (Button) findViewById(R.id.menu_bt_plan);
        mMenuSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), ScheduleActivity.class));
            }
        });
        mMenuLogoutBt = (Button)findViewById(R.id.menu_logout_bt);
        mMenuLogoutBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });


        mPreferences = new AppPreferences(getBaseContext());
        if(mPreferences.isFirstRun()) {
            GroupDialog groupDialog = new GroupDialog(this);
            groupDialog.setOnDismissDialogListener(this);
            groupDialog.showDialog();
            mPreferences.edit().setFirstRun(false).commit();
        }

        setLessonPanel();

        mLogoutUser = (TextView)findViewById(R.id.menu_logout_user_tv);
        mLogoutLayout = (RelativeLayout)findViewById(R.id.menu_logout_layout);
        if(mPreferences.isLoggedIn()){
            mLogoutLayout.setVisibility(View.VISIBLE);
            mLogoutUser.setText(mPreferences.getUserName());
        }
        else{
            mLogoutLayout.setVisibility(View.INVISIBLE);
            mMenuLogoutBt.setVisibility(View.INVISIBLE);
        }
    }

    /** Sprawdza czy Reg_id z GCM-a jest aktualne **/
    private void checkGCMRegId() {
        GCMRegister gcmReg = new GCMRegister();
        // Pobieram registration id z SharedPreferences
        String regid = gcmReg.getSavedGCMRegId(getBaseContext());
        Log.d("reg id z preferencji", " " + regid);

        // Jeśli istnieje regId sprawdzam czy jest nadal ważne.
        if (regid != null && regid.length() > 0) {
            SharedPreferences prefs = gcmReg.getGCMPreferences(this);
            // Sprawdzam czy wersja aplikacji nie zmieniłą się. Jeśli tak to konieczna jest aktualizacja regId.
            int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
            int currentVersion = gcmReg.getAppVersion(getBaseContext());
            if (registeredVersion != currentVersion || gcmReg.isRegistrationExpired(this)) {
                Log.d("GCM", "Wersja aplikacji zmieniła się lub wygasła rejestracja.");
                // Rejestracja w GCM nowym regId + aktualizacja regId na serwerze.
                new GCMRegister(getBaseContext(), regid, true).execute(null, null, null);
            }
        }
    }

	public void logOut() {
        if(ServerConnection.isOnline(this)){
            // Pokazuje dialog o wylogowywaniu.
            pDialog.setCancelable(true);
            pDialog.setIndeterminate(true);
            pDialog.setMessage("Wylogowywanie");
            pDialog.show();

            // wyrejestrowanie regId z serwera.
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    String result = deleteAcc();
                    return result;
                }

                @Override
                protected void onPostExecute(String result) {
                    if (pDialog != null && !isFinishing()) {
                        pDialog.dismiss();
                    }
                    AlertDialogManager dialog = new AlertDialogManager();
                    /** Odpowiedz z serwera **/
                    JSONObject response;
                    Integer success = 0;
                    String error = "";
                    try {
                        response = new JSONObject(result);
                        success = response.getInt("success");
                        error = response.getString("error");
                    } catch (JSONException e1) {
                        throw new RuntimeException(e1.getMessage() + ". Server message: " + result);
                    }
                    if (result.equals("serverProblem")) {
                        dialog.showAlertDialog(MainActivity.this, "Błąd",
                                "Nie można połączyć się z serwerem. Spróbuj ponownie później.", false, null);
                    }
                    else{
                        if(success == 1) {
                            mPreferences.edit().clearUserData();
                            Intent intent = IntentCompat
                                    .makeRestartActivityTask(new ComponentName(MainActivity.this, StartActivity.class));
                            dialog.showAlertDialog(MainActivity.this, "",
                                    "Wylogowanie powiodło się", true, intent);
                        }
                        else if(error.length() > 0){
                            dialog.showAlertDialog(MainActivity.this, "Error",
                                    "error: " + result, false, null);
                        }
                    }
                }
            }.execute();
        }
        else{
            Toast.makeText(this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
        }

	}

	public String deleteAcc() {
        GCMRegister gcmReg = new GCMRegister();
		String regId = gcmReg.getSavedGCMRegId(getBaseContext());

		String result = "";
		JSONObject json = new JSONObject();
		try {
			json.put("regId", regId);
			json.put("email", mPreferences.getUserEmail());
			ServerConnection server = new ServerConnection();
			result = server.post(ServerConnection.SERVER_LOG_OUT,
					json.toString());

		} catch (JSONException e2) {
			e2.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
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
        if (list != null) {
            for (ScheduleItem item : list) {
                Date data = new SimpleDateFormat("HH:mm").parse(item.mTime);
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.HOUR_OF_DAY, data.getHours());
                calendar1.set(Calendar.MINUTE, data.getMinutes());
                if (calendar1.after(calendar)) {
                    return item;
                }
            }
        }
        return null;
    }

    @Override
    public void onDialogDismiss() {
        setLessonPanel();
    }

    private void setLessonPanel() {
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
}
