package hr.jbaketaricetfos.nickchat.Activitys.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hr.jbaketaricetfos.nickchat.Activitys.Adapter.EmojiSpinnerAdapter;
import hr.jbaketaricetfos.nickchat.Activitys.Adapter.MessageAdapter;
import hr.jbaketaricetfos.nickchat.Activitys.CloudMagicCommunication.AddMyToken;
import hr.jbaketaricetfos.nickchat.Activitys.CloudMagicCommunication.DeleteAccount;
import hr.jbaketaricetfos.nickchat.Activitys.CloudMagicCommunication.GetAllOnlineUsers;
import hr.jbaketaricetfos.nickchat.Activitys.CloudMagicCommunication.RemoveMeOnline;
import hr.jbaketaricetfos.nickchat.Activitys.CloudMagicCommunication.ShareMyOpinion;
import hr.jbaketaricetfos.nickchat.Activitys.Containers.EmojiContainer;
import hr.jbaketaricetfos.nickchat.Activitys.Containers.MessageContainer;
import hr.jbaketaricetfos.nickchat.Activitys.Containers.UserCredentials;
import hr.jbaketaricetfos.nickchat.Activitys.GCM.GCMPushReceiverService;
import hr.jbaketaricetfos.nickchat.Activitys.GCM.GCMRegistrationIntentService;
import hr.jbaketaricetfos.nickchat.Activitys.LocalDBCommunication.DatabaseAdapter;
import hr.jbaketaricetfos.nickchat.R;

public class ChatArea extends Activity {
    public static int isAppActive = 0;

    private UserCredentials credentials = UserCredentials.getCredentials();

    private Spinner spinnerEmoji;
    private EmojiSpinnerAdapter emojiSpinnerAdapter;
    private EmojiContainer getEmojis = EmojiContainer.getInstance();

    private EditText textInput;

    public ListView lvChat;
    public MessageAdapter messageAdapter;
    public ArrayList<MessageContainer> messageList;

