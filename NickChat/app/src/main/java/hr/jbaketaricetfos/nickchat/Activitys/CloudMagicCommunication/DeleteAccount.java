package hr.jbaketaricetfos.nickchat.Activitys.CloudMagicCommunication;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Josip on 08.05.2016..
 */
public class DeleteAccount extends StringRequest {

    private static final String URL_ADD_ME_ONLINE = "http://chatnick.azurewebsites.net/DeleteAccount.php";
    private Map<String, String> params;

    public DeleteAccount(String nick, Response.Listener<String> responseListener) {
        super(Method.POST, URL_ADD_ME_ONLINE, responseListener, null);
        params = new HashMap<>();
        params.put("nick", nick);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
