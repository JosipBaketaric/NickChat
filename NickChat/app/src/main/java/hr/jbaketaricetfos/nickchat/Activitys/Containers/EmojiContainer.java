package hr.jbaketaricetfos.nickchat.Activitys.Containers;

import hr.jbaketaricetfos.nickchat.Activitys.Adapter.EmojiSpinnerAdapter;
import hr.jbaketaricetfos.nickchat.R;

/**
 * Created by Josip on 09.05.2016..
 */
public class EmojiContainer {
    private static EmojiContainer instance = null;
    private static final Integer[] emojis = {R.drawable.emoji0, R.drawable.emoji1, R.drawable.emoji2, R.drawable.emoji3,
            R.drawable.emoji4, R.drawable.emoji5, R.drawable.emoji6, R.drawable.emoji7, R.drawable.emoji8, R.drawable.emoji9,
            R.drawable.emoji10, R.drawable.emoji11, R.drawable.emoji12,R.drawable.emoji13, R.drawable.emoji14, R.drawable.emoji15,
            R.drawable.emoji16, R.drawable.emoji17, R.drawable.emoji18, R.drawable.emoji19, R.drawable.emoji20, R.drawable.emoji21};

    private EmojiContainer(){}

    public static EmojiContainer getInstance(){
        if(instance==null){
            instance = new EmojiContainer();
        }
        return instance;
    }

    public Integer[] getIDs(){
        return emojis;
    }
}
