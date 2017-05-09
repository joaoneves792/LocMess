package pt.ulisboa.tecnico.cmov.locmess.Domain;

import java.util.Map;

/**
 * Created by joao on 5/9/17.
 */

public class Profile {
    Map<String, String> interests;

    public Profile(Map<String,String> interests){
        this.interests = interests;
    }

    public Map<String,String> getInterests(){
        return this.interests;
    }

}
