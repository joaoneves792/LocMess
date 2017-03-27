package LocMess;

import Crypto.Cryptography;
import Crypto.exceptions.FailedToHashException;
import LocMess.Domain.DeliverableMessage;
import LocMess.Domain.Message;
import LocMess.Domain.Profile;
import LocMess.Domain.Session;
import LocMess.Exceptions.AuthenticationException;
import LocMess.Exceptions.InsufficientArgumentsException;
import LocMess.Domain.Locations.GPSLocation;
import LocMess.Domain.Locations.Location;
import LocMess.Domain.Locations.WiFiLocation;
import LocMess.Responses.*;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Created by joao on 3/25/17.
 */
@RestController
public class RequestController implements ErrorController{

    private ConcurrentHashMap<Long, Session> _sessions = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Profile> _registeredUsers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Location> _locations = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> _interestKeys = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, Message> _messages = new ConcurrentHashMap<>();
    private AtomicLong _messageId = new AtomicLong(0);


    private Session getSession(long sessionId)throws AuthenticationException{
        if(_sessions.containsKey(sessionId)){
            return _sessions.get(sessionId);
        }
        throw new AuthenticationException("User is not logged in.");
    }

    @RequestMapping("/register")
    public Response register(@RequestParam(value="username")String username, @RequestParam(value="password") String password){
        try {
            if(_registeredUsers.containsKey(username))
                throw new AuthenticationException("User already exists.");

            String passwordToStore = Cryptography.encodeForStorage(Cryptography.hash(password.getBytes()));
            _registeredUsers.put(username, new Profile(username, passwordToStore));

            return new Response(true);

        }catch (FailedToHashException |
                AuthenticationException e){
            return new Response(e);
        }
    }

    @RequestMapping("/login")
    public Response login(@RequestParam(value="username") String username, @RequestParam(value="password") String password) {
        try{
            if(!_registeredUsers.containsKey(username))
                throw new AuthenticationException("User not registered.");


            String hashedPassword = Cryptography.encodeForStorage(Cryptography.hash(password.getBytes()));
            if(_registeredUsers.get(username).getPassword().equals(hashedPassword)){
                Cookie sessionCookie = new Cookie(true);
                Session session = new Session(sessionCookie.getSessionId(), _registeredUsers.get(username));
                _sessions.put(sessionCookie.getSessionId(), session);
                return sessionCookie;
            }else
                throw new AuthenticationException("Failed to login. Username and password don't match.");

        }catch (FailedToHashException |
                AuthenticationException e){
            return new Response(e);
        }
    }

    @RequestMapping("/logout")
    public Response logout(@RequestParam(value="id")long sessionId){
        if(_sessions.containsKey(sessionId)){
            _sessions.remove(sessionId);
        }
        return new Response(true);
    }

    @RequestMapping(value="/locations", method= RequestMethod.POST)
    public Response createLocation(@RequestBody Map<String, String> params){
        try{
            if(params.containsKey("id")) {
                getSession(new Long(params.get("id")));
            }else{
                throw new AuthenticationException();
            }

            String locationName;
            Location location;

            if(params.containsKey("name") &&
                    params.containsKey("latitude") &&
                    params.containsKey("longitude") &&
                    params.containsKey("radius")) {

                locationName = params.get("name");

                Double latitude = new Double(params.get("latitude"));
                Double longitude = new Double(params.get("longitude"));
                Double radius = new Double(params.get("radius"));

                location = new GPSLocation(locationName, latitude, longitude, radius);

            }else if(params.containsKey("name")){
                locationName = params.get("name");

                params.remove("name");
                params.remove("id");
                Collection<String> ssids = params.values();
                location = new WiFiLocation(locationName, ssids);

            }else{
                throw new InsufficientArgumentsException();
            }

            if (_locations.containsKey(locationName)) {
                _locations.remove(locationName);
            }
            _locations.put(locationName, location);
            return new Response(true, "Location successfully added.");

        }catch (InsufficientArgumentsException |
                AuthenticationException e){
            return new Response(e);
        }
    }

    @RequestMapping(value="/locations", method = RequestMethod.GET)
    public Response listLocations(@RequestParam(value = "id")long sessionId){
        try {
            getSession(sessionId);
            return new LocationsList(_locations.elements());
        }catch (AuthenticationException e){
            return new Response(e);
        }
    }

    @RequestMapping(value="/locations/{locationName}", method = RequestMethod.DELETE)
    public Response deleteLocation(@PathVariable("locationName") String locationName, @RequestParam(value="id")long sessionId){
        try{
            getSession(sessionId);
            if(_locations.containsKey(locationName)){
                _locations.remove(locationName);
            }
            return new Response(true);

        }catch (AuthenticationException e){
            return new Response(e);
        }
    }

    @RequestMapping(value = "/profiles/{id}", method = RequestMethod.GET)
    public Response listInterests(@PathVariable(value = "id")long id){
        try {
            Profile profile = getSession(id).getProfile();
            return new InterestsList(profile.getInterests());
        }catch (AuthenticationException e){
            return new Response(e);
        }
    }

    @RequestMapping(value="/profiles/{id}/{key}", method = RequestMethod.PUT)
    public Response addInterest(@PathVariable("id")long id, @PathVariable(value ="key")String key, @RequestParam(value="value")String value){
        try {
            Profile profile = getSession(id).getProfile();
            profile.addInterest(key, value);
            if(_interestKeys.containsKey(key)){
                int count = _interestKeys.get(key);
                _interestKeys.put(key, ++count);
            }else{
                _interestKeys.put(key, 1);
            }

            return new Response(true, "Successfully added a new interest.");

        }catch (AuthenticationException e){
            return new Response(e);
        }
    }

