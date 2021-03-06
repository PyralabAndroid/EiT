package pl.eit.androideit.eit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class LogInOrSignUp extends Activity {
	Context context;

	String userName;
	Button logInBT, createAccBT, logOutBT;
	TextView infoTV;
	ProgressDialog progDial;
	/** true jeśli użytkownik jest już zalogowany. */
	boolean logged = false;
	ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_in_or_sign_up);
	}

//	/** Obsługa kliknięcia na przycisk 'logOut'. */
//	public void logOut(View view) {
//		// Pokazuje dialog o wylogowywaniu.
//		pDialog = new ProgressDialog(context);
//		pDialog.setCancelable(true);
//		pDialog.setIndeterminate(true);
//		pDialog.setMessage("Logging out");
//		pDialog.show();
//
//		// W drugim wątku wykonuje się wyrejestrowanie regId z serwera.
//		new AsyncTask<Void, Void, String>() {
//			@Override
//			protected String doInBackground(Void... params) {
//				String result = deleteAcc();
//				return result;
//			}
//
//			@Override
//			protected void onPostExecute(String result) {
//				if (pDialog != null) {
//					pDialog.dismiss();
//				}
//				AlertDialogManager dialog = new AlertDialogManager();
//				/** Odpowiedz z serwera **/
//				JSONObject response;
//				Integer success = 0;
//				String error = "";
//				try {
//					response = new JSONObject(result);
//					success = response.getInt("success");
//					error = response.getString("error");
//				} catch (JSONException e1) {
//					e1.printStackTrace();
//				}
//				// Jeśli serwer odpowiedział pozytywnie wyczyść dane dotyczące
//				// konta.
//				if (result.equals("serverProblem")) {
//					dialog.showAlertDialog(LogInOrSignUp.this, "Błąd",
//							"Nie można połączyć się z serwerem. Spróbuj ponownie później.", false, null);
//				}
//				else{
//					if(success == 1){
//						prefs.clearAccountInfo();
//						dialog.showAlertDialog(LogInOrSignUp.this, "",
//								"Wylogowanie powiodło się", true, getIntent()
//										.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//					}
//					else if(error.length() > 0){
//						dialog.showAlertDialog(LogInOrSignUp.this, "Error",
//								"error: " + result, false, null);
//					}
//				}
//			}
//		}.execute();
//	}
//
//	/**
//	 * Wylogowuje regId z serwera.
//	 *
//	 * @return odpowiedz z serwera czy wylogowanie powiodło się
//	 */
//	public String deleteAcc() {
//		String regId = gcmReg.getSavedGCMRegId(context);
//
//		String result = "";
//		JSONObject json = new JSONObject();
//		try {
//			json.put("regId", regId);
//			json.put("email", prefs.getAccountEmail());
//			ServerConnection server = new ServerConnection();
//			result = server.post(ServerConnection.SERVER_LOG_OUT,
//					json.toString());
//
//		} catch (JSONException e2) {
//			e2.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		return result;
//	}
}
