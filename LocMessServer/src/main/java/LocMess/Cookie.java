package LocMess;

import java.security.SecureRandom;

/**
 * Created by joao on 3/25/17.
 */
public class Cookie {
    private final long _sessionId;

    public Cookie(){
        _sessionId = new SecureRandom().nextLong();
    }

    public long getSessionId(){
        return _sessionId;
    }
}
