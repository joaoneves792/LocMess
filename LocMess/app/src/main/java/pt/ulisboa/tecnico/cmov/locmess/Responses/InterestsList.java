package pt.ulisboa.tecnico.cmov.locmess.Responses;

import java.util.Map;

/**
 * Created by joao on 3/25/17.
 */
public class InterestsList extends Response{
    private Map<String, String> interests;


    public InterestsList(){

    }

    public Map<String, String> getInterests(){
        return interests;
    }

}
