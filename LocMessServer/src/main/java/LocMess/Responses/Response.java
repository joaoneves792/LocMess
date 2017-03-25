package LocMess.Responses;

/**
 * Created by joao on 3/25/17.
 */
public class Response {
    private final boolean _successful;
    private String _message;

    public Response(boolean successful) {
        _successful = successful;
        if(successful){
           _message = "Successful";
        }else{
            _message = "Request failed";
        }
    }

    public Response(Exception e){
        _successful = false;
        _message = e.getMessage();
    }

    public boolean getSuccessful() {
        return _successful;
    }

    public String getMessage(){
        return _message;
    }
}
