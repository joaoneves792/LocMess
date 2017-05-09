package pt.ulisboa.tecnico.cmov.locmess.Responses;

/**
 * Created by joao on 3/25/17.
 */
//@JsonIgnoreProperties({ "successful", "message" })
public class Cookie extends Response{
    private long sessionId;

    public Cookie(){

    }

    public long getSessionId() {
        return sessionId;
    }
}
