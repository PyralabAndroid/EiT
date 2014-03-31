package pl.eit.androideit.eit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.common.base.Strings;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.eit.androideit.eit.content.SharedPrefs;
import pl.eit.androideit.eit.service.GCMRegister;

public class StartActivity extends Activity {

    GCMRegister gcmReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.inject(this);

        checkIsUserLogIn();
    }

    private void checkIsUserLogIn() {
        SharedPrefs sharedPrefs = new SharedPrefs(this);
        // TODO: check this
        if (!Strings.isNullOrEmpty(sharedPrefs.getUserName()) &&
                !Strings.isNullOrEmpty(sharedPrefs.getAccountEmail())) {
            startApp();
        }
    }

    @OnClick(R.id.home)
    public void startApp() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @OnClick(R.id.start_login)
    public void logIn() {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("createOrLogin", "login");
        startActivity(intent);
    }

    @OnClick(R.id.start_create)
    public void createAcc() {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("createOrLogin", "create");
        startActivity(intent);
    }

}
