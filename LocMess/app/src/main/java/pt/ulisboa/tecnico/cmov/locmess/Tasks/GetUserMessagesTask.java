package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.Domain.DeliverableMessage;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.Responses.MessagesList;
import pt.ulisboa.tecnico.cmov.locmess.Responses.Response;

/**
 * Created by joao on 3/29/17.
 */

public class GetUserMessagesTask extends RestTask{


    private boolean _successful = false;
    private long _sessionId;

    private List<DeliverableMessage> _myMessages;


    public GetUserMessagesTask(Activity appContext, long sessionId){
        super(appContext);
        _sessionId = sessionId;
        _myMessages = new LinkedList<>();
    }

    @Override
    protected String doInBackground(Void... params){
        String result;

        try {
            result = _rest.getForObject(_url+"/messages/"+_sessionId, String.class);
        }catch (RestClientException e){
            Log.e("REST ERROR", e.getClass().toString()+" : "+e.getMessage());
            _successful = false;
            return e.getMessage();
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            MessagesList messages = mapper.readValue(result, MessagesList.class);
            _myMessages = messages.getMessages();
            _successful = messages.getSuccessful();
            return messages.getMessage();

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
            text.setText("MY MESSAGES:\n");
            for (DeliverableMessage message : _myMessages) {
                text.setText(text.getText() + message.getId().toString() + " : " + message.getMessage() + "\n");
            }*/
        }
    }

}

