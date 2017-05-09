package pt.ulisboa.tecnico.cmov.locmess.Domain;


import java.util.Map;

/**
 * Created by joao on 3/25/17.
 */
public class DeliverableMessage {

    private Long id;

    private String hash;

    private String sender;
    private String location;
    private String message;

    private String publicationDate;

    public DeliverableMessage(){

    }

    public DeliverableMessage(long id, String sender, String location, String message, String publicationDate, String hash){
        this.id = id;
        this.sender = sender;
        this.location = location;
        this.message = message;
        this.publicationDate = publicationDate;
        this.hash = hash;
    }


    public String getSender() {
        return sender;
    }

    public String getLocation() {
        return location;
    }

    public String getMessage() {
        return message;
    }

    public Long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getPublicationDate(){
        return publicationDate;
    }

    public String getHash(){
        return hash;
    }

    public boolean equals(DeliverableMessage otherMessage){
        return (this.hash.equals(otherMessage.getHash()));
    }

}
