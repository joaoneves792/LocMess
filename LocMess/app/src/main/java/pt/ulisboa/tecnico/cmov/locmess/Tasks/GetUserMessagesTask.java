package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Base64;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.Domain.DeliverableMessage;
import pt.ulisboa.tecnico.cmov.locmess.MessageViewActivity;
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

        Toast.makeText(_context, result, Toast.LENGTH_LONG).show();
        Log.e("ERRROR", result);

        if(_successful) {
            ListView list = (ListView) _context.findViewById(R.id.listViewMessages);

            ListAdapter messagesAdapter = new GetUserMessagesTask.PostedMessagesListAdapter(_context, _myMessages);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String sender = ((TextView) view.findViewById(R.id.textViewSender)).getText().toString();
                    String location = ((TextView) view.findViewById(R.id.textViewLocation)).getText().toString();
                    String message = ((TextView) view.findViewById(R.id.textViewMessageBody)).getText().toString();

                    int senderLength = sender.getBytes().length;
                    int locationLength = location.getBytes().length;
                    int messageLength = message.getBytes().length;

                    byte[] concatMessage = new byte[senderLength + locationLength + messageLength];
                    System.arraycopy(sender.getBytes(), 0, concatMessage, 0, senderLength);
                    System.arraycopy(location.getBytes(), 0, concatMessage, senderLength, locationLength);
                    System.arraycopy(message.getBytes(), 0, concatMessage, senderLength + locationLength, messageLength);

                    try {
                        MessageDigest md;
                        md = MessageDigest.getInstance("MD5");
                        md.update(concatMessage);
                        byte[] messageDigest = md.digest();
                        String hash = Base64.encodeToString(messageDigest, Base64.URL_SAFE | Base64.NO_WRAP);

                        Intent intent = new Intent(_context, MessageViewActivity.class);
                        intent.putExtra(MessageViewActivity.MESSAGE_ID, hash);
                        _context.startActivity(intent);

                    } catch (NoSuchAlgorithmException e) {
                        Toast.makeText(_context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

            list.setAdapter(messagesAdapter);

        }
    }

    // class for the custom display of user's posted messages
    protected class PostedMessagesListAdapter extends ArrayAdapter<DeliverableMessage> {

        public PostedMessagesListAdapter(@NonNull Context context, List<DeliverableMessage> messages) {
            super(context, R.layout.posted_message_item, messages);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater messageInflater = LayoutInflater.from(getContext());
            View rowView = messageInflater.inflate(R.layout.posted_message_item, parent, false);

            DeliverableMessage message = getItem(position);
            TextView sender = (TextView) rowView.findViewById(R.id.textViewSender);
            TextView messageBody = (TextView) rowView.findViewById(R.id.textViewMessageBody);
            TextView location = (TextView) rowView.findViewById(R.id.textViewLocation);
            TextView publishDate = (TextView) rowView.findViewById(R.id.textViewPublishDate);

            sender.setText(message.getSender());
            location.setText(message.getLocation());
            publishDate.setText(message.getPublicationDate());
            messageBody.setText(message.getMessage());

            return rowView;
        }

    }

}

