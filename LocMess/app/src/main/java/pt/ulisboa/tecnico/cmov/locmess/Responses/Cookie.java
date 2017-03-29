package pt.ulisboa.tecnico.cmov.locmess.Responses;

import java.security.SecureRandom;

/**
 * Created by joao on 3/25/17.
 */
public class Cookie extends Response{
    private long sessionId;


    public Cookie(){

    }

    public long getSessionId() {
        return sessionId;
    }
}
