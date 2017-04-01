package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

import pt.ulisboa.tecnico.cmov.locmess.DataManager;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.HomeActivity;
import pt.ulisboa.tecnico.cmov.locmess.Responses.Cookie;
import pt.ulisboa.tecnico.cmov.locmess.Responses.Response;

/**
 * Created by joao on 3/29/17.
 */

public class TestSessionTask extends RestTask{

    private boolean _successful = false;
    private long _sessionId = 0;


    public TestSessionTask(Activity appContext, long sessionId){
        super(appContext);
        _sessionId = sessionId;
    }

    @Override
    protected String doInBackground(Void... params){
        String result;
        try {
            result =  _rest.getForObject(_url+"/test?id=" + _sessionId, String.class);
        }catch (RestClientException e){
            Log.e("REST ERROR", e.getClass().toString()+" : "+e.getMessage());
            _successful = false;
            return e.getMessage();
        }

        ObjectMapper mapper = new ObjectMapper();
        try{
            Response simpleResponse = mapper.readValue(result, Response.class);
            _successful = simpleResponse.getSuccessful();
            return simpleResponse.getMessage();
        }catch (IOException ex){
            _successful = false;
            return ex.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result){
        if(_successful) {
            DataManager dm = DataManager.getInstance();
            try {
                String username = dm.getLastLoggedInUsername(_context.getApplicationContext());
                dm.setUser(username);
                dm.setSessionId(_context.getApplicationContext(), _sessionId);
                dm.setUsername(_context.getApplicationContext(), username);

                Intent intent = new Intent(_context, HomeActivity.class);
                _context.startActivity(intent);
            }catch (StorageException e){
                return; // If it failed (for storage reasons than do nothing and let the user log in again
            }
        }
    }

}

