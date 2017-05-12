package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.springframework.web.client.RestClientException;

import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.Domain.DeliverableMessage;
import pt.ulisboa.tecnico.cmov.locmess.LocalCache;

/**
 * Created by joao on 3/29/17.
 */

public class DeleteMessageTask extends RestTask{

    private long _sessionId;
    private long _messageId;
    private String _hash;

    private boolean _successful;

    public DeleteMessageTask(Activity appContext, long sessionId, String hash) {
        super(appContext);

        _sessionId = sessionId;
        _messageId = LocalCache.getInstance().getMessage(hash).getId();
        _hash = hash;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {

            // FIXME delete from cache?

            _rest.delete(_url+"/messages/"+_sessionId+"/"+LocalCache.getInstance().getMessage(_hash).getId());
//            _rest.delete(_url+"/messages/"+_sessionId+"/"+_messageId);
            _successful = true;
            return "Message Deleted.";

        } catch (RestClientException e) {
            Log.e("REST ERROR", e.getClass().toString()+" : "+e.getMessage());
            _successful = false;
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(_context, result, Toast.LENGTH_SHORT).show();
    }

}

