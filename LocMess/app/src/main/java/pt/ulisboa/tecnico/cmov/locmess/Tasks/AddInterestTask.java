package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.springframework.web.client.RestClientException;

import pt.ulisboa.tecnico.cmov.locmess.LocalCache;

/**
 * Created by joao on 3/29/17.
 */

public class AddInterestTask extends RestTask{

    private long _sessionId;

    private String _key;
    private String _value;

    private boolean _successful;

    public AddInterestTask(Activity appContext, long sessionId, String key, String value){
        super(appContext);
        _sessionId = sessionId;
        _key = key;
        _value = value;
    }

    @Override
    protected String doInBackground(Void... params){
        LocalCache.getInstance(_context.getApplicationContext()).storeInterest(_key, _value);
        try {
            _rest.put(_url+"/profiles/"+_sessionId+"/"+_key+"?value="+_value, null);
            _successful = true;
            return "Interest added.";
        }catch (RestClientException e) {
            Log.e("REST ERROR", e.getClass().toString() + " : " + e.getMessage());
            _successful = false;
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result){
        Toast.makeText(_context, result, Toast.LENGTH_SHORT).show();
    }

}

