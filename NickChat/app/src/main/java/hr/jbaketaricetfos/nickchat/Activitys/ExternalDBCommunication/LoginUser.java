package hr.jbaketaricetfos.nickchat.Activitys.ExternalDBCommunication;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Josip on 01.05.2016..
 */
public class LoginUser extends StringRequest {

    private static final String URL_LOGIN = "http://chatnick.azurewebsites.net/login.php";
    private Map<String, String> params;

    public LoginUser(String email, String password, Response.Listener<String> responseListener){
        super(Method.POST, URL_LOGIN, responseListener, null);
        params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
