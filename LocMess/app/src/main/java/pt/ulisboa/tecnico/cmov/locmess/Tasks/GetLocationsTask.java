package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import pt.ulisboa.tecnico.cmov.locmess.Domain.GPSLocation;
import pt.ulisboa.tecnico.cmov.locmess.Domain.Location;
import pt.ulisboa.tecnico.cmov.locmess.Domain.WiFiLocation;
import pt.ulisboa.tecnico.cmov.locmess.HomeActivity;
import pt.ulisboa.tecnico.cmov.locmess.MessageViewActivity;
import pt.ulisboa.tecnico.cmov.locmess.PostMessageRules;
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
            ListView list = (ListView) _context.findViewById(R.id.listViewLocations);

            ListAdapter locationsAdapter = new LocationListAdapter(_context, _locations);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(_context, MessageViewActivity.class);
                    _context.startActivity(intent);
                }
            });

            list.setAdapter(locationsAdapter);


        }
    }

    // class for the custom display of locations' list
    protected class LocationListAdapter extends ArrayAdapter<Location> {

        public LocationListAdapter(@NonNull Context context, List<Location> locations) {
            super(context, R.layout.location_item, locations);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater locationInflater = LayoutInflater.from(getContext());
            View rowView = locationInflater.inflate(R.layout.location_item, parent, false);

            Location location = getItem(position);
            TextView locationName = (TextView) rowView.findViewById(R.id.textViewLocationName);
            TextView locationType = (TextView) rowView.findViewById(R.id.textViewLocationType);

            locationName.setText(location.getName());
            locationType.setText(location.getType());

            return rowView;
        }

    }


}
