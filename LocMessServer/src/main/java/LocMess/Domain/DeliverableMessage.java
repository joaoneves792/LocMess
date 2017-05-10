package LocMess.Domain;


import LocMess.Domain.Locations.GPSLocation;
import LocMess.Domain.Locations.Location;
import LocMess.Domain.Locations.WiFiLocation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by joao on 3/25/17.
 */
public class DeliverableMessage {

    private Long _id;

    private String _hash;

    private String _sender;
    private String _location;
    private String _message;
    private String _publicationDate;


    public DeliverableMessage(Message originalMessage){
        _sender = originalMessage.getSender().getUsername();
        _location = originalMessage.getLocation().getName();
        _message = originalMessage.getMessage();
        _id = originalMessage.getId();
        _hash = originalMessage.getHash();

        DateFormat df = new SimpleDateFormat("HH:mm MM/dd/yyyy");
        _publicationDate = df.format(originalMessage.getPublicationDate());
    }


    public String getSender() {
        return _sender;
    }

    public String getLocation() {
        return _location;
    }

    public String getMessage() {
        return _message;
    }

    public Long getId(){
        return _id;
    }

    public String getPublicationDate(){
        return _publicationDate;
    }

    public String getHash(){
        return _hash;
    }

}
