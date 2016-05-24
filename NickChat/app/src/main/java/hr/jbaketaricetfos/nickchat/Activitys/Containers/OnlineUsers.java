package hr.jbaketaricetfos.nickchat.Activitys.Containers;

import java.util.List;

/**
 * Created by Josip on 02.05.2016..
 */
public class OnlineUsers {
    private String nick;

    public OnlineUsers(String nick){
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
