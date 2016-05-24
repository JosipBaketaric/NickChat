package hr.jbaketaricetfos.nickchat.Activitys.CloudMagicCommunication;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Josip on 05.05.2016..
 */
public class ShareMyOpinion extends StringRequest{
    private static final String URL_SHARE_MY_OPINION = "http://chatnick.azurewebsites.net/ShareMyOpinion.php";
    private Map<String, String> params;

    public ShareMyOpinion(String nick, String message, Response.Listener<String> responseListener) {
        super(Method.POST, URL_SHARE_MY_OPINION, responseListener, null);
        params = new HashMap<>();
        params.put("nick", nick);
        params.put("msg", message);
    }

    public ShareMyOpinion(String nick, String message, String type, Response.Listener<String> responseListener) {
        super(Method.POST, URL_SHARE_MY_OPINION, responseListener, null);
        params = new HashMap<>();
        params.put("nick", nick);
        params.put("msg", message);
        params.put("type", type);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
