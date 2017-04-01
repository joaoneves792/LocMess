package pt.ulisboa.tecnico.cmov.locmess.Exceptions;

/**
 * Created by joao on 4/1/17.
 */

public class StorageException extends Exception {
    public StorageException(Throwable e){ super("Failed to retrive values from storage", e);}
    public StorageException(){ super("Failed to retrive values from storage");}
}
