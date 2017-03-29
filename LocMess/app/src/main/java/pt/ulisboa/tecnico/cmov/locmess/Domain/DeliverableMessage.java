package pt.ulisboa.tecnico.cmov.locmess.Domain;


import java.util.Date;
import java.util.Map;

/**
 * Created by joao on 3/25/17.
 */
public class DeliverableMessage {

    private Long _id;

    private String _sender;
    private String _location;
    private Map<String,String> _rules;
    private String _message;


    public String getSender() {
        return _sender;
    }

    public String getLocation() {
        return _location;
    }

    public Map<String, String> getRules() {
        return _rules;
    }

    public String getMessage() {
        return _message;
    }

    public Long getId(){
        return _id;
    }

}
