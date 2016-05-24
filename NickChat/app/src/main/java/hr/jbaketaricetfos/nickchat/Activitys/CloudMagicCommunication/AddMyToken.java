package hr.jbaketaricetfos.nickchat.Activitys.CloudMagicCommunication;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Josip on 04.05.2016..
 */
public class AddMyToken extends StringRequest{

    private static final String URL_ADD_ME_ONLINE = "http://chatnick.azurewebsites.net/SetToken.php";
    private Map<String, String> params;

    public AddMyToken(String token, String nick, Response.Listener<String> responseListener) {
        super(Method.POST, URL_ADD_ME_ONLINE, responseListener, null);
        params = new HashMap<>();
        params.put("nick", nick);
        params.put("token", token);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
