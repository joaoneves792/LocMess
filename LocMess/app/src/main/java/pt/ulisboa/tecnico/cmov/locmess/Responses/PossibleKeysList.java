package pt.ulisboa.tecnico.cmov.locmess.Responses;

import java.util.List;

/**
 * Created by joao on 3/25/17.
 */
public class PossibleKeysList extends Response{
    private List<String> keys;

    public PossibleKeysList(){

    }

    public List<String> getKeys(){
        return keys;
    }

}
