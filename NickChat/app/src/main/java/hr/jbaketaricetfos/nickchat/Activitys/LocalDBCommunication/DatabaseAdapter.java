package hr.jbaketaricetfos.nickchat.Activitys.LocalDBCommunication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import hr.jbaketaricetfos.nickchat.Activitys.Containers.MessageContainer;
import hr.jbaketaricetfos.nickchat.Activitys.Containers.OnlineUsers;

/**
 * Created by Josip on 06.05.2016..
 */
public class DatabaseAdapter {

    DatabaseHelper databaseHelper;

    public DatabaseAdapter(Context context)
    {
        databaseHelper = new DatabaseHelper(context);
    }

    public long addMessage(String nick, String message)
    {
        long id;
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(databaseHelper.NICK_NAME, nick);
        contentValues.put(databaseHelper.MESSAGE, message);
        id=db.insert(databaseHelper.TABLE_1_NAME, null, contentValues);
        db.close();
        return id;
    }

    public long addMessage(String nick, String message, String type)
    {
        long id;
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(databaseHelper.NICK_NAME, nick);
        contentValues.put(databaseHelper.MESSAGE, message);
        contentValues.put(databaseHelper.TYPE, type);
        id=db.insert(databaseHelper.TABLE_1_NAME, null, contentValues);
        db.close();
        return id;
    }

    public MessageContainer[] getAllNotes() {
        MessageContainer[] messageContainers;

        int numRows, index, counter = 0;
        String nick, message, type;
        String[] columns = {databaseHelper.NICK_NAME, databaseHelper.MESSAGE, databaseHelper.TYPE};
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.query(databaseHelper.TABLE_1_NAME, columns, null, null, null, null, null);
        numRows = cursor.getCount();
        messageContainers = new MessageContainer[numRows];
        if(numRows < 1){
            Log.d("JOSIP", "NO MESSAGES IN DB!");
            return null;
        }
        while (cursor.moveToNext()){
            index = cursor.getColumnIndex(databaseHelper.NICK_NAME);
            nick = cursor.getString(index);
            index = cursor.getColumnIndex(databaseHelper.MESSAGE);
            message = cursor.getString(index);
            index = cursor.getColumnIndex(databaseHelper.TYPE);
            type = cursor.getString(index);
            messageContainers[counter] = new MessageContainer(nick,message, type);
            counter++;
        }
        db.close();
        cursor.close();
        return messageContainers;
    }

    public void deleteAllMessages() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.execSQL("delete from " + databaseHelper.TABLE_1_NAME);
        db.close();
    }


//Don't use
    public void addUser(String nick){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(databaseHelper.NICK_NAME, nick);
        db.insert(databaseHelper.TABLE_2_NAME, null, contentValues);
        db.close();
    }

    public OnlineUsers[] getAllUsers(){
        OnlineUsers[] onlineUsers;
        String nick;

        int numRows, index, counter = 0;
        String[] columns = {databaseHelper.NICK_NAME};
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.query(databaseHelper.TABLE_2_NAME, columns, null, null, null, null, null);
        numRows = cursor.getCount();
        cursor.moveToFirst();
        onlineUsers = new OnlineUsers[numRows];
        if(numRows == 0){
            Log.d("JOSIP", "NO USERS IN DB!");
            return null;
        }
        else {
            while (cursor.moveToNext()) {
                index = cursor.getColumnIndex(databaseHelper.NICK_NAME);
                nick = cursor.getString(index);
                onlineUsers[counter] = new OnlineUsers(nick);
                counter++;
            }
            db.close();
            cursor.close();
            return onlineUsers;
        }
    }

    public void deleteUser(String nick){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.execSQL("delete from " + databaseHelper.TABLE_2_NAME + "WHERE Nick = '"+nick+"';");
        db.close();
    }

    //Helper inner class
    static class DatabaseHelper extends SQLiteOpenHelper
    {
        private Context context;

        private static final String DATABASE_NAME = "nickChatDB";
        private static final int DATABASE_VERSION = 5;
        private static final String TABLE_1_NAME = "messages";

        private static final String ID = "_id";
        private static final String NICK_NAME = "Nick";
        private static final String MESSAGE = "Message";
        private static final String TYPE = "type";

        private static final String ID_2 = "_id";
        private static final String TABLE_2_NAME = "users_online";

        private static final String CREATE_TABLE_1 =
                "CREATE TABLE " + TABLE_1_NAME + " (" +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        NICK_NAME + " VARCHAR(255), " +
                        MESSAGE + " VARCHAR(255), " +
                        TYPE + " VARCHAR(50));";

        private static final String CREATE_TABLE_2 =
                "CREATE TABLE " + TABLE_2_NAME + " (" +
                        ID_2 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        NICK_NAME + " VARCHAR(255));";


        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try{
                //Repeat Create on all tables
                db.execSQL(CREATE_TABLE_1);
                db.execSQL(CREATE_TABLE_2);
            }
            catch (SQLException e)
            {
                Log.d("JOSIP", "DatabaseAdapter. InnerClass. onCreate catch exception: "+ e.getMessage());
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_1_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_2_NAME);
            // Repeat DROP for all tables
            onCreate(db);
        }
    }//End of helper
}
