package hr.jbaketaricetfos.nickchat.Activitys.Containers;

/**
 * Created by Josip on 02.05.2016..
 */
public class UserCredentials {
    private static UserCredentials credentials = null;
    private String email;
    private String nick;
    private static boolean tokenAdded;

    private UserCredentials(){}

    public static UserCredentials getCredentials(){
        if(credentials == null){
            credentials = new UserCredentials();
        }
        return credentials;
    }

    public String getEmail(){
        return email;
    }
    public String getNick(){
        return nick;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setNick(String nick){
        this.nick = nick;
    }
    public static boolean getTokenAdded() {
        return tokenAdded;
    }
    public static void setTokenAdded(boolean logged) {
        tokenAdded = logged;
    }
}
