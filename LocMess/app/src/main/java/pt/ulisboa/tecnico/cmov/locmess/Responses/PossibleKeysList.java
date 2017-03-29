package pt.ulisboa.tecnico.cmov.locmess.Responses;

import java.util.Enumeration;
import java.util.Map;

/**
 * Created by joao on 3/25/17.
 */
public class PossibleKeysList extends Response{
    private Enumeration<String> keys;

    public PossibleKeysList(){

    }

    public Enumeration<String> getKeys(){
        return keys;
    }

}
