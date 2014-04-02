package pl.eit.androideit.eit;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;

import pl.eit.androideit.eit.content.AppPreferences;

public class GroupDialog {

    private static final String[] mYears = {"I rok", "II rok", "III rok", "IV rok", "V rok"};
    private static final String[] mGroups = {"T1", "T2","T3","T4","T5","T6","T7","T8", "ICT"};
    private static final String[] mSide = {"lewa","prawa"};

    private Spinner spinner_year, spinner_group, spinner_side;
    private Context mContext;
    private AppPreferences mPreferences;

    private AdapterView.OnItemSelectedListener mCustomSelectedListener;
    public void showDialog(final Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.starts_dialog);
        dialog.setTitle("Wybierz swój plan...");
        mContext = context;

        mCustomSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value;
                switch(parent.getId()){
                    case R.id.spinner_year:
                        value = mYears[position];
                        mPreferences.edit().setYear(value).commit();
                        break;
                    case R.id.spinner_group:
                        value = mGroups[position];
                        mPreferences.edit().setGroup(value).commit();
                        break;
                    case R.id.spinner_side:
                        value = mSide[position];
                        mPreferences.edit().setSite(value).commit();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        spinner_year = (Spinner) dialog.findViewById(R.id.spinner_year);
        spinner_year.setOnItemSelectedListener(mCustomSelectedListener);
        spinner_group = (Spinner) dialog.findViewById(R.id.spinner_group);
        spinner_group.setOnItemSelectedListener(mCustomSelectedListener);
        spinner_side = (Spinner) dialog.findViewById(R.id.spinner_side);
        spinner_side.setOnItemSelectedListener(mCustomSelectedListener);
        mPreferences = new AppPreferences(context);

        addItemOnSpinner(spinner_year, mYears);
        addItemOnSpinner(spinner_group, mGroups);
        addItemOnSpinner(spinner_side,mSide);

        dialog.show();
    }

    private void addItemOnSpinner(Spinner spinner, String[] array) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, new ArrayList<String>(Arrays.asList(array)));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }
}
