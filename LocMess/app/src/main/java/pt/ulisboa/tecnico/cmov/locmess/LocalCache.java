package pt.ulisboa.tecnico.cmov.locmess;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pt.ulisboa.tecnico.cmov.locmess.Domain.DeliverableMessage;
import pt.ulisboa.tecnico.cmov.locmess.Domain.GPSLocation;
import pt.ulisboa.tecnico.cmov.locmess.Domain.Location;
import pt.ulisboa.tecnico.cmov.locmess.Domain.Profile;
import pt.ulisboa.tecnico.cmov.locmess.Domain.WiFiLocation;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;

/**
 * Created by joao on 4/1/17.
 */

public class LocalCache {
    private static LocalCache ourInstance;
    private static Context _context;

    private static final int CACHE_SIZE = 10;

    private static final String MESSAGE_CACHE = "MESSAGECACHE";
    private static final String SEPARATOR = "!-!"; //CAREFULL this cannot contain regex special characters!
    private static final String INVALID_ID = "!";

    private static final String SENDER_FIELD = "SENDER";
    private static final String MESSAGE_FIELD = "MESSAGE";
    private static final String LOCATION_FIELD = "LOCATION";
    private static final String DATE_FIELD = "DATE";
    private static final String HASH_FIELD = "HASH";
    private static final String ID_FIELD = "ID";

    private static final String LOCATION_CACHE = "LOCATIONCACHE";
    private static final String LOCATION = "LOCATION";

    private static final String TYPE_FIELD = "TYPE";
    private static final String SSIDS_FIELD = "SSIDS";
    private static final String LAT_FIELD = "LATITUDE";
    private static final String LONG_FIELD = "LONGITUDE";
    private static final String RADIUS_FIELD = "RADIUS";

    private static final String PROFILE = "PROFILE";
    private static final String KEY_VALUE_SEPARATOR = ",";

    private static List<Location> _locations;
    private static List<DeliverableMessage> _messages;

    public static LocalCache getInstance(Context context) {
        if(null == ourInstance){
            ourInstance = new LocalCache();
        }
        _context = context;
        return ourInstance;
    }

    public static LocalCache getInstance(){
        return getInstance(_context);
    }

    private LocalCache() {
        _messages = new LinkedList<>();
    }


    /*--------------MESSAGES----------------------------------------------------------------------*/

    /*Warning: Returns null if message not found!*/
    public DeliverableMessage getMessage(String hash){
        List<DeliverableMessage> cachedMessages = getMessages();
        for(DeliverableMessage m : cachedMessages){
            if(m.getHash().equals(hash)){
                return m;
            }
        }
        return null;
    }

    public List<DeliverableMessage> getMessages(){
        fetchMessagesFromStorage();
        return _messages;
    }

    public List<DeliverableMessage> storeMessages(List<DeliverableMessage> freshMessages){
        fetchMessagesFromStorage();

        for(DeliverableMessage m: _messages){
            for(DeliverableMessage freshM : freshMessages){
                if(freshM.equals(m)){
                    //Mark as old
                    freshM.setId(-1);
                }
            }
        }

        List<DeliverableMessage> newMessages = new LinkedList<DeliverableMessage>();
        for(DeliverableMessage m : freshMessages){
            if(m.getId() >= 0) {
                insertIntoStorage(m);
                newMessages.add(m);
            }
        }

        return newMessages;
    }

