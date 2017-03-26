package LocMess.Exceptions;

/**
 * Created by joao on 3/25/17.
 */
public class InsufficientArgumentsException extends Exception {
    public InsufficientArgumentsException(String m, Throwable throwable){
        super(m, throwable);
    }
    public InsufficientArgumentsException(String m){
        super(m);
    }
    public InsufficientArgumentsException(){
        super("The request was incomplete.");
    }
}
