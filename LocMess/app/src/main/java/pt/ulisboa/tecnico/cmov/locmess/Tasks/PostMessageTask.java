package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import pt.ulisboa.tecnico.cmov.locmess.Domain.GPSLocation;
import pt.ulisboa.tecnico.cmov.locmess.Domain.Location;
import pt.ulisboa.tecnico.cmov.locmess.Domain.WiFiLocation;
import pt.ulisboa.tecnico.cmov.locmess.Responses.Response;

/**
 * Created by joao on 3/29/17.
 */

public class PostMessageTask extends RestTask {

    private long _sessionId;
    private String _location;
    private Map<String, String> _rules;
    private boolean _whitelist;
    private String _startDate;
    private String _endDate;
    private String _message;

    private boolean _successful;

    public PostMessageTask(Activity appContext, long sessionId, String location, Map<String, String> rules, boolean whitelist, String startDate, String endDate, String message){
        super(appContext);
        _sessionId = sessionId;
        _location = location;
        _rules = rules;
        _whitelist = whitelist;
        _startDate = startDate;
        _endDate = endDate;
        _message = message;
    }

    @Override
    protected String doInBackground(Void... params){
        String result;

        Map<String, String> vars = new HashMap<>();
        vars.put("location", _location);
        vars.put("whitelist", (_whitelist)?"true":"false");
        vars.put("startDate", _startDate);
        vars.put("endDate", _endDate);
        vars.put("message", _message);
        if(null != _rules)
            vars.putAll(_rules);

        try {
            result = _rest.postForObject(_url+"/messages/"+_sessionId, vars, String.class);

            ObjectMapper mapper = new ObjectMapper();
            Response response = mapper.readValue(result, Response.class);

            _successful = response.getSuccessful();
             return response.getMessage();

        }catch (RestClientException e) {
            Log.e("REST ERROR", e.getClass().toString() + " : " + e.getMessage());
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
    }

}