    private void insertIntoStorage(DeliverableMessage m){
        try {
            DataManager dm = DataManager.getInstance();

            /*Get the stored Hashes and trim them to CACHE_SIZE*/
            String messageHashes = dm.getUserAttributeString(_context, MESSAGE_CACHE);
            String[] hashes = messageHashes.split(SEPARATOR);
            if(hashes.length >= CACHE_SIZE){
                String REMOVAL_ID = hashes[0];
                hashes[0]  = INVALID_ID;  /*We allways remove the first and append to the last*/
                dm.removeUserAttribute(_context, REMOVAL_ID+ID_FIELD);
                dm.removeUserAttribute(_context, REMOVAL_ID+SENDER_FIELD);
                dm.removeUserAttribute(_context, REMOVAL_ID+LOCATION_FIELD);
                dm.removeUserAttribute(_context, REMOVAL_ID+MESSAGE_FIELD);
                dm.removeUserAttribute(_context, REMOVAL_ID+DATE_FIELD);
            }

            /*Build a new Ids Vector*/
            messageHashes = "";
            for(String hash : hashes){
                if(!hash.equals(INVALID_ID)){
                    messageHashes += hash + SEPARATOR;
                }
            }
            messageHashes += m.getHash();

            /*Store it*/
            String HASH = m.getHash();
            dm.setUserAttribute(_context, MESSAGE_CACHE, messageHashes);
            dm.setUserAttribute(_context, HASH+ID_FIELD, m.getId());
            dm.setUserAttribute(_context, HASH+SENDER_FIELD, m.getSender());
            dm.setUserAttribute(_context, HASH+LOCATION_FIELD, m.getLocation());
            dm.setUserAttribute(_context, HASH+MESSAGE_FIELD, m.getMessage());
            dm.setUserAttribute(_context, HASH+DATE_FIELD, m.getPublicationDate());



        }catch (StorageException e){
            //What should we do?
            Log.e(MESSAGE_CACHE, "Storage error!");
        }
    }

    private void fetchMessagesFromStorage(){
        try {
            _messages = new LinkedList<>();
            DataManager dm = DataManager.getInstance();
            String messageHashes = dm.getUserAttributeString(_context, MESSAGE_CACHE);
            String[] hashes = messageHashes.split(SEPARATOR);
            for(String hash: hashes){
                if(hash.equals("")){
                    continue;
                }
                Long id = dm.getUserAttributeLong(_context, hash+ID_FIELD);
                String sender = dm.getUserAttributeString(_context, hash+SENDER_FIELD);
                String location = dm.getUserAttributeString(_context, hash+LOCATION_FIELD);
                String message = dm.getUserAttributeString(_context, hash+MESSAGE_FIELD);
                String date = dm.getUserAttributeString(_context, hash+DATE_FIELD);
                DeliverableMessage m = new DeliverableMessage(id, sender, location, message, date, hash);
                _messages.add(m);
            }
        }catch (StorageException e){
            //What should we do?
            Log.e(MESSAGE_CACHE, "Storage error!");
        }

    }

    /*----------------------LOCATIONS-------------------------------------------------------------*/
    public List<Location> getLocations(){
        fetchLocationsFromStorage();
        return _locations;
    }

    public void insertIntoStorage(Location l){
        try{
            DataManager dm = DataManager.getInstance();

            /*Get the stored locations and trim them to CACHE_SIZE*/
            String locationNames = dm.getUserAttributeString(_context, LOCATION_CACHE);
            String[] locations = locationNames.split(SEPARATOR);
            if(locations.length >= CACHE_SIZE){
                String REMOVAL_LOCATION = locations[0];
                locations[0] = INVALID_ID;
                String type = dm.getUserAttributeString(_context, REMOVAL_LOCATION+TYPE_FIELD);
                dm.removeUserAttribute(_context, REMOVAL_LOCATION+TYPE_FIELD);
                if(type.equals(WiFiLocation.TYPE)){
                    dm.removeUserAttribute(_context, REMOVAL_LOCATION+SSIDS_FIELD);
                }else{
                    dm.removeUserAttribute(_context, REMOVAL_LOCATION+LAT_FIELD);
                    dm.removeUserAttribute(_context, REMOVAL_LOCATION+LONG_FIELD);
                    dm.removeUserAttribute(_context, REMOVAL_LOCATION+RADIUS_FIELD);
                }
            }
            locationNames = "";
            for(String locName: locations) {
                if (!locName.equals(INVALID_ID) && !locName.equals(l.getName())) {
                    locationNames += locName + SEPARATOR;
                }
            }
            locationNames += l.getName();

            /*Store it*/
            dm.setUserAttribute(_context, LOCATION_CACHE, locationNames);
            dm.setUserAttribute(_context, l.getName()+TYPE_FIELD, l.getType());
            if(l.getType().equals(WiFiLocation.TYPE)){
                WiFiLocation w = (WiFiLocation)l;
                String ssids = "";
                for(String ssid: w.getWifiIds()){
                    ssids += ssid + SEPARATOR;
                }
                ssids = ssids.replaceAll(SEPARATOR+"$", "");
                dm.setUserAttribute(_context, l.getName()+SSIDS_FIELD, ssids);
            }else{
                GPSLocation g = (GPSLocation)l;
                dm.setUserAttribute(_context, l.getName()+LAT_FIELD, (float)g.getLatitude());
                dm.setUserAttribute(_context, l.getName()+LONG_FIELD, (float)g.getLongitude());
                dm.setUserAttribute(_context, l.getName()+RADIUS_FIELD, (float)g.getRadius());
            }

        }catch (StorageException e){
            Log.e(LOCATION_CACHE, "Storage error!");
        }

    }

