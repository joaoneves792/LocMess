package LocMess;

import Crypto.Cryptography;
import Crypto.exceptions.FailedToHashException;
import LocMess.Exceptions.AuthenticationException;
import LocMess.Responses.Cookie;
import LocMess.Responses.Response;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by joao on 3/25/17.
 */
@RestController
public class RequestController implements ErrorController{

    private ConcurrentHashMap<Long, Session> _sessions = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> _registeredUsers = new ConcurrentHashMap<>();

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
        if(_sessions.containsKey(id)){
            return new Response(true);
        }else{
            return new Response(false);
        }
    }

    @RequestMapping("debug-clear-all")
    public Response clearAll(){
        _sessions.clear();
        _registeredUsers.clear();
        return new Response(true);
    }

}
