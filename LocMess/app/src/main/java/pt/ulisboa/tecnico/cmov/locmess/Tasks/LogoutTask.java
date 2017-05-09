package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

import pt.ulisboa.tecnico.cmov.locmess.LoginActivity;
import pt.ulisboa.tecnico.cmov.locmess.Responses.Response;

/**
 * Created by joao on 3/29/17.
 */

public class LogoutTask extends RestTask{

    private long _sessionId;

    private boolean _successful = false;

    public LogoutTask(Activity appContext, long sessionId){
        super(appContext);
        _sessionId = sessionId;
    }

    @Override
    protected String doInBackground(Void... params){
        String result;
        try {
            result = _rest.getForObject(_url+"/logout?id=" + _sessionId, String.class);

            ObjectMapper mapper = new ObjectMapper();
            Response response = mapper.readValue(result, Response.class);

            _successful = response.getSuccessful();
            return response.getMessage();

        }catch (RestClientException e){
            Log.e("REST ERROR", e.getClass().toString()+" : "+e.getMessage());
            _successful = false;
            return e.getMessage();
        }catch (IOException e){
            _successful = false;
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result){
        Toast.makeText(_context, result, Toast.LENGTH_SHORT).show();
        if(_successful) {
            Intent intent = new Intent(_context, LoginActivity.class);
            _context.startActivity(intent);
        }
    }

}

