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
	
	// terminal color
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_ITALIC = "\u001B[3m";
	public static final String ANSI_RESET = "\u001B[0m";
	
	public String green(String str) { return ANSI_GREEN + str + ANSI_RESET; }
	public String cyan(String str) { return ANSI_CYAN + str + ANSI_RESET; }
	public String red(String str) { return ANSI_RED + str + ANSI_RESET; }
	public String purple(String str) { return ANSI_PURPLE + str + ANSI_RESET; }
	public String italic(String str) { return ANSI_ITALIC + str + ANSI_RESET; }
	
	public void printAttribute(String key, String value) { System.out.println("\t" + cyan(key) + " : " + italic(value)); }
	

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
            if(_registeredUsers.containsKey(username)) {
				System.out.println(red("\n[register]") + " username '" + italic(username) + "' already in use.");
                throw new AuthenticationException("User already exists.");
			}
			
            String passwordToStore = Cryptography.encodeForStorage(Cryptography.hash(password.getBytes()));
            _registeredUsers.put(username, new Profile(username, passwordToStore));
            
            System.out.println(green("\n[register]"));
			printAttribute("username", username);
			printAttribute("password", passwordToStore);
			
            return new Response(true);
            
        } catch (FailedToHashException | AuthenticationException e) {
            return new Response(e);
        }
    }

    @RequestMapping("/login")
    public Response login(@RequestParam(value="username") String username, @RequestParam(value="password") String password) {
        try{
            if(!_registeredUsers.containsKey(username)) {
				System.out.println(red("\n[login] ") + "User '" + italic(username) + "' not registered.");
                throw new AuthenticationException("User not registered.");
			}
			
            String hashedPassword = Cryptography.encodeForStorage(Cryptography.hash(password.getBytes()));
            if(_registeredUsers.get(username).getPassword().equals(hashedPassword)) {
                Cookie sessionCookie;
                do {
                    sessionCookie = new Cookie(true);
                } while (_sessions.containsKey(sessionCookie.getSessionId()));
                Session session = new Session(sessionCookie.getSessionId(), _registeredUsers.get(username));
                _sessions.put(sessionCookie.getSessionId(), session);
                
                System.out.println(green("\n[login]"));
				printAttribute("username", username);
				
                return sessionCookie;
                
            } else {
				System.out.println(red("\n[login] ") + "Failed to login" + italic(username) + ". Username and password don't match.");
                throw new AuthenticationException("Failed to login. Username and password don't match.");
			}
			
        }catch (FailedToHashException |
                AuthenticationException e){
            return new Response(e);
        }
    }

    @RequestMapping("/logout")
    public Response logout(@RequestParam(value="id")long sessionId){
        if(_sessions.containsKey(sessionId)){
            _sessions.remove(sessionId);
            System.out.println(green("\n[logout]"));
			printAttribute("session", ""+sessionId);
        }
        return new Response(true);
    }

    @RequestMapping(value="/locations", method= RequestMethod.POST)
    public Response createLocation(@RequestBody Map<String, String> params){
        try{
            if (params.containsKey("id")) {
                getSession(new Long(params.get("id")));
            } else {
				System.out.println(red("\n[new_location_GPS] ") + "invalid session.");
                throw new AuthenticationException();
            }

            String locationName;
            Location location;

            if (params.containsKey("name") &&
                    params.containsKey("latitude") &&
                    params.containsKey("longitude") &&
                    params.containsKey("radius")) {

                locationName = params.get("name");

                Double latitude = new Double(params.get("latitude"));
                Double longitude = new Double(params.get("longitude"));
                Double radius = new Double(params.get("radius"));

                location = new GPSLocation(locationName, latitude, longitude, radius);
                
                System.out.println(green("\n[new_location_GPS]"));
				printAttribute("session ID", ""+params.get("id"));
				printAttribute("name", locationName);
				printAttribute("latitude", ""+latitude);
				printAttribute("longitude", ""+longitude);
				printAttribute("latitude", ""+radius);

            } else if(params.containsKey("name")) {
                locationName = params.get("name");
                params.remove("name");
                
                String id = params.get("id");
                params.remove("id");
                
                Collection<String> ssids = params.values();
                location = new WiFiLocation(locationName, ssids);
                
				System.out.println(green("\n[new_location_WiFi]"));
				printAttribute("session ID", id);
				printAttribute("name", locationName);
				
				int i=0;
				for (String ssid : ssids) {
					printAttribute("SSID."+(i++), ssid);
				}
				
            } else {
				System.out.println(red("\n[new_location_GPS] ") + "insufficient arguments.");
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
            
			System.out.println(green("\n[list_locations]"));
			printAttribute("session ID", ""+sessionId);
            
            return new LocationsList(_locations.elements());
            
        } catch (AuthenticationException e) {
			System.out.println(red("\n[list_locations] ") + "invalid session.");
			return new Response(e);
        }
    }

    @RequestMapping(value="/locations/{locationName}", method = RequestMethod.DELETE)
    public Response deleteLocation(@PathVariable("locationName") String locationName, @RequestParam(value="id")long sessionId){
        try{
            getSession(sessionId);
            if(_locations.containsKey(locationName)){
                _locations.remove(locationName);
				
				System.out.println(green("\n[delete_location]"));
				printAttribute("session ID", ""+sessionId);
				printAttribute("name", locationName);
            }
            
            return new Response(true);
            
        } catch (AuthenticationException e) {
          	System.out.println(red("\n[delete_location] ") + "invalid session.");
			return new Response(e);
        }
    }

    @RequestMapping(value = "/profiles/{id}", method = RequestMethod.GET)
    public Response listInterests(@PathVariable(value = "id")long id){
        try {
            Profile profile = getSession(id).getProfile();
				
				System.out.println(green("\n[list_interests]"));
				printAttribute("session ID", ""+id);
            
            return new InterestsList(profile.getInterests());
            
        } catch (AuthenticationException e) {
			System.out.println(red("\n[list_interests] ") + "invalid session.");
			return new Response(e);
        }
    }

    @RequestMapping(value="/profiles/{id}/{key}", method = RequestMethod.PUT)
    public Response addInterest(@PathVariable("id")long id, @PathVariable(value ="key")String key, @RequestParam(value="value")String value){
        try {
            Profile profile = getSession(id).getProfile();
            
            if(!profile.getInterests().containsKey(key)) {
                if(_interestKeys.containsKey(key)) {
                    int count = _interestKeys.get(key);
                    _interestKeys.put(key, ++count);
                    
                } else {
                    _interestKeys.put(key, 1);
                }
            }
            
            System.out.println(green("\n[add_interest]"));
			printAttribute("session ID", ""+id);
			printAttribute("key", key);
			printAttribute("value", value);
            
            profile.addInterest(key, value);

            return new Response(true, "Successfully added a new interest.");

        } catch (AuthenticationException e) {
			System.out.println(red("\n[add_interest] ") + "invalid session.");
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
            } else {
				System.out.println(red("\n[delete_interest] ") + "invalid interest key.");
                return new Response(false, "There is no interest with this key.");
            }
            
			System.out.println(green("\n[delete_interest]"));
			printAttribute("session ID", ""+id);
			printAttribute("key", key);
			
            return new Response(true, "Successfully deleted this interest.");

        }catch (AuthenticationException e){
			System.out.println(red("\n[delete_interest] ") + "invalid session.");
			return new Response(e);
		}
    }

    @RequestMapping(value = "/interests", method = RequestMethod.GET)
    public Response listGlobalInterests(@RequestParam("id")long id){
        try {
            getSession(id);
            return new PossibleKeysList(_interestKeys.keys());

        }catch (AuthenticationException e){
			System.out.println(red("\n[list_global_intersts] ") + "invalid session.");
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
            
			System.out.println(green("\n[new_message]"));
			printAttribute("session ID", ""+id);
			printAttribute("poster", profile.getUsername());
			printAttribute("location", location.getName());
			printAttribute("whitelisted", ""+whitelisted);
			int i=0;
			if(rules != null) {
                for (Map.Entry<String, String> rule : rules.entrySet()) {
                    printAttribute("rule." + (i++), rule.toString());
                }
            }
			printAttribute("startDate", startDate.toString());
			printAttribute("endDate", endDate.toString());
			printAttribute("message", message.replaceAll("[\\t\\n\\r]"," "));
            
            return new Response(true, "Message successfully posted.");

        }catch (AuthenticationException |
                ParseException e){
			System.out.println(red("\n[new_message] ") + "invalid session.");
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
            
			System.out.println(green("\n[list_user_messages]"));
			printAttribute("session ID", ""+id);
			int i=0;
			for(Message m : _messages.values()){
				printAttribute("messageID."+(i++), ""+m.getId());
			}
			
            return new MessagesList(messages);

        }catch (AuthenticationException e){
			System.out.println(red("\n[list_user_messages] ") + "invalid session.");
            return new Response(e);
        }
    }

    @RequestMapping(value = "/messages/{id}/{messageId}", method = RequestMethod.DELETE)
    public Response deleteMessage(@PathVariable(value = "id")long id, @PathVariable(value = "messageId")long messageId) {
        try {
            Profile profile = getSession(id).getProfile();

            if(!_messages.containsKey(messageId)) {
                return new Response(false, "There is no such message on the server!");
            }
            Message m = _messages.get(messageId);
            if(!m.getSender().equals(profile)) {
                return new Response(false, "The message that you are trying to delete doesn't belong to you!");
            }
            _messages.remove(messageId);
            
			System.out.println(green("\n[delete_message]"));
			printAttribute("session ID", ""+id);
			printAttribute("messageID", ""+messageId);


            return new Response(true, "Message successfully deleted.");

        }catch (AuthenticationException e){
			System.out.println(red("\n[delete_message] ") + "invalid session.");
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
            
			System.out.println(green("\n[list_messages]"));
			printAttribute("sessionID", ""+id);
			int i=0;
			for(Message m : _messages.values()){
				printAttribute("messageID."+(i++), ""+m.getId());
			}

            return new MessagesList(messages);

        }catch (AuthenticationException e){
			System.out.println(red("\n[list_messages] ") + "invalid session.");
            return new Response(e);
        }
    }

    /*---------------------------------------------
     RESPONSE FOR BAD REQUESTS
     ---------------------------------------------*/
    @RequestMapping("/error")
    public Response error(){
		System.out.println(red("\n[404]"));
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
