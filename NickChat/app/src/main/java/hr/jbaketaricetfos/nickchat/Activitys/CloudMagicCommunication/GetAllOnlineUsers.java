package hr.jbaketaricetfos.nickchat.Activitys.CloudMagicCommunication;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Josip on 07.05.2016..
 */
public class GetAllOnlineUsers extends StringRequest {

    private static final String URL_GET_ALL_USERS = "http://chatnick.azurewebsites.net/GetAllOnlineUsers.php";
    private Map<String, String> params;

    public GetAllOnlineUsers(Response.Listener<String> responseListener) {
        super(Method.POST, URL_GET_ALL_USERS, responseListener, null);
        params = new HashMap<>();
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}