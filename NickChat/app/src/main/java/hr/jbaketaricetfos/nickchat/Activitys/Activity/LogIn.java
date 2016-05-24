package hr.jbaketaricetfos.nickchat.Activitys.Activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import hr.jbaketaricetfos.nickchat.Activitys.Containers.UserCredentials;
import hr.jbaketaricetfos.nickchat.Activitys.ExternalDBCommunication.LoginUser;
import hr.jbaketaricetfos.nickchat.Activitys.ExternalDBCommunication.RegisterUser;
import hr.jbaketaricetfos.nickchat.R;

public class LogIn extends Activity implements View.OnClickListener{

    private String email;
    private String password;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("JOSIP", "LogIN");
        if(checkIfLogged()){
            Intent intent = new Intent(LogIn.this, ChatArea.class);
            startActivity(intent);
        }
        setContentView(R.layout.activity_log_in);
        initializeUI();
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private void initializeUI() {
        this.etEmail = (EditText) findViewById(R.id.etEmail);
        this.etPassword = (EditText) findViewById(R.id.etPassword);
        this.btnLogin = (Button) findViewById(R.id.btnLogIn);
        this.btnRegister = (Button) findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnLogIn:
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();

                if(checkWhatUserEntered()){
                    btnLogin.setClickable(false);
                    //Start progress bar
                    final ProgressBar progressBar = (ProgressBar) findViewById(R.id.LoginProgress);
                    progressBar.setVisibility(RecyclerView.VISIBLE);
                    ObjectAnimator animation = ObjectAnimator.ofInt (progressBar, "progress", 0, 500);
                    animation.setDuration (5000); //in milliseconds
                    animation.setInterpolator (new DecelerateInterpolator());
                    animation.start ();

                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Stop progress
                            progressBar.clearAnimation();
                            progressBar.setVisibility(RecyclerView.GONE);

                            btnLogin.setClickable(true);

                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean error = jsonResponse.getBoolean("error");

                                if(error){
                                    String error_msg = jsonResponse.getString("error_msg");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LogIn.this);
                                    builder.setMessage("Error: "+ error_msg)
                                            .setNegativeButton("LogIn failed", null)
                                            .create()
                                            .show();
                                } else{

                                    String user_nick = jsonResponse.getString("user_nick");
                                    String user_email = jsonResponse.getString("user_email");
                                    String server_response = jsonResponse.getString("server_response");

                                    UserCredentials.getCredentials().setEmail(user_email);
                                    UserCredentials.getCredentials().setNick(user_nick);

                                    Toast.makeText(LogIn.this, server_response, Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(LogIn.this, ChatArea.class);
                                    intent.putExtra("nick", user_nick);
                                    intent.putExtra("email", user_email);
                                    saveDataInSharedPref(user_nick, user_email);
                                    startActivity(intent);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };//End of responseListener

                    LoginUser loginUser = new LoginUser(email, password, responseListener);
                    RequestQueue requestQueue = Volley.newRequestQueue(LogIn.this);
                    requestQueue.add(loginUser);

                } else {
                    Toast.makeText(this, "Enter credentials", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.btnRegister:
                Intent intent = new Intent(this, Register.class);
                startActivity(intent);
                break;
        }
    }

    private boolean checkWhatUserEntered() {
        String[] arrayOfFalse = new String[20];

        arrayOfFalse[0] = null;
        arrayOfFalse[1] = "";

        for(int counter = 0; counter < arrayOfFalse.length; counter++) {
            if(password.equals(arrayOfFalse[counter]))
                return false;
            if(email.equals(arrayOfFalse[counter]))
                return false;
        }

        return true;
    }

    private void saveDataInSharedPref(String nick, String email){
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("LoginPreff", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("user_email", email);
        editor.putString("user_nick", nick);
        editor.putBoolean("loged", true);
        editor.putBoolean("tokenAdded", false);
        editor.apply();
        Log.d("JOSIP", "Add data to Shared Pref");
    }

    private boolean checkIfLogged(){
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("LoginPreff", 0);
        boolean loged = sharedPref.getBoolean("loged", false);

        Log.d("JOSIP", "checkIfLogged. loged: "+ loged);
        if(loged){
            Log.d("JOSIP", "checkIfLogged. if(loged)");
            String nick = sharedPref.getString("user_nick", "");
            String email = sharedPref.getString("user_email", "");

            UserCredentials credentials = UserCredentials.getCredentials();
            credentials.setEmail(email);
            credentials.setNick(nick);


            Log.d("JOSIP", "User already loged");
        }
        else{
            Log.d("JOSIP", "User is not loged");
            UserCredentials credentials = UserCredentials.getCredentials();
            credentials.setTokenAdded(false);
        }
        return loged;
    }


}
