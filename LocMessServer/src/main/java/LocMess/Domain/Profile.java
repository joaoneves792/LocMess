package LocMess.Domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Created by joao on 3/25/17.
 */
public class Profile {
    private final String _username;
    private final String _password;

    private Map<String, String> _interests;

    public Profile(String user, String password){
        _username = user;
        _password = password;
        _interests = new HashMap<>();
    }

    public void addInterest(String key, String value){
        _interests.put(key, value);
    }

    public void removeInterest(String key){
        _interests.remove(key);
    }

    public Map<String,String> getInterests(){
        return _interests;
    }

    public String getUsername(){
        return _username;
    }

    public String getPassword(){
        return _password;
    }

    public boolean equals(Profile otherProfile){
        return (_username.equals(otherProfile.getUsername()) && _password.equals(otherProfile.getPassword()));
    }

}
