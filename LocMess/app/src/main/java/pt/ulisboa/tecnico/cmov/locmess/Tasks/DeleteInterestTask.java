package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.springframework.web.client.RestClientException;

/**
 * Created by joao on 3/29/17.
 */

public class DeleteInterestTask extends RestTask{

    private long _sessionId;
    private String _key;

    private boolean _successful;

    public DeleteInterestTask(Activity appContext, long sessionId, String key){
        super(appContext);
        _sessionId = sessionId;
        _key = key;
    }

    @Override
    protected String doInBackground(Void... params){
        try {
            _rest.delete(_url+"/profiles/"+_sessionId+"/"+_key);

//            FIXME delete interest from cache

            _successful = true;
            return "Location Deleted.";

        } catch (RestClientException e) {
            Log.e("REST ERROR", e.getClass().toString()+" : "+e.getMessage());
            _successful = false;
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result){
        Toast.makeText(_context, result, Toast.LENGTH_SHORT).show();
    }

}

