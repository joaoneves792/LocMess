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
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pt.ulisboa.tecnico.cmov.locmess.Domain.Profile;
import pt.ulisboa.tecnico.cmov.locmess.LocalCache;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.Responses.InterestsList;
import pt.ulisboa.tecnico.cmov.locmess.Responses.Response;
import pt.ulisboa.tecnico.cmov.locmess.UI.InterestViewActivity;
import pt.ulisboa.tecnico.cmov.locmess.UI.RuleViewActivity;

/**
 * Created by joao on 3/29/17.
 */

public class GetUserProfileTask extends RestTask{

//    static final int ADD_RULE_REQUEST_CODE = 1;
    static final int UPDATE_INTEREST_REQUEST_CODE = 2;

    private boolean _successful = false;

    private long _sessionId;
    private Map<String, String> _interests;
    private Map<String, String> _unsyncronizedInterests;


    public GetUserProfileTask(Activity appContext, long sessionId){
        super(appContext);
        _sessionId = sessionId;
        _unsyncronizedInterests = new HashMap<>();
    }

    @Override
    protected String doInBackground(Void... params){
        String result;
        _interests = LocalCache.getInstance(_context.getApplicationContext()).getStoredProfile().getInterests();

        try {
            result =  _rest.getForObject(_url+"/profiles/"+_sessionId, String.class);

        } catch(RestClientException e) {
            Log.e("REST ERROR", e.getClass().toString()+" : "+e.getMessage());
            _successful = false;
            return e.getMessage();
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            InterestsList interestsResponse = mapper.readValue(result, InterestsList.class);
            _interests = interestsResponse.getInterests();
            _successful = interestsResponse.getSuccessful();


            /*Integrate results with local cache*/
            Profile profile = LocalCache.getInstance(_context.getApplicationContext()).getStoredProfile();
            for(String key :profile.getInterests().keySet()){
                if(_interests.containsKey(key) && _interests.get(key).equals(profile.getInterests().get(key))){
                    continue;
                }
                _interests.put(key, profile.getInterests().get(key));
                _unsyncronizedInterests.put(key, profile.getInterests().get(key));
            }

            return interestsResponse.getMessage();

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
        if(!_successful) {
            Toast.makeText(_context, result, Toast.LENGTH_SHORT).show();
        }
        ListView list = (ListView) _context.findViewById(R.id.listViewInterests);

        ListAdapter interestsAdapter = new GetUserProfileTask.InterestAdapter(_context, _interests);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String key = ((TextView) view.findViewById(R.id.textViewKey)).getText().toString();
                String value = ((TextView) view.findViewById(R.id.textViewValue)).getText().toString();

                Intent intent = new Intent(_context, InterestViewActivity.class);
                intent.putExtra("INTEREST_KEY", key);
                intent.putExtra("INTEREST_VALUE", value);
                _context.startActivityForResult(intent, UPDATE_INTEREST_REQUEST_CODE);
            }
        });

        list.setAdapter(interestsAdapter);

        /*Syncronhize the interests*/
        for(String key : _unsyncronizedInterests.keySet()) {
            new AddInterestAndForgetTask(_context, _sessionId, key, _unsyncronizedInterests.get(key)).execute();
        }

    }

    // class for the custom display of locations' list
    protected class InterestAdapter extends ArrayAdapter<Map.Entry<String,String>> {

        public InterestAdapter(@NonNull Context context, Map<String,String> interests) {
            super(context, R.layout.interest_item, new ArrayList<>(interests.entrySet()));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater interestInflater = LayoutInflater.from(getContext());
            View rowView = interestInflater.inflate(R.layout.interest_item, parent, false);

            Map.Entry<String,String> interest = getItem(position);
            TextView key = (TextView) rowView.findViewById(R.id.textViewKey);
            TextView value = (TextView) rowView.findViewById(R.id.textViewValue);

            key.setText(interest.getKey());
            value.setText(interest.getValue());

            return rowView;
        }

    }

}

