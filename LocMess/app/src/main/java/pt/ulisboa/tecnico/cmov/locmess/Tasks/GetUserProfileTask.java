package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.ulisboa.tecnico.cmov.locmess.HomeActivity;
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
            /*TextView text = (TextView) _context.findViewById(R.id.debugText);
            text.setText("");
            for (Map.Entry<String, String> entry : _interests.entrySet()) {
                text.setText(text.getText() + entry.getKey() + " : " + entry.getValue() + "\n");
            }*/
        }
    }

}

