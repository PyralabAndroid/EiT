package pl.eit.androideit.eit.chanel;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import pl.eit.androideit.eit.R;

public class RegisterActivity extends Activity implements GCMRegister.AsyncResponse {

	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";

	// Okres ważności regID. Po 7 dniach RegId uznawany jest jako nieważny.
	public static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7;

	// Tag used on log messages.
	static final String TAG = "GCM";
	// alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();
	AlertDialog.Builder registerDialog;
	ProgressDialog pDialog;

	EditText txtPassword, txtName, txtEmail;
	Button btnRegister;
	WebView webView;

	String regid, isRegistered, encryptedPass, email, password, userName, createOrLogin;
	GoogleCloudMessaging gcm;
	Context context;
	boolean isRegisterSuccess;
	GCMRegister gcmReg;
	Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		setActionBar();

		context = RegisterActivity.this;
		intent = getIntent();
		createOrLogin = intent.getStringExtra("createOrLogin");
		
		gcm = GoogleCloudMessaging.getInstance(this);
		registerDialog = new AlertDialog.Builder(RegisterActivity.this);

		txtPassword = (EditText) findViewById(R.id.registerPassword);
		txtName = (EditText) findViewById(R.id.registerName);
		txtEmail = (EditText) findViewById(R.id.registerEmail);
		btnRegister = (Button) findViewById(R.id.btnRegister);

		gcmReg = new GCMRegister();

		// Jeśli jest logowanie zmień nazwę przycisku na Zaloguj oraz ukryj pole nazwy usera.
		if(createOrLogin.equals("login")){
			btnRegister.setText("Zaloguj");
			txtName.setVisibility(View.GONE);
		}
		

		/** Obsługa przycisku rejestracji. */
		btnRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				password = txtPassword.getText().toString().trim();
				userName = txtName.getText().toString().trim();
				email = txtEmail.getText().toString().trim();

				pDialog = new ProgressDialog(RegisterActivity.this);
				pDialog.setCancelable(true);
				pDialog.setIndeterminate(true);
				pDialog.setMessage("Trwa rejestracja. Proszę czekać :-)");

				// Sprawdzam czy użytkownik wypełnił formularz.
				if (email.length() > 0) {
					if(createOrLogin.equals("create")){
						if(userName.length() <= 0){
							// Użytkownik nie podał nazwy usera
							alert.showAlertDialog(RegisterActivity.this,
									"", "Proszę podać nazwę użytkownika", false, null);
							return;
						}
					}
					// Jeśli hasło jest za krótkie. Pokaż ostrzeżenie i zfokusuj
					// edittext hasła.
					if (password.length() < 6) {
						Toast.makeText(context,
								"Hasło musi mieć conajmniej 6 znaków.",
								Toast.LENGTH_LONG).show();
						txtPassword.requestFocus();
						return;
					}
					
					// Szyfrowanie hasła.
					try {
						Crypt crypt = new Crypt();
						encryptedPass = Crypt.bytesToHex(crypt.encrypt(password));
						//Log.d("zakodowane hasło", encryptedPass.toString());

					} catch (Exception e) {
						e.printStackTrace();
					}

					// Jeśli wprowadzone dane są prawidłowe, pokaż ProgressDialog
					// i zarejestruj użytkownika w GCM oraz na serwerze
					pDialog.show();
					if(createOrLogin.equals("create")){
						new GCMRegister(context, true, email, encryptedPass, userName, false, RegisterActivity.this)
						.execute(null, null, null);
					}
					else{
						new GCMRegister(context, false, email, encryptedPass, false, RegisterActivity.this)
						.execute(null, null, null);
					}
					

				} 
				else{
					// Użytkownik nie wypełnij wszystkich danych.
					alert.showAlertDialog(RegisterActivity.this,
							"Registration Error!", "Please enter your details", false, null);
				}
			}
		});
	}
	
	private void setActionBar(){
		final ActionBar actionBar = getActionBar();
		actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>Registration</font>"));
	}

	// Funkcja z interfejsu AsyncResponse. Wykonuje się po
	// skończeniu rejestrowania w GCM w drugim wątku.
	public void processFinish(String serverResponse) {
		Log.d("reg result", "result:" + serverResponse);
		// Wyłączam progress dialog.
		if (pDialog != null) {
			pDialog.dismiss();
		}

		// Sprawdzam odpowiedź z serwera.
		if (serverResponse != null) {
			// Wyświetl komunikaty o statusie rejestracji.
			if (serverResponse.equals("serverProblem")) {
				alert.showAlertDialog(RegisterActivity.this, "Błąd",
						"Nie można połączyć się z serwerem. Spróbuj ponownie później.", false, null);
			} 
			else{
				// Odpowiedz z serwrea w JSONIE
				JSONObject response;
				String info = "";
				Integer success = 0;
				String error = "";
				/** Zwracana nazwa użytkownika podczas logowania 
				 * Użytkownik przy logowaniu podaje adres email i haslo, więc
				 * nazwa użytkownika musi zostać zwrócona z serwera**/
				String userNameFromLogin = "";
				try {
					response = new JSONObject(serverResponse);
					success = response.getInt("success");
					error = response.getString("error");
					info = response.getString("info");
					userNameFromLogin = response.getString("userName");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				// Były jakieś błędy, np. zajęte nazwa usera
				if(info.length() > 0){
					if (info.equals("occupied")) {
						alert.showAlertDialog(RegisterActivity.this,
								"", "Podany adres email oraz nazwa użytkownika są już zajęte.", false, null);
					}
					else if (info.equals("occupiedEmail")) {
						alert.showAlertDialog(RegisterActivity.this,
								"", "Podany adres email jest już zajęty.", false, null);
					}
					else if (info.equals("occupiedUserName")) {
						alert.showAlertDialog(RegisterActivity.this,
								"", "Podana nazwa użytkownika jest już zajęta.", false, null);
					}
					else if(info.equals("badPass")){
						alert.showAlertDialog(RegisterActivity.this,
								"", "Incorrect password.", false, null);
					}
					else if(info.equals("accountNotFound")){
						alert.showAlertDialog(RegisterActivity.this,
								"", "Konto powiązane z podanym adresem email nie istnieje.", false, null);
					}
				}
				// Jeśli nie ma błędów oraz informacji zwrotnych a success=1 to wszystko jest OK
				else if(success == 1){
					Intent intent = new Intent(RegisterActivity.this, ChannelsActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					
					String message;
					if(createOrLogin.equals("create")){
						message = "Rejestracja udana :)";
					}
					else{
						message = "Logowanie powiodło się :)";
						if(userNameFromLogin.length() > 0){
							this.userName = userNameFromLogin;
						}
					}
					alert.showAlertDialog(RegisterActivity.this, "", message, true, intent);
					// Po pomyślnej rejestracji zapisz nazwę i email
					// użytkownika.
					SharedPrefs prefs = new SharedPrefs(context);
					prefs.saveAccInfo(userName, email);
				}
				// Blędy z bazy danych
				if(error.length() > 0){
					alert.showAlertDialog(RegisterActivity.this,
							"Registration Error", "Error: " + error, false, null);
				}
				
			}

		}
	}

}
