package pl.eit.androideit.eit.chanel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class AlertDialogManager {
	 /**
     * Function to display simple Alert Dialog
     * @param context - application context
     * @param title - alert dialog title
     * @param message - alert message
     * @param status - success/failure (used to set icon)
     *               - pass null if you don't want icon
     * @param intent - intencja z aktywnścią, która ma zostać uruchomiona po wciśnięciu OK
     * 				 - przekazanie null nie uruchomi nowej aktywności.
     * */
    public void showAlertDialog(final Context context, String title, String message,
            Boolean status, final Intent intent) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
 
        // Setting Dialog Title
        alertDialog.setTitle(title);
 
        // Setting Dialog Message
        alertDialog.setMessage(message);
 
        if(status != null)
        // Setting OK Button
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	dialog.dismiss();
            	if(intent != null){
            		context.startActivity(intent);
            	}       	
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
    }
}
