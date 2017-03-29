package pt.ulisboa.tecnico.cmov.locmess.Responses;

import java.security.SecureRandom;

/**
 * Created by joao on 3/25/17.
 */
public class Cookie extends Response{
    private long _sessionId;

    public Cookie(boolean loginSuccessfull) {
        super(loginSuccessfull);
        if (loginSuccessfull) {
            while ((_sessionId = new SecureRandom().nextLong()) == 0) {
            }
        } else {
            _sessionId = 0;
        }
    }

    public long getSessionId() {
        return _sessionId;
    }
}
