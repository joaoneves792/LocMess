package pt.ulisboa.tecnico.cmov.locmess.Domain;


import java.util.Map;

/**
 * Created by joao on 3/25/17.
 */
public class DeliverableMessage {

    private Long id;

    private String sender;
    private String location;
    private Map<String,String> rules;
    private String message;

    public DeliverableMessage(){

    }


    public String getSender() {
        return sender;
    }

    public String getLocation() {
        return location;
    }

    public Map<String, String> getRules() {
        return rules;
    }

    public String getMessage() {
        return message;
    }

    public Long getId(){
        return id;
    }

}
