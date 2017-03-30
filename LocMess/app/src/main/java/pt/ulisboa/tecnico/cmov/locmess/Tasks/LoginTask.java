package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

import pt.ulisboa.tecnico.cmov.locmess.HomeActivity;
import pt.ulisboa.tecnico.cmov.locmess.LoginActivity;
import pt.ulisboa.tecnico.cmov.locmess.Responses.Cookie;
import pt.ulisboa.tecnico.cmov.locmess.Responses.Response;

/**
 * Created by joao on 3/29/17.
 */

public class LoginTask extends RestTask{

    private String _username;
    private String _password;

    public LoginTask(Context appContext, String username, String password){
        super(appContext);
        _username = username;
        _password = password;
    }

    @Override
    protected String doInBackground(Void... params){
        try {
            return _rest.getForObject(_url+"/login?username=" + _username + "&password=" + _password, String.class);
        }catch (RestClientException e){
            Log.e("REST ERROR", e.getClass().toString()+" : "+e.getMessage());
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result){
        String toastMessage;
        boolean successful;
        long sessionId = 0;
        ObjectMapper mapper = new ObjectMapper();
        try {
            Cookie cookie = mapper.readValue(result, Cookie.class);
            sessionId = cookie.getSessionId();

            successful = cookie.getSuccessful();
            toastMessage = cookie.getMessage();

        }catch (IOException e){
            try{
                Response simpleResponse = mapper.readValue(result, Response.class);
                successful = simpleResponse.getSuccessful();
                toastMessage = simpleResponse.getMessage();
            }catch (IOException ex){
                successful = false;
                toastMessage = ex.getMessage();
            }
        }
        Toast.makeText(_context, toastMessage, Toast.LENGTH_SHORT).show();
        if(successful) {
            Intent intent = new Intent(_context, HomeActivity.class);
            intent.putExtra("SESSIONID", sessionId);
            _context.startActivity(intent);
        }
    }

}

