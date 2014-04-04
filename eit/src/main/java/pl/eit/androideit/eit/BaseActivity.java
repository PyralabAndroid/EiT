package pl.eit.androideit.eit;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import dagger.ObjectGraph;
import pl.eit.androideit.eit.dagger.ActivityGraphProvider;
import pl.eit.androideit.eit.dagger.ActivityModule;

public abstract class BaseActivity extends ActionBarActivity implements ActivityGraphProvider {

    private ObjectGraph mActivityGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivityGraph().inject(this);
    }

    @Override
    public ObjectGraph getActivityGraph() {
        if (mActivityGraph == null) {
            mActivityGraph = MainApplication.fromApplication(getApplication())
                    .getApplicationGraph()
                    .plus(new ActivityModule(this));
        }

        return mActivityGraph;
    }

}
