package pl.eit.androideit.eit.content;

import android.content.Context;
import android.content.SharedPreferences;

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
            mEditor.putBoolean(IS_FIRST_RUN, isFirstRun);
            return this;
        }

        public AppPreferencesEditor setYear(String year) {
            mEditor.putString(PREFERENCE_YEAR, year);
            return this;
        }

        public AppPreferencesEditor setGroup(String group) {
            mEditor.putString(PREFERENCE_GROUP, group);
            return this;
        }

        public AppPreferencesEditor setFirstRun(String site) {
            mEditor.putString(PREFERENCE_SITE, site);
            return this;
        }
    }

    private static final String PREFERENCES_NAME = "app";
    private static final String IS_FIRST_RUN = "is_first_run";
    private static final String PREFERENCE_YEAR = "year";
    private static final String PREFERENCE_GROUP = "group";
    private static final String PREFERENCE_SITE = "site";


    private SharedPreferences mPreferences;

    public AppPreferences(Context context) {
        mPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
    }

    public AppPreferencesEditor edit() {
        return new AppPreferencesEditor(mPreferences);
    }

    public boolean isFirstRun() {
        return mPreferences.getBoolean(IS_FIRST_RUN, true);
    }

    // TODO: change value
    public String getYear() { return mPreferences.getString(PREFERENCE_YEAR, "4");}
    public String getGroup() { return mPreferences.getString(PREFERENCE_GROUP, "T2");}
    public String getSite() { return mPreferences.getString(PREFERENCE_SITE, "left");}
}
