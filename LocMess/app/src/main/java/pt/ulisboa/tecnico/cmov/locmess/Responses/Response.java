package pt.ulisboa.tecnico.cmov.locmess.Responses;

/**
 * Created by joao on 3/25/17.
 */
public class Response {
    private boolean successful;
    private String message;

    public Response(){

    }

    public boolean getSuccessful() {
        return successful;
    }

    public String getMessage(){
        return message;
    }
}
