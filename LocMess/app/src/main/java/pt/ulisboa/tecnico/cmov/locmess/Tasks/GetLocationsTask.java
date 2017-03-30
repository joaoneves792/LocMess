package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import pt.ulisboa.tecnico.cmov.locmess.Domain.GPSLocation;
import pt.ulisboa.tecnico.cmov.locmess.Domain.Location;
import pt.ulisboa.tecnico.cmov.locmess.Domain.WiFiLocation;
import pt.ulisboa.tecnico.cmov.locmess.HomeActivity;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.Responses.Cookie;
import pt.ulisboa.tecnico.cmov.locmess.Responses.LocationsList;
import pt.ulisboa.tecnico.cmov.locmess.Responses.Response;

/**
 * Created by joao on 3/29/17.
 */

public class GetLocationsTask extends RestTask{

    private long _sessionId;
    private List<Location> _locations = new LinkedList<>();
    private boolean _successful;

    public GetLocationsTask(Activity appContext, long sessionId){
        super(appContext);
        _sessionId = sessionId;
    }

    @Override
    protected String doInBackground(Void... params){
        String result;
        JSONObject json;
        _successful = false;
        ObjectMapper mapper = new ObjectMapper();
        try {
            result = _rest.getForObject(_url+"/locations?id=" + _sessionId, String.class);
            json = new JSONObject(result);
            _successful = json.getBoolean("successful");
            if(!_successful){
                return json.getString("message");
            }
            JSONArray locArray = json.getJSONArray("locations");
            for(int i=0; i<locArray.length(); i++){
                JSONObject location = locArray.getJSONObject(i);
                if(location.getString("type").equals("GPS")){
                    GPSLocation gps = mapper.readValue(location.toString(), GPSLocation.class);
                    _locations.add(gps);
                }else{
                    WiFiLocation wifi = mapper.readValue(location.toString(), WiFiLocation.class);
                    _locations.add(wifi);
                }
            }
            return json.getString("message");
        }catch (RestClientException e){
            Log.e("REST ERROR", e.getClass().toString()+" : "+e.getMessage());
            return e.getMessage();
        }catch (JSONException |
                IOException e){
            Log.e("JSON ERROR", e.getMessage());
            _successful = false;
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result){
        Toast.makeText(_context, result, Toast.LENGTH_SHORT).show();
        if(_successful) {
            /*TextView text = (TextView)_context.findViewById(R.id.debugText);
            text.setText("");
            for(Location loc : _locations) {
                String locText = loc.getName();
                if(loc.getType().equals("GPS")){
                    GPSLocation gps = (GPSLocation)loc;
                    locText += " :" + gps.getLatitude() + " " + gps.getLongitude() + " " + gps.getRadius();
                }else{
                    WiFiLocation wifi = (WiFiLocation)loc;
                    for(String ssid : wifi.getWifiIds()){
                        locText += " " + ssid;
                    }
                }
                text.setText(text.getText() + "\n" + locText);
            }
            //Intent intent = new Intent(_context, HomeActivity.class);
            //intent.putExtra("SESSIONID", _sessionId);
            //_context.startActivity(intent);*/
        }
    }

}

