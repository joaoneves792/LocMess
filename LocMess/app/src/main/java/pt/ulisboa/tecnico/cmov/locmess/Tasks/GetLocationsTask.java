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
import pt.ulisboa.tecnico.cmov.locmess.LocalCache;
import pt.ulisboa.tecnico.cmov.locmess.UI.LocationGPSViewActivity;
import pt.ulisboa.tecnico.cmov.locmess.R;

/**
 * Created by joao on 3/29/17.
 */

public class GetLocationsTask extends RestTask{

    protected long _sessionId;
    protected List<Location> _locations = new LinkedList<>();
    private List<Location> _unsyncronizedLocations = new LinkedList<>();
    protected boolean _successful;

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

            /*Syncronize cache and server*/
            List<Location> cachedLocations = LocalCache.getInstance(_context.getApplicationContext()).getLocations();
            for(Location l : cachedLocations){
                boolean found = false;
                for(Location sl : _locations){
                    if(sl.getName().equals(l.getName())){
                        found = true;
                    }
                }
                if(!found){
                    _locations.add(l);
                    //_unsyncronizedLocations.add(l);
                }
            }

            return json.getString("message");

        }catch (RestClientException e){
            Log.e("REST ERROR", e.getClass().toString()+" : "+e.getMessage());
            _locations = LocalCache.getInstance(_context.getApplicationContext()).getLocations();
            return e.getMessage();

        }catch (JSONException |
                IOException e){
            Log.e("JSON ERROR", e.getMessage());
            _successful = false;
            _locations = LocalCache.getInstance(_context.getApplicationContext()).getLocations();
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result){
        if(!_successful) {
            Toast.makeText(_context, result, Toast.LENGTH_SHORT).show();
        }
        ListView list = (ListView) _context.findViewById(R.id.listViewLocations);

        ListAdapter locationsAdapter = new LocationListAdapter(_context, _locations);

//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(_context, MessageViewActivity.class);
//                _context.startActivity(intent);
//            }
//        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // start GPS view activity
                if (( (TextView) view.findViewById(R.id.textViewLocationType)).getText().toString().equals("GPS")) {
                    Intent intent = new Intent(_context, LocationGPSViewActivity.class);
                    intent.putExtra("LOCATIONNAME", ((TextView) view.findViewById(R.id.textViewLocationName)).getText().toString());
                    _context.startActivity(intent);
                }

            }
        });

//            list.setAdapter(locationsAdapter);

        list.setAdapter(locationsAdapter);

        //for(Location l: _unsyncronizedLocations) {
        //    new UploadLocationAndForgetTask(_context, _sessionId, l).execute();
        //}
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
