package LocMess.Domain;


import LocMess.Domain.Locations.GPSLocation;
import LocMess.Domain.Locations.Location;
import LocMess.Domain.Locations.WiFiLocation;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * Created by joao on 3/25/17.
 */
public class Message {

    private Long _id;

    private String _hash;

    private Profile _sender;
    private Location _location;
    private boolean _whitelisted;
    private Map<String,String> _rules;
    private String _message;

    private Date _publicationDate;

    private Date _startDate;
    private Date _endDate;


    public Message(Long id, Profile sender, Location location, boolean whitelisted, Map<String, String> rules, Date startDate, Date endDate, String message){
        _sender = sender;
        _location = location;
        _whitelisted = whitelisted;
        _rules = rules;
        _startDate = startDate;
        _endDate = endDate;
        _message = message;
        _id = id;
        _publicationDate = new Date();

        byte[] concatMessage = new byte[sender.getUsername().getBytes().length+location.getName().getBytes().length+message.getBytes().length];
        System.arraycopy(sender.getUsername().getBytes(), 0, concatMessage, 0, sender.getUsername().getBytes().length);
        System.arraycopy(location.getName().getBytes(), 0, concatMessage, sender.getUsername().getBytes().length, location.getName().getBytes().length);
        System.arraycopy(message.getBytes(), 0, concatMessage, sender.getUsername().getBytes().length+location.getName().getBytes().length, message.getBytes().length);

        try {
            _hash = hash(concatMessage);
        }catch (NoSuchAlgorithmException e){
            //fail
        }

    }

    public boolean canDeliver(Profile user, GPSLocation GPSLocation, WiFiLocation wiFiLocation){

        Date currentDate = new Date();
        if(currentDate.before(_startDate) || currentDate.after(_endDate)){
            return false;
        }

        if(_location.getType().equals(GPSLocation.getType())){
            if(!_location.equals(GPSLocation))
                return false;
        }else{
            if(!_location.equals(wiFiLocation))
                return false;
        }

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
        }

        return true;
    }

    public Profile getSender() {
        return _sender;
    }

    public Location getLocation() {
        return _location;
    }

    public boolean isWhitelisted() {
        return _whitelisted;
    }

    public Map<String, String> getRules() {
        return _rules;
    }

    public String getMessage() {
        return _message;
    }

    public Date getStartDate() {
        return _startDate;
    }

    public Date getEndDate() {
        return _endDate;
    }

    public Long getId(){
        return _id;
    }

    public Date getPublicationDate(){
        return _publicationDate;
    }

    public String getHash(){
        return _hash;
    }

    private String hash(byte[] message) throws NoSuchAlgorithmException {
        MessageDigest md;
        md = MessageDigest.getInstance("MD5");
        md.update(message);
        byte[] messageDigest = md.digest();
        return Base64.getEncoder().encodeToString(messageDigest);
    }

}