    @RequestMapping(value = "/profiles/{id}/{key}", method = RequestMethod.DELETE)
    public Response removeInterest(@PathVariable("id")long id, @PathVariable("key")String key){
        try {
            Profile profile = getSession(id).getProfile();
            if(profile.getInterests().containsKey(key)){
                profile.getInterests().remove(key);
                int count = _interestKeys.get(key);
                count--;
                if(count<=0){
                    _interestKeys.remove(key);
                }else{
                    _interestKeys.put(key, count);
                }
            }else{
                return new Response(false, "There is no interest with this key.");
            }
            return new Response(true, "Successfully deleted this interest.");

        }catch (AuthenticationException e){
            return new Response(e);
        }
    }

    @RequestMapping(value = "/interests", method = RequestMethod.GET)
    public Response listGlobalInterests(@RequestParam("id")long id){
        try {
            getSession(id);
            return new PossibleKeysList(_interestKeys.keys());

        }catch (AuthenticationException e){
            return new Response(e);
        }
    }

    @RequestMapping(value="/messages/{id}", method= RequestMethod.POST)
    public Response createMessage(@PathVariable("id")long id, @RequestBody Map<String, String> params){
        try{

            Profile profile = getSession(id).getProfile();

            Long messageId = _messageId.incrementAndGet();

            String desiredLocation = params.get("location");
            if(!_locations.containsKey(desiredLocation))
                return new Response(false, "Invalid location.");
            Location location = _locations.get(desiredLocation);
            params.remove("location");

            boolean whitelisted = (params.get("whitelist").equals("true"));
            params.remove("whitelist");

            DateFormat format = new SimpleDateFormat("HH:mm-MM/dd/yyyy", Locale.ENGLISH); //Example 14:30-12/24/2017
            Date startDate = format.parse(params.get("startDate"));
            Date endDate = format.parse(params.get("endDate"));
            params.remove("startDate");
            params.remove("endDate");

            String message = params.get("message");
            params.remove("message");

            Map<String, String> rules = null;

            if(params.values().size() > 0){
                rules = new HashMap<>(params);
            }

            Message m = new Message(messageId, profile, location, whitelisted, rules, startDate, endDate, message);

            _messages.put(messageId, m);
            return new Response(true, "Message successfully posted.");

        }catch (AuthenticationException |
                ParseException e){
            return new Response(e);
        }

    }

    @RequestMapping(value="/messages/{id}", method=RequestMethod.GET)
    public Response listUserMessages(@PathVariable("id")long id){
        try {
            Profile profile = getSession(id).getProfile();

            List<DeliverableMessage> messages = new LinkedList<>();
            for(Message m : _messages.values()){
                if(profile.equals(m.getSender()))
                    messages.add(new DeliverableMessage(m));
            }

            return new MessagesList(messages);

        }catch (AuthenticationException e){
            return new Response(e);
        }
    }

    @RequestMapping(value = "/messages/{id}/{messageId}", method = RequestMethod.DELETE)
    public Response deleteMessage(@PathVariable(value = "id")long id, @PathVariable(value = "messageId")long messageId){
        try {
            Profile profile = getSession(id).getProfile();

            if(!_messages.containsKey(messageId)){
                return new Response(false, "There is no such message on the server!");
            }
            Message m = _messages.get(messageId);
            if(!m.getSender().equals(profile)){
                return new Response(false, "The message that you are trying to delete doesn't belong to you!");
            }
            _messages.remove(messageId);

            return new Response(true, "Message successfully deleted.");

        }catch (AuthenticationException e){
            return new Response(e);
        }
    }

    @RequestMapping(value = "/messages", method = RequestMethod.GET)
    public Response getMessages(@RequestParam(value = "id")long id, @RequestParam(value = "latitude")double latitude, @RequestParam(value = "longitude")double longitude, @RequestParam(value = "ssid") List<String> ssids){
        try {
            Profile profile = getSession(id).getProfile();
            GPSLocation gps = new GPSLocation("", latitude, longitude, 0);
            WiFiLocation wifi = new WiFiLocation("", ssids);

            List<DeliverableMessage> messages = new LinkedList<>();

            for(Message m : _messages.values()){
                if(m.canDeliver(profile,gps, wifi)){
                    messages.add(new DeliverableMessage(m));
                }
            }

            return new MessagesList(messages);

        }catch (AuthenticationException e){
            return new Response(e);
        }
    }

    /*---------------------------------------------
     RESPONSE FOR BAD REQUESTS
     ---------------------------------------------*/
    @RequestMapping("/error")
    public Response error(){
        return new Response(false, "404 Resource not found!");
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    /*----------------------------------------------
    DEBUG METHODS
    ------------------------------------------------ */
    @RequestMapping("/test")
    public Response testSession(@RequestParam(value="id") long id){
        try {
            getSession(id);
            return new Response(true);
        }catch (AuthenticationException e){
            return new Response(e);
        }
    }

    @RequestMapping("debug-clear-all")
    public Response clearAll(){
        _sessions.clear();
        _registeredUsers.clear();
        _locations.clear();
        _interestKeys.clear();
        _messages.clear();
        return new Response(true);
    }

}
