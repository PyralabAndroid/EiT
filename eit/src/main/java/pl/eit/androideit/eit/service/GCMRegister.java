package pl.eit.androideit.eit.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;

public class GCMRegister extends AsyncTask<Void, Void, String> {

	boolean isUpdate, newAcc;
	String password, email, userName, oldRegId, regId;
	Context context;

	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	public static final String PROPERTY_APP_VERSION = "appVersion";
	public static final String PROPERTY_ON_SERVER_EXPIRATION_TIME = "onServerExpirationTimeMs";
	public static final String TAG = "GCM";
	/**
	 * Default lifespan (7 days) of a reservation until it is considered
	 * expired.
	 */
	public static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7;
	private final String SENDER_ID = "742187645451";
	GoogleCloudMessaging gcm;
	public AsyncResponse delegate = null;


	/** Konstruktor dla tworzenia konta **/
	public GCMRegister(Context context, Boolean newAcc, String email,
			String password, String userName, boolean isUpdate,
			AsyncResponse delegate) {
		this.isUpdate = isUpdate;
		this.password = password;
		this.email = email;
		this.context = context;
		this.delegate = delegate;
		this.newAcc = newAcc;
		this.userName = userName;
	}

	/** Konstruktor dla logowania **/
	public GCMRegister(Context context, Boolean newAcc, String email,
			String password, boolean isUpdate, AsyncResponse delegate) {
		this.isUpdate = isUpdate;
		this.password = password;
		this.email = email;
		this.context = context;
		this.delegate = delegate;
		this.newAcc = newAcc;
	}

	/** Konstruktor dla aktualizacji gcm_regId na serwerze **/
	public GCMRegister(Context context, String oldRegId, boolean isUpdate) {
		this.isUpdate = isUpdate;
		this.oldRegId = oldRegId;
		this.context = context;
	}

	public GCMRegister() {
	}

	/** Interfejs łączący GCMRegister oraz RegisterActivity. **/
	public interface AsyncResponse {
		void processFinish(String serverResponse);
	}

	@Override
	protected String doInBackground(Void... params) {
		String serverResponse = "failed";
		try {
			if (gcm == null) {
				gcm = GoogleCloudMessaging.getInstance(context);
			}

			// Rejestracja w usłudze GCM
			regId = gcm.register(SENDER_ID);
			Log.d("reg_id ", "Device registered in GCM, registration id="
					+ regId + " ");

			// Jeśli otrzymano regId z serwera GCM
			if (regId != null) {
				// Zapisz reg_id w local prefs
				setRegistrationId(context, regId);

				ServerConnection server = new ServerConnection();
				JSONObject json;

				// Aktualizuj registration_id
				if (isUpdate) {
					json = new JSONObject();
					try {
						json.put("newRegId", regId);
						json.put("oldRegId", oldRegId);
					} catch (JSONException e2) {
						e2.printStackTrace();
					}
					serverResponse = server
							.post(ServerConnection.SERVER_UPDATE_URL,
									json.toString());
				}
				// Zarejestruj nowe konto lub zaloguj
				else {
					json = new JSONObject();
					try {
						json.put("regId", regId);
						json.put("password", password);
						json.put("email", email);
						json.put("userName", userName);
					} catch (JSONException e2) {
						e2.printStackTrace();
					}

					// Jeśli jest tworzone nowe konto.
					if (newAcc) {
						serverResponse = server.post(
								ServerConnection.SERVER_REGISTER,
								json.toString());
					}
					// Jeśli użytkownik loguje się.
					else {
						serverResponse = server.post(
								ServerConnection.SERVER_LOGIN, json.toString());
					}
				}

			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return serverResponse;
	}


    @Override
	protected void onPostExecute(String serverResponse) {
		// Jeśli wykonywana była rejestracja konta, zwróć odpowiedź serwera
		// poprzez interface.
		if (!isUpdate) {
			delegate.processFinish(serverResponse);
		}
	}

	/**
	 * Przechowuje registration_id, wersję aplikacji i czas wygaśnięcia reg_id w
	 * aplikacji.
	 * 
	 * @param context
	 *            kontekst aplikacji
	 * @param regId
	 *            registration_id
	 */
	public void setRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);

		Log.v(TAG, "Saving regId on app version " + appVersion);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);

		long expirationTime = System.currentTimeMillis()
				+ REGISTRATION_EXPIRY_TIME_MS;
		Log.v(TAG, "Setting registration expiry time to "
				+ new Timestamp(expirationTime));
		editor.putLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, expirationTime);
		editor.commit();
	}

	/**
	 * @return Kod wersji aplikacji z {@code PackageManager}.
	 */
	public int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	public SharedPreferences getGCMPreferences(Context context) {
		return context.getSharedPreferences("GCM", Context.MODE_PRIVATE);
	}

	public String getSavedGCMRegId(Context context) {
		SharedPreferences prefs = getGCMPreferences(context);
		return prefs.getString(PROPERTY_REG_ID, "");
	}

	/**
	 * Usuwa registerId oraz nazwę użytkownika. Wysyła również do serwera prośbę
	 * o usunięcie z bazy danego regId
	 */
	public void deleteSavedGCMRegId(Context contex) {
		SharedPreferences prefs = getGCMPreferences(contex);
		prefs.edit().remove(PROPERTY_REG_ID).commit();
	}

	/**
	 * Sprawdza czy rejestracja nie wygasła. Jeśli wygasła należy aktualizować
	 * registrationID.
	 * 
	 * @return true jeśli rejestracja wygasła.
	 */
	public boolean isRegistrationExpired(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		// checks if the information is not stale
		long expirationTime = prefs.getLong(PROPERTY_ON_SERVER_EXPIRATION_TIME,
				-1);
		return System.currentTimeMillis() > expirationTime;
	}

}
