package pt.ulisboa.tecnico.cmov.locmess;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;

/**
 * Created by joao on 4/1/17.
 */

public class DataManager {
    private static DataManager ourInstance;

    private static String _currentUser;

    public static final String STORAGE_TAG = "DATA MANAGER";

    private static final String USER_CLASS = "User";
    private static final String USER_DATA = "USERDATA";
    private static final String SESSION_DATA = "SESSION";

    private static final String LAST_LOGIN_USER = "LastLoginUsername";

    private static final String SESSION_ID = "SessionId";
    private static final String USERNAME = "Username";

    public static DataManager getInstance() {
        if(null == ourInstance){
            ourInstance = new DataManager();
        }

        return ourInstance;
    }

    private DataManager() {
    }


    public void setUser(String username){
        _currentUser = username;
    }


    private SharedPreferences.Editor getEditor(Context context, String spFilename) {
        return context.getSharedPreferences(spFilename, Context.MODE_PRIVATE).edit();
    }

    private String getAttributeString(Context context, String spFilename, String attributeName)throws StorageException{
        try {
            String attribute = context.getSharedPreferences(spFilename, Context.MODE_PRIVATE).
                    getString(attributeName, "");

            return attribute;
        } catch ( ClassCastException exception) {
            throw new StorageException(exception);
        }
    }

    private long getAttributeLong(Context context, String spFilename, String attributeName) throws StorageException {
        try {
            long attribute = context.getSharedPreferences(spFilename, Context.MODE_PRIVATE).
                    getLong(attributeName, -1);

            return attribute;
        } catch ( ClassCastException exception) {
            throw new StorageException(exception);
        }
    }

    private void setAttribute(Context context, String spFilename, String attributeName, String value) {
        SharedPreferences.Editor editor = getEditor(context, spFilename);

        editor.putString(attributeName, value);
        editor.apply();
    }

    private void setAttribute(Context context, String spFilename, String attributeName, long value) {
        SharedPreferences.Editor editor = getEditor(context, spFilename);
        editor.putLong(attributeName, value);
        editor.apply();
    }

    public long getSessionId(Context context)throws StorageException{
        return getAttributeLong(context, _currentUser+SESSION_DATA, SESSION_ID);
    }

    public void setSessionId(Context context, long sessionId){
        setAttribute(context, _currentUser+SESSION_DATA, SESSION_ID, sessionId);
    }

    public String getUsername(Context context)throws StorageException{
        return getAttributeString(context, _currentUser+SESSION_DATA, USERNAME);
    }

    public void setUsername(Context context, String username){
        setAttribute(context, _currentUser+SESSION_DATA, USERNAME, username);
    }

    public String getLastLoggedInUsername(Context context)throws StorageException{
        return getAttributeString(context, LAST_LOGIN_USER, LAST_LOGIN_USER);
    }

    public void setLasLoggedInUsername(Context context, String username){
        setAttribute(context, LAST_LOGIN_USER, LAST_LOGIN_USER, username);
    }


}
