package hr.jbaketaricetfos.nickchat.Activitys.GCM;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import hr.jbaketaricetfos.nickchat.Activitys.Containers.UserCredentials;
import hr.jbaketaricetfos.nickchat.R;

/**
 * Created by Josip on 05.05.2016..
 */
public class GCMRegistrationIntentService extends IntentService {
    public static final String REGISTRATION_SUCCESS = "RegistrationSuccess";
    public static final String REGISTRATION_ERROR = "RegistrationError";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public GCMRegistrationIntentService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //don't jam google, have some respect :D
        Context ctx = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        editor = prefs.edit();
        boolean isTokenAdded = prefs.getBoolean("tokenAdded", false);

        if(!UserCredentials.getCredentials().getTokenAdded()){
            registerGCM();
        }
    }

    private void registerGCM() {
        Intent registrationComplete = null;
        String token = null;
        try {
            InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.d("JOSIP", "token:" + token);
            //notify to UI that registration complete success
            registrationComplete = new Intent(REGISTRATION_SUCCESS);
            registrationComplete.putExtra("token", token);
            UserCredentials.getCredentials().setTokenAdded(true);
            editor.putBoolean("tokenAdded", true);
            editor.apply();
        } catch (Exception e) {
            Log.d("JOSIP", "Registration error");
            registrationComplete = new Intent(REGISTRATION_ERROR);
        }
        //Send broadcast
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
}
