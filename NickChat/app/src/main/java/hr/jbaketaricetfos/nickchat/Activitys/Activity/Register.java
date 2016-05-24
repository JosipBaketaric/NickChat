package hr.jbaketaricetfos.nickchat.Activitys.Activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
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

import hr.jbaketaricetfos.nickchat.Activitys.ExternalDBCommunication.LoginUser;
import hr.jbaketaricetfos.nickchat.Activitys.ExternalDBCommunication.RegisterUser;
import hr.jbaketaricetfos.nickchat.R;

public class Register extends Activity implements View.OnClickListener {
    private String email;
    private String password;
    private String nick;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etNick;
    private Button btnRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeUI();
    }

    private void initializeUI() {
        etEmail = (EditText) findViewById(R.id.etRegisterEmail);
        etPassword = (EditText) findViewById(R.id.etRegisterPassword);
        etNick = (EditText) findViewById(R.id.etRegisterNickName);
        btnRegister = (Button) findViewById(R.id.btnRegisterRegister);

        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        nick = etNick.getText().toString();

        if(checkWhatUserEntered()) {
            btnRegister.setClickable(false);
            //Start progress bar
            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.RegisterProgress);
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

                    btnRegister.setClickable(true);

                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean error = jsonResponse.getBoolean("error");

                        if(error){
                            String error_msg = jsonResponse.getString("error_msg");
                            AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                            builder.setMessage("Error: "+ error_msg)
                                    .setNegativeButton("Register failed", null)
                                    .create()
                                    .show();
                        } else{
                            String serverResponse = jsonResponse.getString("server_response");
                            Toast.makeText(Register.this, serverResponse, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Register.this, LogIn.class);
                            startActivity(intent);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };//End of responseListener

            RegisterUser registerUser = new RegisterUser(email, password, nick, responseListener);
            RequestQueue requestQueue = Volley.newRequestQueue(Register.this);
            requestQueue.add(registerUser);

        } else {
            Toast.makeText(this, "Enter credentials", Toast.LENGTH_LONG).show();
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
            if(nick.equals(arrayOfFalse[counter]))
                return false;
        }

        return true;
    }


}
