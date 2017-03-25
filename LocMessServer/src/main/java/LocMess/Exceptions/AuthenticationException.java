package LocMess.Exceptions;

/**
 * Created by joao on 3/25/17.
 */
public class AuthenticationException extends Exception {
    public AuthenticationException(String m, Throwable throwable){
        super(m, throwable);
    }
    public AuthenticationException(String m){
        super(m);
    }
    public AuthenticationException(){
        super("Failed to authenticate");
    }
}
