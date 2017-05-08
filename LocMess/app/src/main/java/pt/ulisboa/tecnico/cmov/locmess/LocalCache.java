package pt.ulisboa.tecnico.cmov.locmess;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.Domain.DeliverableMessage;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;

/**
 * Created by joao on 4/1/17.
 */

public class LocalCache {
    private static LocalCache ourInstance;
    private static Context _context;

    private static final int CACHE_SIZE = 10;

    private static final String MESSAGE_CACHE = "MESSAGECACHE";
    private static final String SEPARATOR = "-";
    private static final String INVALID_ID = "!";

    private static final String SENDER_FIELD = "SENDER";
    private static final String MESSAGE_FIELD = "MESSAGE";
    private static final String LOCATION_FIELD = "LOCATION";
    private static final String DATE_FIELD = "DATE";

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


    /*Warning: Returns null if message not found!*/
    public DeliverableMessage getMessage(long id){
        List<DeliverableMessage> cachedMessages = getMessages();
        for(DeliverableMessage m : cachedMessages){
            if(m.getId().equals(id)){
                return m;
            }
        }
        return null;
    }

    public List<DeliverableMessage> getMessages(){
        fetchFromStorage();
        return _messages;
    }

    public List<DeliverableMessage> storeMessages(List<DeliverableMessage> freshMessages){
        fetchFromStorage();


        for(DeliverableMessage m: _messages){
            for(DeliverableMessage freshM : freshMessages){
                if(freshM.getId().equals(m.getId())){
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

            /*Get the stored Ids and trim them to CACHE_SIZE*/
            String messageIds = dm.getUserAttributeString(_context, MESSAGE_CACHE);
            String[] ids = messageIds.split(SEPARATOR);
            if(ids.length >= CACHE_SIZE){
                String REMOVAL_ID = ids[0];
                ids[0]  = INVALID_ID;  /*We allways remove the first and append to the last*/
                dm.removeUserAttribute(_context, REMOVAL_ID+SENDER_FIELD);
                dm.removeUserAttribute(_context, REMOVAL_ID+LOCATION_FIELD);
                dm.removeUserAttribute(_context, REMOVAL_ID+MESSAGE_FIELD);
                dm.removeUserAttribute(_context, REMOVAL_ID+DATE_FIELD);
            }

            /*Build a new Ids Vector*/
            messageIds = "";
            for(String id : ids){
                if(!id.equals(INVALID_ID)){
                    messageIds += id + SEPARATOR;
                }
            }
            messageIds += m.getId();

            /*Store it*/
            String ID = m.getId().toString();
            dm.setUserAttribute(_context, MESSAGE_CACHE, messageIds);
            dm.setUserAttribute(_context, ID+SENDER_FIELD, m.getSender());
            dm.setUserAttribute(_context, ID+LOCATION_FIELD, m.getLocation());
            dm.setUserAttribute(_context, ID+MESSAGE_FIELD, m.getMessage());
            dm.setUserAttribute(_context, ID+DATE_FIELD, m.getPublicationDate());



        }catch (StorageException e){
            //What should we do?
            Log.e(MESSAGE_CACHE, "Storage error!");
        }
    }

    private void fetchFromStorage(){
        try {
            _messages = new LinkedList<>();
            DataManager dm = DataManager.getInstance();
            String messageIds = dm.getUserAttributeString(_context, MESSAGE_CACHE);
            String[] ids = messageIds.split(SEPARATOR);
            for(String id: ids){
                if(id.equals("")){
                    continue;
                }
                String sender = dm.getUserAttributeString(_context, id+SENDER_FIELD);
                String location = dm.getUserAttributeString(_context, id+LOCATION_FIELD);
                String message = dm.getUserAttributeString(_context, id+MESSAGE_FIELD);
                String date = dm.getUserAttributeString(_context, id+DATE_FIELD);
                DeliverableMessage m = new DeliverableMessage(Long.parseLong(id), sender, location, message, date);
                _messages.add(m);
            }
        }catch (StorageException e){
            //What should we do?
            Log.e(MESSAGE_CACHE, "Storage error!");
        }

    }


}
