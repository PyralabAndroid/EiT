package pl.eit.androideit.eit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.ButterKnife;
import pl.eit.androideit.eit.content.AppPreferences;

public class GroupDialog extends DialogFragment{

    private static final String[] mYears = {"1", "2", "3", "4", "5"};
    private static final String[] mGroups = {"T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "ICT"};
    private static final String[] mSide = {"lewa", "prawa"};

    private Spinner spinner_year, spinner_group, spinner_side;
    private AppPreferences mPreferences;

    private AdapterView.OnItemSelectedListener mCustomSelectedListener;
    private CustomDismissDialogListener mCustomDissmisDialogListener;


    public static GroupDialog newInstance() {
        return new GroupDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.starts_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Wybierz swój plan...")
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCustomDissmisDialogListener.onDialogDismiss();
                    }
                })
                .setNegativeButton("Cofnij", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dismiss();
                            }
                        }
                );

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

        spinner_year = (Spinner) view.findViewById(R.id.spinner_year);
        spinner_year.setOnItemSelectedListener(mCustomSelectedListener);
        spinner_group = (Spinner) view.findViewById(R.id.spinner_group);
        spinner_group.setOnItemSelectedListener(mCustomSelectedListener);
        spinner_side = (Spinner) view.findViewById(R.id.spinner_side);
        spinner_side.setOnItemSelectedListener(mCustomSelectedListener);

        mPreferences = new AppPreferences(getActivity());
        int pos = 0;
        for (String year : mYears) {
            if (year.equals(mPreferences.getYear())) {
                spinner_year.setSelection(pos);
                break;
            }
            pos++;
        }
        pos = 0;
        for (String group : mGroups) {
            if (group.equals(mPreferences.getGroup())) {
                spinner_year.setSelection(pos);
                break;
            }
            pos++;
        }
        pos = 0;
        for (String year : mSide) {
            if (year.equals(mPreferences.getSide())) {
                spinner_year.setSelection(pos);
                break;
            }
            pos++;
        }

        addItemOnSpinner(spinner_year, mYears);
        addItemOnSpinner(spinner_group, mGroups);
        addItemOnSpinner(spinner_side, mSide);

        return builder.create();
    }

    private void addItemOnSpinner(Spinner spinner, String[] array) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, new ArrayList<String>(Arrays.asList(array)));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void setOnDismissDialogListener(CustomDismissDialogListener customDismissDialogListener) {
        mCustomDissmisDialogListener = customDismissDialogListener;
    }
}
