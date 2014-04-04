package pl.eit.androideit.eit;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

import butterknife.ButterKnife;
import dagger.ObjectGraph;
import pl.eit.androideit.eit.dagger.ActivityGraphProvider;
import pl.eit.androideit.eit.dagger.FragmentModule;

import static com.google.common.base.Preconditions.checkNotNull;

public class BaseFragment extends DialogFragment {

    private ObjectGraph mFragmentGraph;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            final ActivityGraphProvider graphProvider = checkNotNull((ActivityGraphProvider) getActivity());
            mFragmentGraph = graphProvider.getActivityGraph().plus(new FragmentModule(this));
        } catch (ClassCastException e) {
            throw new RuntimeException("Activity does not implement ActivityGraphProvider", e);
        }

        mFragmentGraph.inject(this);

        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    @Override
    public void onDestroyView() {
        ButterKnife.reset(this);
        super.onDestroyView();
    }
}
