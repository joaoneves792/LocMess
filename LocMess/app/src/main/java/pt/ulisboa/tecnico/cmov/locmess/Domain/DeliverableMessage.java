package pt.ulisboa.tecnico.cmov.locmess.Domain;


/**
 * Created by joao on 3/25/17.
 */
public class DeliverableMessage {

    private static final String SEPARATOR = "!--!";
    private static final int ID = 0;
    private static final int HASH = 1;
    private static final int SENDER = 2;
    private static final int LOCATION = 3;
    private static final int MESSAGE = 4;
    private static final int PUBLICATION_DATE = 5;

    private static final String TERMINATION = "\n";

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

    public byte[] serialize(){
        String serialized = id + SEPARATOR + hash + SEPARATOR + sender + SEPARATOR +
                location + SEPARATOR + message + SEPARATOR + publicationDate + TERMINATION;
        return serialized.getBytes();
    }

    public static DeliverableMessage deserialize(byte[] serializedMessage){
        String[] splitMessage = (new String(serializedMessage)).split(SEPARATOR);
        return new DeliverableMessage(Long.parseLong(splitMessage[ID]), splitMessage[SENDER],
                splitMessage[LOCATION], splitMessage[MESSAGE], splitMessage[PUBLICATION_DATE],
                splitMessage[HASH]);
    }
}
