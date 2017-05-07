package pt.ulisboa.tecnico.cmov.locmess.Exceptions;

/**
 * Created by joao on 4/1/17.
 */

public class LocationException extends Exception {
    public LocationException(Throwable e){ super("Failed to retrive current Location", e);}
    public LocationException(){ super("Failed to retrive current Location");}
    public LocationException(String m){ super(m);}
}
