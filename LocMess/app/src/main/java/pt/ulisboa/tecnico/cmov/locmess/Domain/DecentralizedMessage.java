package pt.ulisboa.tecnico.cmov.locmess.Domain;


import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by joao on 3/25/17.
 */
public class DecentralizedMessage {
    public static final long DECENTRALIZED_ID = -2;


    private Long id;

    private String hash;

    private String sender;
    private String location;
    private Map<String,String> rules;
    private String message;

    private String publicationDate;
    private String startDate;
    private String endDate;

    public DecentralizedMessage(){

    }

    public DecentralizedMessage(String sender, String location, String message, String startDate, String endDate){
        this.id = DECENTRALIZED_ID;
        this.sender = sender;
        this.location = location;
        this.message = message;
        this.startDate = startDate;
        this.endDate = endDate;

        DateFormat df = new SimpleDateFormat("HH:mm MM/dd/yyyy");
        this.publicationDate = df.format(new Date());

        byte[] concatMessage = new byte[sender.getBytes().length+location.getBytes().length+message.getBytes().length];
        System.arraycopy(sender.getBytes(), 0, concatMessage, 0, sender.getBytes().length);
        System.arraycopy(location.getBytes(), 0, concatMessage, sender.getBytes().length, location.getBytes().length);
        System.arraycopy(message.getBytes(), 0, concatMessage, sender.getBytes().length+location.getBytes().length, message.getBytes().length);

        try {
            this.hash = hash(concatMessage);
        }catch (NoSuchAlgorithmException e){
            //fail
        }
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

    public void setId(long id){
        this.id = id;
    }

    public String getPublicationDate(){
        return publicationDate;
    }

    public static String hash(byte[] message) throws NoSuchAlgorithmException {
        MessageDigest md;
        md = MessageDigest.getInstance("SHA-256");
        md.update(message);
        byte[] messageDigest = md.digest();
        return Base64.encodeToString(messageDigest, Base64.DEFAULT);
    }
}
