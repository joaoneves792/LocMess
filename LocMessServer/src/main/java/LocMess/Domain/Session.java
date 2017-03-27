package LocMess.Domain;

import java.security.SecureRandom;
import java.util.Date;
import java.util.concurrent.TimeUnit.*;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by joao on 3/25/17.
 */
public class Session {
    private Profile _profile;
    private long _sessionId;
    private Date _loginTimestamp;

    public Session(long sessionId, Profile profile){
        _profile = profile;
        _sessionId = sessionId;
        _loginTimestamp = new Date();
    }


    public long getSessionId(){
        return _sessionId;
    }

    public Profile getProfile(){
        return _profile;
    }

    public String getUsername(){
        return _profile.getUsername();
    }

    public boolean isExpired(){
        long maxDuration = MILLISECONDS.convert(6, HOURS);

        return ((new Date().getTime())-_loginTimestamp.getTime() > maxDuration);
    }
}
