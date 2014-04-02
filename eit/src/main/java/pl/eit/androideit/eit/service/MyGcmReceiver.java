package pl.eit.androideit.eit.service;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import pl.eit.androideit.eit.R;
import pl.eit.androideit.eit.chanel.SingleChannel;

public class MyGcmReceiver extends BroadcastReceiver{
	private static final String TAG = "MyGcmReceiver";
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Context ctx;
    String message, userName;
    long messageDate;
    String channel;
    GoogleCloudMessaging gcm;
	
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("GCM", "ODEBRANO POWIADOMIENIE");
    	gcm = GoogleCloudMessaging.getInstance(context);
        String action = intent.getAction();
        if (action.equals("com.google.android.c2dm.intent.RECEIVE")) {
            handleMessage(intent);
        }
    }
    
    /** Odczytuje wiadomość z GCM **/
    public void handleMessage(Intent intent){
        String messageType = gcm.getMessageType(intent);
        
        if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
        	showNotification("Send error: " /*+ intent.getExtras().toString()*/);
        } 
        else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
        	showNotification("Deleted messages on server: " /*+ intent.getExtras().toString()*/);
        } 
        else {
        	String data = intent.getExtras().getString("message");
        	Log.d("Received GCM Notification", "with message: " + data);
			
        	showNotification(data);
        }
        
        setResultCode(Activity.RESULT_OK);
    }
    
    /** Wyświetlenie wiadomości z GCM w notyfikacji. */
    private void showNotification(String msg) {
        mNotificationManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        // Intencja z odebraną wiadomością, która po kliknięciu notyfikacji
        // zostanie wyświetlona w MainActivity.
        Intent intent = new Intent(ctx, SingleChannel.class);
        intent.putExtra("gcmNotification", msg);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx)
			.setSmallIcon(R.drawable.ic_menu_add)
			.setContentTitle("Nowa wiadomość!")
			.setStyle(new NotificationCompat.BigTextStyle())
			.setContentText(msg)
			.setAutoCancel(true)
			.setContentIntent(contentIntent);
        
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
