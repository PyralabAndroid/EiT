package pl.eit.androideit.eit;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;

import pl.eit.androideit.eit.content.AppPreferences;

public class GroupDialog {

    private static final String[] mYears = {"1", "2", "3", "4", "5"};
    private static final String[] mGroups = {"T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "ICT"};
    private static final String[] mSide = {"lewa", "prawa"};

    private Spinner spinner_year, spinner_group, spinner_side;
    private Context mContext;
    private AppPreferences mPreferences;

    private AdapterView.OnItemSelectedListener mCustomSelectedListener;
    private CustomDismissDialogListener mCustomDissmisDialogListener;

    private Dialog mDialog;

    public GroupDialog(Context context) {
        mContext = context;

        mDialog = new Dialog(context);
        mDialog.setContentView(R.layout.starts_dialog);
        mDialog.setTitle("Wybierz swój plan...");
        mContext = context;

        mCustomSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value;
                switch (parent.getId()) {
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
                        mPreferences.edit().setSide(value).commit();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mCustomDissmisDialogListener != null ) {
                    mCustomDissmisDialogListener.onDialogDismiss();
                }
            }
        });

        spinner_year = (Spinner) mDialog.findViewById(R.id.spinner_year);
        spinner_year.setOnItemSelectedListener(mCustomSelectedListener);
        spinner_group = (Spinner) mDialog.findViewById(R.id.spinner_group);
        spinner_group.setOnItemSelectedListener(mCustomSelectedListener);
        spinner_side = (Spinner) mDialog.findViewById(R.id.spinner_side);
        spinner_side.setOnItemSelectedListener(mCustomSelectedListener);
        mPreferences = new AppPreferences(context);
        int pos = 0;
        for (String year : mYears) {
            if (year.equals(mPreferences.getYear())) {
                spinner_year.setSelection(pos);
                return;
            }
            pos++;
        }
        pos = 0;
        for (String group : mGroups) {
            if (group.equals(mPreferences.getGroup())) {
                spinner_year.setSelection(pos);
                return;
            }
            pos++;
        }
        pos = 0;
        for (String year : mSide) {
            if (year.equals(mPreferences.getSide())) {
                spinner_year.setSelection(pos);
                return;
            }
            pos++;
        }
    }

    public void showDialog() {
        addItemOnSpinner(spinner_year, mYears);
        addItemOnSpinner(spinner_group, mGroups);
        addItemOnSpinner(spinner_side, mSide);
        mDialog.show();
    }

    private void addItemOnSpinner(Spinner spinner, String[] array) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, new ArrayList<String>(Arrays.asList(array)));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void setOnDismissDialogListener(CustomDismissDialogListener customDismissDialogListener) {
        mCustomDissmisDialogListener = customDismissDialogListener;
    }
}
