package pl.eit.androideit.eit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;

public class GroupDialog {

    private Spinner spinner_year, spinner_group, spinner_side;
    private Context mContext;

    public void showDialog(final Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.starts_dialog);
        dialog.setTitle("Wybierz swój plan");
        mContext = context;
        spinner_year = (Spinner) dialog.findViewById(R.id.spinner_year);
        spinner_group = (Spinner) dialog.findViewById(R.id.spinner_group);
        spinner_side = (Spinner) dialog.findViewById(R.id.spinner_side);

        addItemOnSpinner(spinner_year,mYears);
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

    String[] mYears = {"I rok", "II rok", "III rok"};
    String[] mGroups = {"T1", "T2","T3","T4","T5","T6","T7","T8"};
    String[] mSide = {"left","right"};

}
