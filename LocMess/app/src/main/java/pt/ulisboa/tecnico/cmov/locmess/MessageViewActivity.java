package pt.ulisboa.tecnico.cmov.locmess;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.locmess.Domain.DeliverableMessage;

public class MessageViewActivity extends AppCompatActivity {

    public static final String MESSAGE_ID = "MESSAGE_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_view);

        try {
            Intent previousIntent = getIntent();
            String hash = previousIntent.getStringExtra(MESSAGE_ID);
            DeliverableMessage message = LocalCache.getInstance().getMessage(hash);

            if (message != null) {
                TextView sender = (TextView) findViewById(R.id.textViewSender);
                sender.setText(message.getSender());

                TextView location = (TextView) findViewById(R.id.textViewLocation);
                location.setText(message.getLocation());

                TextView date = (TextView) findViewById(R.id.textViewPublishDate);
                date.setText(message.getPublicationDate());

                TextView text = (TextView) findViewById(R.id.textViewText);
                text.setText(message.getMessage());

            } else {
                Toast.makeText(this, "Failed to find message with id " + hash, Toast.LENGTH_LONG).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "message id from intent = " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // FIXME change message to seen

    }

}
