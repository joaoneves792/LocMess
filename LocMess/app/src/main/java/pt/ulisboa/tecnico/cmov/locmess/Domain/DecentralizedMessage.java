package pt.ulisboa.tecnico.cmov.locmess.Domain;


import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pt.ulisboa.tecnico.cmov.locmess.GPSLocationListener;

/**
 * Created by joao on 3/25/17.
 */
public class DecentralizedMessage {
    public static final long DECENTRALIZED_ID = -2;


    private Long id;

    private String hash;

    private String sender;
    private String location;
    private List<String> ssids;
    private double latitude;
    private double longitude;
    private double radius;
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

    private String hash(byte[] message) throws NoSuchAlgorithmException {
        MessageDigest md;
        md = MessageDigest.getInstance("MD5");
        md.update(message);
        byte[] messageDigest = md.digest();
        return Base64.encodeToString(messageDigest, Base64.DEFAULT);
    }

    public boolean canDeliver(List<String> availableSSIDs, GPSLocationListener gps)throws ParseException{

        Date currentDate = new Date();
        DateFormat format = new SimpleDateFormat("HH:mm-MM/dd/yyyy", Locale.ENGLISH); //Example 14:30-12/24/2017
        Date startDate = format.parse(this.startDate);
        Date endDate = format.parse(this.endDate);

        if(currentDate.before(startDate) || currentDate.after(endDate)){
            return false;
        }

        boolean wiFiLocation = (ssids.size() > 0);

        if(!wiFiLocation){
            GPSLocation messageGPSLocation = new GPSLocation("bogus", latitude, longitude, radius);
            GPSLocation currentGPSLocation = new GPSLocation("bogus", gps.getLatitude(), gps.getLongitude(), 0);
            if(!messageGPSLocation.equals(currentGPSLocation))
                return false;
        }else{
            WiFiLocation currentWifiLocation = new WiFiLocation("bogus", availableSSIDs);
            WiFiLocation messageWifiLocation = new WiFiLocation("bogus", ssids);
            if(!messageWifiLocation.equals(currentWifiLocation))
                return false;
        }

        /* This must be decided on the receiving users device

        if(_whitelisted){
            if(null == _rules) //if there are no rules then deliver to nobody
                return false;

            for(String key : _rules.keySet()){
                if(!user.getInterests().containsKey(key))
                    return false;
                if(!user.getInterests().get(key).equals(_rules.get(key)))
                    return false;
            }

        }else{ //Blacklisted
            if(null == _rules) //if there are no rules then deliver to everybody
                return true;

            for(String key : _rules.keySet()){
                if(user.getInterests().containsKey(key)){
                    if(user.getInterests().get(key).equals(_rules.get(key)))
                        return false;
                }
            }
        }*/

        return true;
    }
}
