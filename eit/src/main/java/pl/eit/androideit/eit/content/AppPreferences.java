package pl.eit.androideit.eit.content;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.StringUtils;

public class AppPreferences {

    public static class AppPreferencesEditor {
        private SharedPreferences.Editor mEditor;

        AppPreferencesEditor(SharedPreferences preferences) {
            mEditor = preferences.edit();
        }

        public boolean commit() {
            return mEditor.commit();
        }

        public AppPreferencesEditor clear() {
            mEditor.clear();
            return this;
        }

        public AppPreferencesEditor setFirstRun(boolean isFirstRun) {
            mEditor.putBoolean(PREFERENCES_IS_FIRST_RUN, isFirstRun);
            return this;
        }

        public AppPreferencesEditor setYear(String year) {
            mEditor.putString(PREFERENCES_YEAR, year);
            return this;
        }

        public AppPreferencesEditor setGroup(String group) {
            mEditor.putString(PREFERENCES_GROUP, group);
            return this;
        }

        public AppPreferencesEditor setSide(String side) {
            mEditor.putString(PREFERENCES_SIDE, side);
            return this;
        }

        public AppPreferencesEditor setUserName(String name) {
            mEditor.putString(PREFERENCES_USER_NAME, name);
            return this;
        }

        public AppPreferencesEditor setUserEmail(String email) {
            mEditor.putString(PREFERENCES_EMAIL, email);
            return this;
        }

        public AppPreferencesEditor clearUserData(){
            setUserName("").commit();
            setUserEmail("").commit();
            setFirstRun(true).commit();
            return this;
        }
    }

    private static final String PREFERENCES_NAME = "app";
    private static final String PREFERENCES_IS_FIRST_RUN = "is_first_run";
    private static final String PREFERENCES_YEAR = "year";
    private static final String PREFERENCES_GROUP = "group";
    private static final String PREFERENCES_SIDE = "side";
    private static final String PREFERENCES_USER_NAME = "user";
    private static final String PREFERENCES_EMAIL = "email";


    private SharedPreferences mPreferences;

    public AppPreferences(Context context) {
        mPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
    }

    public AppPreferencesEditor edit() {
        return new AppPreferencesEditor(mPreferences);
    }

    public boolean isFirstRun() {
        return mPreferences.getBoolean(PREFERENCES_IS_FIRST_RUN, true);
    }

    public boolean isLoggedIn() {return !(Strings.isNullOrEmpty(getUserEmail()) || Strings.isNullOrEmpty(getUserName()));}

    // TODO: change value
    public String getYear() { return mPreferences.getString(PREFERENCES_YEAR, "4");}
    public String getGroup() { return mPreferences.getString(PREFERENCES_GROUP, "T2");}
    public String getSide() { return mPreferences.getString(PREFERENCES_SIDE, "left");}
    public String getUserName() {
        return mPreferences.getString(PREFERENCES_USER_NAME, null);
    }
    public String getUserEmail() {
        return mPreferences.getString(PREFERENCES_EMAIL, null);
    }
}