    public ListView listView;
    public ArrayAdapter<String> adapter;
    public List<String> onlineUsers;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private BroadcastReceiver mPushBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Resize when keyboard appears
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setContentView(R.layout.activity_chat_area);
        //Initialize UI
        initializeUI();
        //Add all saved messages (if any)
        addMessagesFromDB();
        //Get token from google
        startReceiver();
        //Get messages from users
        startPushReceiver();
    }

    private void initializeUI() {
        this.textInput = (EditText) findViewById(R.id.etTextInput);

        spinnerEmoji = (Spinner) findViewById(R.id.spinnerEmoji);
        spinnerEmoji.setSelection(0, false);
        emojiSpinnerAdapter = new EmojiSpinnerAdapter(ChatArea.this, getEmojis.getIDs());
        spinnerEmoji.setAdapter(emojiSpinnerAdapter);


        //Chat list
        lvChat = (ListView) findViewById(R.id.lvChat);
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(ChatArea.this, messageList);
        lvChat.setAdapter(messageAdapter);

        messageAdapter.notifyDataSetChanged();

        //Online users
        listView = (ListView) findViewById(R.id.lvUsersOnline);
        onlineUsers = new ArrayList<>();
        adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.simple_adapter_online_users, onlineUsers);
        listView.setAdapter(adapter);

        spinnerEmoji.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int check = 0;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                check=check+1;
                if(check>1 && position != 0) {
                    shareMyOpinion(credentials.getNick(), Integer.toString(getEmojis.getIDs()[position]), "EMOJI");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void addMessagesFromDB(){
        DatabaseAdapter dbAdapter = new DatabaseAdapter(ChatArea.this);

        MessageContainer[] messageContainers = dbAdapter.getAllNotes();
        if(messageContainers != null){
            for(int counter = 0; counter < messageContainers.length; counter++){
                if(messageContainers[counter].getType() == null){
                    messageList.add(messageContainers[counter]);
                }
                //IF EMOJI
                else if(messageContainers[counter].getType().equals("EMOJI")){
                    Log.d("JOSIP", "addMessagesFromDB: EMOJI");
                    int pictureID = Integer.parseInt(messageContainers[counter].getMessage());
                    messageList.add(new MessageContainer(messageContainers[counter].getName(), pictureID));
                    messageAdapter.notifyDataSetChanged();
                }

            }
            messageAdapter.notifyDataSetChanged();
        }
        //First get all online users to the db
        getAllOnlineUsers();
    }

    private void startReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Check type of intent filter
                if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
                    //Registration success
                    String token = intent.getStringExtra("token");
                    //notify script that I am alive as well

                    Log.d("JOSIP", "ADD TOKEN. token: "+token);
                    Log.d("JOSIP", "ADD TOKEN. nick: "+ credentials.getNick());
                    addMyToken(token, credentials.getNick());

                } else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){
                    //Registration error
                    Toast.makeText(getApplicationContext(), "GCM registration error!!!", Toast.LENGTH_LONG).show();

                } else{
                     }
            }
        };

        //Check status of Google play service in device
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if(ConnectionResult.SUCCESS != resultCode) {
            //Check type of error
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                //So notification
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }
        } else {
            //Start service
            Intent intent = new Intent(this, GCMRegistrationIntentService.class);
            startService(intent);
        }
    }

    private void startPushReceiver(){
        mPushBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(GCMPushReceiverService.PUSH_KEY)){
                    String jsonMsg = intent.getStringExtra(GCMPushReceiverService.PUSH_GET_VALUE);
                    try {
                        JSONObject jsonObject = new JSONObject(jsonMsg);
                        String TYPE = jsonObject.getString("TYPE");
                        String user ="", msg;

                        switch(TYPE){
                            case "NEW_USER":
                                boolean fExists = false;
                                String newUser = jsonObject.getString("nick");
                                Log.d("JOSIP", "NEW MESSAGE. TYPE: NEW_USER. USER: " + user);
                                for(int i = 0; i< onlineUsers.size(); i++){
                                    if(onlineUsers.get(i).toString().equals(newUser.toString())){
                                        fExists = true;
                                        break;
                                    }
                                }

                                if(!fExists){
                                    onlineUsers.add(newUser);
                                    adapter.notifyDataSetChanged();
                                }
                                break;

                            case "USER_DISCONNECTED":
                                String userGoneRogue = jsonObject.getString("nick");
                                Log.d("JOSIP", "NEW MESSAGE. TYPE: USED_DISCONNECTED. USER: " + user);
                                int tempPlace = onlineUsers.indexOf(userGoneRogue);
                                onlineUsers.remove(tempPlace);
                                adapter.notifyDataSetChanged();
                                break;

                            case "NEW_MESSAGE":
                                user = jsonObject.getString("nick");
                                msg = jsonObject.getString("msg");
                                Log.d("JOSIP", "NEW MESSAGE. TYPE: NEW_MESSAGE. USER: " + user + ", MESSAGE: "+ msg);
                                messageList.add(new MessageContainer(user, msg));
                                messageAdapter.notifyDataSetChanged();
                                break;

                            case "EMOJI":
                                user = jsonObject.getString("nick");
                                msg = jsonObject.getString("msg");
                                Log.d("JOSIP", "NEW MESSAGE. TYPE: EMOJI. USER: " + user + ", MESSAGE: "+ msg);
                                int pictureID = Integer.parseInt(msg);

                                messageList.add(new MessageContainer(user, pictureID));
                                messageAdapter.notifyDataSetChanged();
                                break;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        isAppActive = 1;
        Log.d("JOSIP", "OnSTART");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("JOSIP", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
        LocalBroadcastManager.getInstance(this).registerReceiver(mPushBroadcastReceiver,
                new IntentFilter(GCMPushReceiverService.PUSH_KEY));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("JOSIP", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPushBroadcastReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("JOSIP", "ON STOP");
        isAppActive = 0;
    }

    @Override
    protected void onDestroy() {
        Log.d("JOSIP", "OnDESTROY");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //Don't go to Login. Go to homescreen
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public void sendMsg(View view) {
        String msg = textInput.getText().toString();
        textInput.setText("");
        Log.d("JOSIP", "sendMsg. msg: " + msg + ", nick: " + credentials.getNick());
        if(msg.isEmpty()){
            Toast.makeText(ChatArea.this, "Enter message", Toast.LENGTH_LONG).show();
        }
        else{
            shareMyOpinion(credentials.getNick(), msg);
        }
    }

    public void addMyToken(String token, String nick){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean error = jsonResponse.getBoolean("error");

                    if(error){
                        String error_msg = jsonResponse.getString("error_msg");
                        Toast.makeText(ChatArea.this, error_msg, Toast.LENGTH_LONG).show();
                        Log.d("JOSIP", "server response: "+ error_msg);
                    } else{
                        String serverResponse = jsonResponse.getString("msg");
                        Log.d("JOSIP", "server response: "+ serverResponse);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };//End of responseListener

        AddMyToken addMyToken = new AddMyToken(token, nick, responseListener);
        RequestQueue requestQueue = Volley.newRequestQueue(ChatArea.this);
        requestQueue.add(addMyToken);
    }

    public void removeMeOnline(String nick){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            //Move to login screen
            Intent intent = new Intent(ChatArea.this, LogIn.class);
            startActivity(intent);
            }
        };//End of responseListener

        RemoveMeOnline removeMeOnline = new RemoveMeOnline(nick, responseListener);
        RequestQueue requestQueue = Volley.newRequestQueue(ChatArea.this);
        requestQueue.add(removeMeOnline);
    }

    public void shareMyOpinion(String nick, String msg){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean error = jsonResponse.getBoolean("error");

                    if(error){
                        String error_msg = jsonResponse.getString("error_msg");
                        Toast.makeText(ChatArea.this, error_msg, Toast.LENGTH_LONG).show();
                        Log.d("JOSIP", "server response: "+ error_msg);
                    } else{
                        String serverResponse = jsonResponse.getString("msg");
                        Log.d("JOSIP", "server response: "+ serverResponse);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };//End of responseListener

        ShareMyOpinion shareMyOpinion = new ShareMyOpinion(nick, msg, responseListener);
        RequestQueue requestQueue = Volley.newRequestQueue(ChatArea.this);
        requestQueue.add(shareMyOpinion);
    }

    public void shareMyOpinion(String nick, String msg, String type){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean error = jsonResponse.getBoolean("error");

                    if(error){
                        String error_msg = jsonResponse.getString("error_msg");
                        Toast.makeText(ChatArea.this, error_msg, Toast.LENGTH_LONG).show();
                        Log.d("JOSIP", "server response: "+ error_msg);
                    } else{
                        String serverResponse = jsonResponse.getString("msg");
                        Log.d("JOSIP", "server response: "+ serverResponse);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };//End of responseListener

        ShareMyOpinion shareMyOpinion = new ShareMyOpinion(nick, msg, type, responseListener);
        RequestQueue requestQueue = Volley.newRequestQueue(ChatArea.this);
        requestQueue.add(shareMyOpinion);
    }

    public void getAllOnlineUsers(){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean error = jsonResponse.getBoolean("error");

                    if(error){
                        String error_msg = jsonResponse.getString("error_msg");
                        Toast.makeText(ChatArea.this, error_msg, Toast.LENGTH_LONG).show();
                        Log.d("JOSIP", "server response: "+ error_msg);
                    } else{
                        //read all users
                        int nUsers = jsonResponse.getInt("nUsers");
                        Log.d("JOSIP", "nUsers: " + nUsers);
                        //First clear existing
                        onlineUsers.clear();

                        for(int counter = 0; counter < nUsers; counter++){
                            String onUser = jsonResponse.getString(Integer.toString(counter));
                            Log.d("JOSIP", "LOOP ONLINE USERS: " + onUser);
                            onlineUsers.add(onUser);
                        }
                        adapter.notifyDataSetChanged();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };//End of responseListener

        GetAllOnlineUsers getAllOnlineUsers = new GetAllOnlineUsers(responseListener);
        RequestQueue requestQueue = Volley.newRequestQueue(ChatArea.this);
        requestQueue.add(getAllOnlineUsers);
    }

    public void deleteAccount(String nick){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean error = jsonResponse.getBoolean("error");

                    if(error){
                        String error_msg = jsonResponse.getString("error_msg");
                        Toast.makeText(ChatArea.this, error_msg, Toast.LENGTH_LONG).show();
                        Log.d("JOSIP", "server response: "+ error_msg);
                    } else{
                        String serverResponse = jsonResponse.getString("msg");
                        Log.d("JOSIP", "server response: "+ serverResponse);

                        //Get sharedPref and delete email and nick. change loged to false and get new token just in case
                        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("LoginPreff", 0);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.clear();
                        editor.apply();

                        //logout online too
                        removeMeOnline(credentials.getNick());
                        DatabaseAdapter databaseAdapter = new DatabaseAdapter(ChatArea.this);
                        databaseAdapter.deleteAllMessages();

                        //Move to login screen
                        Intent intent = new Intent(ChatArea.this, LogIn.class);
                        startActivity(intent);

                        Toast.makeText(ChatArea.this, serverResponse, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };//End of responseListener

        DeleteAccount deleteAccount = new DeleteAccount(nick, responseListener);
        RequestQueue requestQueue = Volley.newRequestQueue(ChatArea.this);
        requestQueue.add(deleteAccount);
    }

    public void openSettings(View view) {
        final String dialogTitle = "Settings";
        final Dialog dialog = new Dialog(ChatArea.this);

        dialog.setContentView(R.layout.dialog_settings);
        dialog.setCancelable(true);
        dialog.setTitle(dialogTitle);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

        final Button btnDeleteAllMessages = (Button) dialog.findViewById(R.id.settings_btnDeleteMessages);
        final Button btnDeleteAccount = (Button) dialog.findViewById(R.id.settings_btnDeleteAccount);
        final Button btnLogout = (Button) dialog.findViewById(R.id.settings_btnLogout);

        btnDeleteAllMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatArea.this);
                builder.setMessage("Are you sure you want to delete all messages?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseAdapter databaseAdapter = new DatabaseAdapter(ChatArea.this);
                                databaseAdapter.deleteAllMessages();
                                messageList.clear();
                                messageAdapter.notifyDataSetChanged();
                                Toast.makeText(ChatArea.this, "Messages deleted", Toast.LENGTH_LONG).show();
                            }
                        })
                        .create()
                        .show();
            }
        });//Delete All messages on click listener

       btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatArea.this);
                builder.setMessage("Are you sure you want logout? All saved messages will be deleted")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Get sharedPref and delete email and nick. change loged to false and get new token just in case
                                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("LoginPreff", 0);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.clear();
                                editor.apply();


                                DatabaseAdapter databaseAdapter = new DatabaseAdapter(ChatArea.this);
                                databaseAdapter.deleteAllMessages();
                                removeMeOnline(credentials.getNick());

                            }
                        })
                        .create()
                        .show();
            }
        });//Logout

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatArea.this);
                builder.setMessage("Are you sure you want Delete your account? All saved messages will be deleted")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //delete from db
                                deleteAccount(credentials.getNick());
                            }
                        })
                        .create()
                        .show();;
            }
        });//Delete account
    }

    public void refreshOnlineUsers(View view) {
        getAllOnlineUsers();
    }
}