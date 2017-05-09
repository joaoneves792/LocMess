package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

import pt.ulisboa.tecnico.cmov.locmess.DataManager;
import pt.ulisboa.tecnico.cmov.locmess.HomeActivity;
import pt.ulisboa.tecnico.cmov.locmess.Responses.Cookie;
import pt.ulisboa.tecnico.cmov.locmess.Responses.Response;

/**
 * Created by joao on 3/29/17.
 */

public class LoginTask extends RestTask{

    private String _username;
    private String _password;

    private boolean _successful = false;
    private long _sessionId = 0;


    public LoginTask(Activity appContext, String username, String password){
        super(appContext);
        _username = username;
        _password = password;
    }

    @Override
    protected String doInBackground(Void... params){
        String result;
        try {
            result =  _rest.getForObject(_url+"/login?username=" + _username + "&password=" + _password, String.class);
        }catch (RestClientException e){
            Log.e("REST ERROR", e.getClass().toString()+" : "+e.getMessage());
            _successful = false;
            return e.getMessage();
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            Cookie cookie = mapper.readValue(result, Cookie.class);
            _sessionId = cookie.getSessionId();

            _successful = cookie.getSuccessful();
            return cookie.getMessage();

        }catch (IOException e){
            try{
                Response simpleResponse = mapper.readValue(result, Response.class);
                _successful = simpleResponse.getSuccessful();
                return simpleResponse.getMessage();
            }catch (IOException ex){
                _successful = false;
                return ex.getMessage();
            }
        }
    }

    @Override
    protected void onPostExecute(String result){
        Toast.makeText(_context, result, Toast.LENGTH_SHORT).show();
        if(_successful) {
            DataManager dm = DataManager.getInstance();
            dm.setUser(_username);
            dm.setLasLoggedInUsername(_context.getApplicationContext(), _username);
            dm.setSessionId(_context.getApplicationContext(), _sessionId);
            dm.setUsername(_context.getApplicationContext(), _username);

            Intent intent = new Intent(_context, HomeActivity.class);
            _context.startActivity(intent);
        }
    }

}

