package hr.jbaketaricetfos.nickchat.Activitys.GCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import hr.jbaketaricetfos.nickchat.Activitys.Activity.ChatArea;
import hr.jbaketaricetfos.nickchat.Activitys.LocalDBCommunication.DatabaseAdapter;
import hr.jbaketaricetfos.nickchat.R;

/**
 * Created by Josip on 05.05.2016..
 */
public class GCMPushReceiverService extends GcmListenerService {
    public final static String PUSH_KEY = "PUSH_MSG";
    public final static String PUSH_GET_VALUE = "PUSH_VALUE";
    public String showMsg = null;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        //First add to the DB then send notification
        Log.d("JOSIP", "GCMPushReceiverService: msg got here: "+message);
        addMessageToTheDB(message);
        //Notify only if activity is not active

        sendResult(message);
    }

    private void sendNotification(String message) {
        Intent intent = new Intent(this, ChatArea.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;//req code
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        //Setup notification
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), sound);
        r.play();
        //Build notification
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notificon)
                .setContentText("New message from: ")
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noBuilder.build()); //0 = ID of notification

        //Send data to ChatArea by Broadcast
        Log.d("JOSIP", "GCMPushReceiverService: " + message);

    }

    public void sendResult(String message) {
        Intent intent = new Intent(PUSH_KEY);
        if(message != null)
            intent.putExtra(PUSH_GET_VALUE, message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void addMessageToTheDB(String message){
        DatabaseAdapter dbAdapter = new DatabaseAdapter(getApplicationContext());
        String user ="";
        String msg;
        try {
            JSONObject jsonObject = new JSONObject(message);
            String TYPE = jsonObject.getString("TYPE");
            switch (TYPE){
                case "NEW_MESSAGE":
                    user = jsonObject.getString("nick");
                    msg = jsonObject.getString("msg");

                    dbAdapter.addMessage(user, msg);
                    Log.d("JOSIP", "GCMPushReceiverService. ADDED MESSAGE IN DB.");

                    showMsg = user;
                    if(ChatArea.isAppActive == 0 && showMsg != null){
                        sendNotification(showMsg);
                    }
                    break;

                case "EMOJI":
                    user = jsonObject.getString("nick");
                    msg = jsonObject.getString("msg");

                    dbAdapter.addMessage(user, msg, "EMOJI");
                    Log.d("JOSIP", "GCMPushReceiverService. ADDED EMOJI IN DB.");
                    showMsg = user;
                    if(ChatArea.isAppActive == 0 && showMsg != null){
                        sendNotification(showMsg);
                    }
                    break;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

