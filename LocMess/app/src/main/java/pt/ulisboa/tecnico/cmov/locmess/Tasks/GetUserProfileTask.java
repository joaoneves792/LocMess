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
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.ulisboa.tecnico.cmov.locmess.Domain.Location;
import pt.ulisboa.tecnico.cmov.locmess.HomeActivity;
import pt.ulisboa.tecnico.cmov.locmess.MessageViewActivity;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.Responses.Cookie;
import pt.ulisboa.tecnico.cmov.locmess.Responses.InterestsList;
import pt.ulisboa.tecnico.cmov.locmess.Responses.Response;

/**
 * Created by joao on 3/29/17.
 */

public class GetUserProfileTask extends RestTask{


    private boolean _successful = false;

    private long _sessionId;
    private Map<String, String> _interests;


    public GetUserProfileTask(Activity appContext, long sessionId){
        super(appContext);
        _sessionId = sessionId;
    }

    @Override
    protected String doInBackground(Void... params){
        String result;
        try {
            result =  _rest.getForObject(_url+"/profiles/"+_sessionId, String.class);
        }catch (RestClientException e){
            Log.e("REST ERROR", e.getClass().toString()+" : "+e.getMessage());
            _successful = false;
            return e.getMessage();
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            InterestsList interestsResponse = mapper.readValue(result, InterestsList.class);
            _interests = interestsResponse.getInterests();
            _successful = interestsResponse.getSuccessful();
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
        Toast.makeText(_context, result, Toast.LENGTH_SHORT).show();

        if(_successful) {
//            ListView list = (ListView) _context.findViewById(R.id.listViewInterests);
//
//            ListAdapter interestsAdapter = new GetUserProfileTask.InterestAdapter(_context, _interests);
//
//            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Toast.makeText(_context, "selected something", Toast.LENGTH_SHORT).show();
//
////                    Intent intent = new Intent(_context, MessageViewActivity.class);
////                    intent.putExtra("SESSIONID", _sessionId);
////                    _context.startActivity(intent);
//                }
//            });
//
//            list.setAdapter(interestsAdapter);
//

        }
    }

//    // class for the custom display of locations' list
//    protected class InterestAdapter extends ArrayAdapter<Map.Entry<String,String>> {
//
//        public InterestAdapter(@NonNull Context context, Map<String,String> interests) {
//            super(context, R.layout.interest_item, new ArrayList<Map.Entry<String,String>>(interests.values()));
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            LayoutInflater interestInflater = LayoutInflater.from(getContext());
//            View rowView = interestInflater.inflate(R.layout.interest_item, parent, false);
//
//            Map.Entry<String,String> interest = getItem(position);
//            TextView key = (TextView) rowView.findViewById(R.id.textViewKey);
//            TextView value = (TextView) rowView.findViewById(R.id.textViewValue);
//
//            key.setText(interest.getKey());
//            value.setText(interest.getValue());
//
//            return rowView;
//        }
//
//    }

}

