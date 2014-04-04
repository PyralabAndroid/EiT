package pl.eit.androideit.eit;

import android.app.Application;

import dagger.ObjectGraph;
import pl.eit.androideit.eit.dagger.ApplicationModule;
import pl.eit.androideit.eit.dagger.ApplicationStageModule;

public class MainApplication extends Application {

    private ObjectGraph mApplicationGraph;

    private Object mApplicationModule = BuildConfig.DEBUG
            ? new ApplicationStageModule(this)
            : new ApplicationModule(this);

    @Override
    public void onCreate() {
        super.onCreate();

        initializeDagger();
    }

    private void initializeDagger() {
        mApplicationGraph = ObjectGraph.create(mApplicationModule);
        mApplicationGraph.inject(this);
    }

    public ObjectGraph getApplicationGraph() {
        return mApplicationGraph;
    }

    public static MainApplication fromApplication(Application application) {
        return (MainApplication) application;
    }
}
