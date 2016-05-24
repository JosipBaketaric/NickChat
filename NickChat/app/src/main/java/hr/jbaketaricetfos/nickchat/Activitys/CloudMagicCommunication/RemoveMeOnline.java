package hr.jbaketaricetfos.nickchat.Activitys.CloudMagicCommunication;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Josip on 05.05.2016..
 */
public class RemoveMeOnline extends StringRequest {
    private static final String URL_REMOVE_ME_ONLINE = "http://chatnick.azurewebsites.net/RemoveMeOnline.php";
    private Map<String, String> params;

    public RemoveMeOnline(String nick, Response.Listener<String> responseListener) {
        super(Request.Method.POST, URL_REMOVE_ME_ONLINE, responseListener, null);
        params = new HashMap<>();
        params.put("nick", nick);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
