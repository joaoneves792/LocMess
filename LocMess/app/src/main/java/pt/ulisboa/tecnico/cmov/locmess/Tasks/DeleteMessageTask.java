package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.springframework.web.client.RestClientException;

/**
 * Created by joao on 3/29/17.
 */

public class DeleteMessageTask extends RestTask{

    private long _sessionId;
    private long _messageId;

    private boolean _successful;

    public DeleteMessageTask(Activity appContext, long sessionId, long messageId){
        super(appContext);
        _sessionId = sessionId;
        _messageId = messageId;
    }

    @Override
    protected String doInBackground(Void... params){
        try {
            _rest.delete(_url+"/messages/"+_sessionId+"/"+_messageId);
            _successful = true;
            return "Location Deleted.";
        }catch (RestClientException e){
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

