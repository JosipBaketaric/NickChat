package hr.jbaketaricetfos.nickchat.Activitys.ExternalDBCommunication;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import hr.jbaketaricetfos.nickchat.Activitys.Activity.Register;

/**
 * Created by Josip on 01.05.2016..
 */
public class RegisterUser extends StringRequest {

    private static final String URL_REGISTER = "http://chatnick.azurewebsites.net/register.php";
    private Map<String, String> params;

    public RegisterUser(String email, String password, String nick, Response.Listener<String> responseListener){
        super(Method.POST, URL_REGISTER, responseListener, null);
        params = new HashMap<>();
        params.put("nick", nick);
        params.put("email", email);
        params.put("password", password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
