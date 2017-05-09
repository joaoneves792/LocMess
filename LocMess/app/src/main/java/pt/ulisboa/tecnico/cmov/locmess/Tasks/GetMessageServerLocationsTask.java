package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.Domain.GPSLocation;
import pt.ulisboa.tecnico.cmov.locmess.Domain.Location;
import pt.ulisboa.tecnico.cmov.locmess.Domain.WiFiLocation;
import pt.ulisboa.tecnico.cmov.locmess.MessageViewActivity;
import pt.ulisboa.tecnico.cmov.locmess.R;

public class GetMessageServerLocationsTask extends RestTask{

    private long _sessionId;
    private List<Location> _locations = new LinkedList<>();
    private boolean _successful;

    public GetMessageServerLocationsTask(Activity appContext, long sessionId){
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

        if(_successful) {
            // clear and repopulate locations list
            RadioGroup radioGroup = (RadioGroup) _context.findViewById(R.id.radioGroupLocations);
            radioGroup.removeAllViews();

            int i = 0;
            RadioButton button;
            for(Location location : _locations) {
                button = new RadioButton(_context);
                button.setId(i++);
                button.setText(location.getName());
                radioGroup.addView(button);
            }
        }
    }

}
