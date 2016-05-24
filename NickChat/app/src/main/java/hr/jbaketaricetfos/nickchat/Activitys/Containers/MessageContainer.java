package hr.jbaketaricetfos.nickchat.Activitys.Containers;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by Josip on 02.05.2016..
 */
public class MessageContainer{
    private String name;
    private String message;
    private Integer pictureID;
    private String type;



    public MessageContainer(String name){
        this.name = name;
    }

    public MessageContainer(String name, String message){
        this.name = name;
        this.message = message;
    }

    public MessageContainer(String name, String message, String type){
        this.name = name;
        this.message = message;
        this.type = type;
    }

    public MessageContainer(String name, int pictureID){
        this.name = name;
        this.pictureID = pictureID;
        Log.d("JOSIP", "MessageContainer. Constructor. pictureid: "+ Integer.toString(pictureID));
    }

    protected MessageContainer(Parcel in) {
        pictureID = in.readInt();
        name = in.readString();
        message = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getPictureID() {
        return pictureID;
    }

    public void setPictureID(Integer pictureID) {
        this.pictureID = pictureID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