    private void fetchLocationsFromStorage(){
        try{
            _locations = new LinkedList<>();
            DataManager dm = DataManager.getInstance();
            String locationNames = dm.getUserAttributeString(_context, LOCATION_CACHE);
            String[] locations = locationNames.split(SEPARATOR);
            for(String locName: locations){
                if(locName.equals("")){
                    continue;
                }
                String type = dm.getUserAttributeString(_context, locName+TYPE_FIELD);

                if(type.equals(WiFiLocation.TYPE)){
                    String ssids = dm.getUserAttributeString(_context, locName+SSIDS_FIELD);
                    String[] splitSsids = ssids.split(SEPARATOR);
                    List<String> ssidsList = new ArrayList<String>();
                    for(String ssid: splitSsids){
                        if(ssid.equals(""))
                            continue;
                        ssidsList.add(ssid);
                    }
                    WiFiLocation wiFiLocation = new WiFiLocation(locName, ssidsList);
                    _locations.add(wiFiLocation);
                }else{
                    double longitude = dm.getUserAttributeFloat(_context, locName+LONG_FIELD);
                    double latitude = dm.getUserAttributeFloat(_context, locName+LAT_FIELD);
                    double radius = dm.getUserAttributeFloat(_context, locName+RADIUS_FIELD);

                    GPSLocation gpsLocation = new GPSLocation(locName, latitude, longitude, radius);
                    _locations.add(gpsLocation);
                }
            }
        }catch (StorageException e){
            Log.e(LOCATION_CACHE, "Storage error!");
        }
    }


    /*---------------------------PROFILES---------------------------------------------------------*/
    public void storeInterest(String key, String value){
        Profile profile = getStoredProfile();
        if(profile.getInterests().containsKey(key)){
            deleteInterest(key);
        }
        try {
            DataManager dm = DataManager.getInstance();

            String interests = dm.getUserAttributeString(_context, PROFILE);
            interests = interests + SEPARATOR + key + KEY_VALUE_SEPARATOR + value;
            dm.setUserAttribute(_context, PROFILE, interests);
        }catch (StorageException e){
            Log.e(PROFILE, "Storage error!");
        }
    }

    public void deleteInterest(String key){
        try {
            DataManager dm = DataManager.getInstance();
            String interests = dm.getUserAttributeString(_context, PROFILE);
            String[] keyValuePairs = interests.split(SEPARATOR);
            interests = "";
            for(String keyValue : keyValuePairs) {
                if(keyValue.equals(""))
                    continue;
                String[] splitInterest = keyValue.split(KEY_VALUE_SEPARATOR);
                if(splitInterest[0].equals("") || splitInterest[1].equals(""))
                    continue;
                if(!splitInterest[0].equals(key)){
                    interests = interests + SEPARATOR + splitInterest[0] + KEY_VALUE_SEPARATOR + splitInterest[1];
                }
            }
            dm.setUserAttribute(_context, PROFILE, interests);
        }catch (StorageException e){
            Log.e(PROFILE, "Storage error!");
        }
    }

    public Profile getStoredProfile(){
        Map<String, String> interestsMap = new HashMap<>();
        try {
            DataManager dm = DataManager.getInstance();
            String interests = dm.getUserAttributeString(_context, PROFILE);
            String[] keyValuePairs = interests.split(SEPARATOR);
            for(String keyValue : keyValuePairs){
                if(keyValue.equals(""))
                    continue;
                String[] splitInterest = keyValue.split(KEY_VALUE_SEPARATOR);
                if(splitInterest[0].equals("") || splitInterest[1].equals(""))
                    continue;
                interestsMap.put(splitInterest[0], splitInterest[1]);
            }
        }catch (StorageException e){
            Log.e(PROFILE, "Storage error!");
        }

        return new Profile(interestsMap);
    }

}
