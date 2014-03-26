package pl.eit.androideit.eit.chanel;


import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ServerConnection {
	    private static final int MAX_ATTEMPTS = 4;
	    long backoff = 1000L; // początkowy backoff w milisekundach.
	    JSONObject response = null;
	    /** Odpowiedź z serwera */
	    String result = null;

	    static final String SERVER_UPDATE_URL = "http://eit.besaba.com/update_regid.php"; 
	    static final String SERVER_LOG_OUT = "http://eit.besaba.com/log_out.php"; 
	    static final String SERVER_REGISTER = "http://eit.besaba.com/register.php"; 
	    static final String SERVER_LOGIN = "http://eit.besaba.com/login.php"; 
	    static final String SERVER_SEND_MESSAGE = "http://eit.besaba.com/sendMessage.php";
	    	    	 	     
	    /**
	     * Wysyła zapytanie POST do serwera.
	     *
	     * @param endpoint POST adres.
	     * @param json dane do wysłania.
	     * @throws java.io.IOException propagated from POST.
	     */
	    public String post(String endpoint, String json) throws IOException { 
	    	Log.d("sending data", "json: " + json);
	    	// Docelowy adres skryptu serwera.
	        URL url;
	        HttpURLConnection conn = null;
	        
	        try {
	            url = new URL(endpoint);
	        } catch (MalformedURLException e) {
	            throw new IllegalArgumentException("invalid url: " + endpoint);
	        }
	        
	        // Przesyłane dane zamieniane są na tablicę bajtów z kodowaniem UTF-8
	        byte[] bytes = json.getBytes("UTF-8");

	        
	        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
	            Log.d("", "Attempt #" + i + " to " + url);
	            
		        try {
		            // Ustawiam dane połączenia
		            conn = (HttpURLConnection) url.openConnection();
		            conn.setDoOutput(true);
		            conn.setUseCaches(false);
		            conn.setFixedLengthStreamingMode(bytes.length);
		            conn.setRequestMethod("POST");
		            conn.setRequestProperty("Content-Type", "application/json");
		            
		            // Wysyłam dane
		            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		            out.write(bytes);
		            out.flush();
		            out.close();
		            
		            // Sprawdzam jaka jest odpowiedź od serwera.
		            int status = conn.getResponseCode();
		            Log.d("server connection status ", " " + Integer.toString(status));
		            
		            // Niepoprawna odpowiedź, więc spróbuj jeszcze raz z zwiększonym backoffem.
		            if (status != 200) {
		              throw new IOException("Post failed with error code " + status);
		            }
		            // Poprawna odpowiedź
		            // Odczytaj wiadomość zwrotną
		            else{
		            	Log.d("Post successfull", "code: " + status);
		            	InputStream in = new BufferedInputStream(conn.getInputStream());
		            	BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		            	
		            	StringBuilder sb = new StringBuilder();
		            	String line = null;
		            	while( (line = reader.readLine()) != null){
		            		sb.append(line);
		            	}
		            	in.close();
		            	
		            	result = sb.toString();
		            	Log.d("response from server", "odpowiedz: " + result);
		            	
		            	conn.disconnect();
		            	break;
		            }
		        }
	         
		        catch(IOException e){
		        	e.printStackTrace();
		        	try {
	                    Log.d("", "Sleeping for " + backoff + " ms before retry");
	                    Thread.sleep(backoff);	                
	                } 
		        	catch (InterruptedException e1) {
	                    // Aktywność została zamknięta zanim skończył wykonywać się kod - wyjdź.
	                    Log.d("", "Thread interrupted: abort remaining retries!");
	                    Thread.currentThread().interrupt();
	                }
		        	backoff *= 2;
		        }
		        finally {
		            if (conn != null) {
		                conn.disconnect();
		            }
		        }
	        }
	        
	        // Zwróc odpowiedź od servera lub jeśli nie udało się z nim połączyć "serverProblem".
	        if(result == null){
	        	result = "serverProblem";
	        }

	        return result.trim();
	     	        
	      }
	    
	    
	    

}
