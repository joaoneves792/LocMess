package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.Responses.PossibleKeysList;
import pt.ulisboa.tecnico.cmov.locmess.Responses.Response;

/**
 * Created by joao on 3/29/17.
 */

public class GetInterestKeysTask extends RestTask{


    private boolean _successful = false;

    private long _sessionId;
    private List<String> _interests;


    public GetInterestKeysTask(Activity appContext, long sessionId){
        super(appContext);
        _sessionId = sessionId;
    }

    @Override
    protected String doInBackground(Void... params){
        String result;
        try {
            result =  _rest.getForObject(_url+"/interests?id="+_sessionId, String.class);
        }catch (RestClientException e){
            Log.e("REST ERROR", e.getClass().toString()+" : "+e.getMessage());
            _successful = false;
            return e.getMessage();
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            PossibleKeysList keys = mapper.readValue(result, PossibleKeysList.class);
            _interests = keys.getKeys();
            _successful = keys.getSuccessful();
            return keys.getMessage();

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
            for(String key : _interests){
                text.setText(text.getText() + key + "\n");
            }*/
        }
    }

}

