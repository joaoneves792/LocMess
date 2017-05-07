package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.app.Activity;
import android.content.Context;
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
import pt.ulisboa.tecnico.cmov.locmess.Domain.Location;
import pt.ulisboa.tecnico.cmov.locmess.LocalCache;
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


    public GetMessagesTask(Context appContext, long sessionId, double latitude, double longitude, List<String> ssids){
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
        if(null != _ssids) {
            for (String ssid : _ssids)
                ssidList += ssid + ",";
        }
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
        if(_successful) {
            Toast.makeText(_appContext, "Checked messages: " + _receivedMessages.size(), Toast.LENGTH_SHORT).show();
        }/*else {
            Toast.makeText(_appContext, "Failed to check messages",  Toast.LENGTH_SHORT).show();
        }*/
        if(_successful) {
            LocalCache cache = LocalCache.getInstance();
            List<DeliverableMessage> newMessages = cache.storeMessages(_receivedMessages);
            if(newMessages.size() > 0){
                /*TODO
                    Generate a notification for each new message in this list
                 */
                Toast.makeText(_appContext, "You have new messages!!", Toast.LENGTH_SHORT).show();
            }

            /*TODO
                Maybe launch an intent to HomeActivity so it can update the messages list?
             */

        }
    }

}

