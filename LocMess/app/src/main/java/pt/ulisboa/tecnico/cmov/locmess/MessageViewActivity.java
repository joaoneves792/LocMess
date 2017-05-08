package pt.ulisboa.tecnico.cmov.locmess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.Domain.DeliverableMessage;

public class MessageViewActivity extends AppCompatActivity {

    public static final String MESSAGE_ID = "MESSAGE_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_view);

        Intent previousIntent = getIntent();
        long id = Integer.parseInt(previousIntent.getStringExtra(MESSAGE_ID));

//        List<DeliverableMessage> messages = LocalCache.getInstance().getMessages();
//        Log.d("MessageViewActivity :", messages.toString());

        DeliverableMessage message = LocalCache.getInstance().getMessage(id);

        TextView sender = (TextView) findViewById(R.id.textViewSender);
        sender.setText(message.getSender());

        TextView location = (TextView) findViewById(R.id.textViewLocation);
        location.setText(message.getLocation());

        TextView date = (TextView) findViewById(R.id.textViewPublishDate);
        date.setText(message.getPublicationDate());

        TextView text = (TextView) findViewById(R.id.textViewText);
        text.setText(message.getMessage());

        // FIXME change message to seen

    }

}
