package LocMess;

import Crypto.Cryptography;
import Crypto.exceptions.FailedToHashException;
import LocMess.Exceptions.AuthenticationException;
import LocMess.Exceptions.InsufficientArgumentsException;
import LocMess.Locations.GPSLocation;
import LocMess.Locations.Location;
import LocMess.Responses.Cookie;
import LocMess.Responses.LocationsList;
import LocMess.Responses.Response;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by joao on 3/25/17.
 */
@RestController
public class RequestController implements ErrorController{

    private ConcurrentHashMap<Long, Session> _sessions = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> _registeredUsers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Location> _locations = new ConcurrentHashMap<>();

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
            _registeredUsers.put(username, passwordToStore);

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
            if(_registeredUsers.get(username).equals(hashedPassword)){
                Cookie sessionCookie = new Cookie(true);
                Session session = new Session(sessionCookie.getSessionId(), username);
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

    /*@RequestMapping(value="/createLocation", method= RequestMethod.POST)
    public Response createLocation(@RequestParam(value="id")long sessionId,
                                   @RequestParam(value="name")String locationName,
                                   @RequestParam(value="latitude", required=false)Double latitude,
                                   @RequestParam(value="longitude", required=false)Double longitude,
                                   @RequestParam(value="radius", required=false)Double radius
        ){*/
    @RequestMapping(value="/createLocation", method= RequestMethod.POST)
    public Response createLocation(@RequestBody Map<String, String> params){
        try{
            if(params.containsKey("id")) {
                getSession(new Long(params.get("id")));
            }else{
                throw new AuthenticationException();
            }

            if(params.containsKey("name") &&
                    params.containsKey("latitude") &&
                    params.containsKey("longitude") &&
                    params.containsKey("radius")){

                String locationName = params.get("name");

                Double latitude = new Double(params.get("latitude"));
                Double longitude = new Double(params.get("longitude"));
                Double radius = new Double(params.get("radius"));

                Location location = new GPSLocation(locationName, latitude, longitude, radius);
                if(_locations.containsKey(locationName)){
                    _locations.remove(locationName);
                }
                _locations.put(locationName, location);
                return new Response(true, "Location successfully added.");
            }else{
                throw new InsufficientArgumentsException();
            }

        }catch (InsufficientArgumentsException |
                AuthenticationException e){
            return new Response(e);
        }
    }

    @RequestMapping(value="/listLocations", method = RequestMethod.GET)
    public Response listLocations(@RequestParam(value = "id")long sessionId){
        try {
            getSession(sessionId);
            return new LocationsList(_locations.elements());
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
        return new Response(true);
    }

}
