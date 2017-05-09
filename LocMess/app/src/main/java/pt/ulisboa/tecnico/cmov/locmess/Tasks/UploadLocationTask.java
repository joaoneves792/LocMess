package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import pt.ulisboa.tecnico.cmov.locmess.Domain.GPSLocation;
import pt.ulisboa.tecnico.cmov.locmess.Domain.Location;
import pt.ulisboa.tecnico.cmov.locmess.Domain.WiFiLocation;
import pt.ulisboa.tecnico.cmov.locmess.LocalCache;
import pt.ulisboa.tecnico.cmov.locmess.Responses.Response;

/**
 * Created by joao on 3/29/17.
 */

public class UploadLocationTask extends RestTask{

    private long _sessionId;
    private Location _location;

    private boolean _successful;

    public UploadLocationTask(Activity appContext, long sessionId, Location location){
        super(appContext);
        _sessionId = sessionId;
        _location = location;
    }

    @Override
    protected String doInBackground(Void... params){
        String result;


        Map<String, String> vars = new HashMap<>();
        vars.put("id", Long.valueOf(_sessionId).toString());
        vars.put("name", _location.getName());
        if(_location.getType().equals("GPS")){
            GPSLocation gps = (GPSLocation) _location;
            vars.put("latitude", Double.valueOf(gps.getLatitude()).toString());
            vars.put("longitude", Double.valueOf(gps.getLongitude()).toString());
            vars.put("radius", Double.valueOf(gps.getRadius()).toString());
            LocalCache.getInstance(_context.getApplicationContext()).insertIntoStorage(gps);
        }else{
            WiFiLocation wifi = (WiFiLocation)_location;
            String SSID = "ssid";
            int i = 1;
            for(String ssid : wifi.getWifiIds()){
                vars.put(SSID+(i++), ssid);
            }
            LocalCache.getInstance(_context.getApplicationContext()).insertIntoStorage(wifi);
        }
        try {
            result = _rest.postForObject(_url+"/locations", vars, String.class);

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

