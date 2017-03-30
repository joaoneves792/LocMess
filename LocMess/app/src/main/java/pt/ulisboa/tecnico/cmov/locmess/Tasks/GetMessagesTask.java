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
import java.util.Map;

import pt.ulisboa.tecnico.cmov.locmess.Domain.DeliverableMessage;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.Responses.InterestsList;
import pt.ulisboa.tecnico.cmov.locmess.Responses.MessagesList;
import pt.ulisboa.tecnico.cmov.locmess.Responses.Response;

/**
 * Created by joao on 3/29/17.
 */

public class GetMessagesTask extends RestTask{


    private boolean _successful = false;

    private long _sessionId;
    private double _longitude;
    private double _latitude;
    private List<String> _ssids;

    private List<DeliverableMessage> _receivedMessages;


    public GetMessagesTask(Activity appContext, long sessionId, double latitude, double longitude, List<String> ssids){
        super(appContext);
        _sessionId = sessionId;
        _latitude = latitude;
        _longitude = longitude;
        _ssids = ssids;
        _receivedMessages = new LinkedList<>();
    }

    @Override
    protected String doInBackground(Void... params){
        String result;

        String ssidList = "";
        for(String ssid : _ssids)
            ssidList += ssid+",";
        ssidList = ssidList.replaceAll(",$", "");
        try {
            result = _rest.getForObject(_url+"/messages?id="+_sessionId+"&latitude="+_latitude+"&longitude="+_longitude+"&ssid="+ssidList, String.class);
        }catch (RestClientException e){
            Log.e("REST ERROR", e.getClass().toString()+" : "+e.getMessage());
            _successful = false;
            return e.getMessage();
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            MessagesList messages = mapper.readValue(result, MessagesList.class);
            _receivedMessages = messages.getMessages();
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
            text.setText("");
            for (DeliverableMessage message : _receivedMessages) {
                text.setText(text.getText() + message.getMessage() + "\n");
            }*/
        }
    }

}

