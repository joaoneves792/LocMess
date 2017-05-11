package pt.ulisboa.tecnico.cmov.locmess.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import pt.ulisboa.tecnico.cmov.locmess.DataManager;
import pt.ulisboa.tecnico.cmov.locmess.Domain.DeliverableMessage;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.LocalCache;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.DeleteInterestTask;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.DeleteMessageTask;

public class MessageViewActivity extends AppCompatActivity {

    public static final String MESSAGE_ID = "MESSAGE_ID";

    private long _sessionId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            _sessionId = DataManager.getInstance().getSessionId(getApplicationContext());
        } catch (StorageException e) {
            Toast.makeText(this, R.string.sessionFailed, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }

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

                // if message not from the user remove delete button
                if (message.getSender().equals(DataManager.getInstance().getUsername(getApplicationContext()))) {
                    View deleteButton = findViewById(R.id.buttonDeleteMessage);
//                    ((ViewManager) deleteButton.getParent()).removeView(toRemove);
                    deleteButton.setVisibility(View.VISIBLE);
                } else {
                    View deleteButton = findViewById(R.id.buttonDeleteMessage);
                    deleteButton.setVisibility(View.GONE);
                }

            } else {
                Toast.makeText(this, "Failed to find message with id " + hash, Toast.LENGTH_LONG).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "id from intent = " + e.getMessage(), Toast.LENGTH_LONG).show();

        } catch (StorageException e) {
            Toast.makeText(this, "failed to retrieve username from DataManager. " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public void deleteMessage(View view) {
        String sender = ((TextView) findViewById(R.id.textViewSender)).getText().toString();
        String location = ((TextView) findViewById(R.id.textViewLocation)).getText().toString();
        String message = ((TextView) findViewById(R.id.textViewText)).getText().toString();

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
            String hash = Base64.encodeToString(messageDigest, Base64.URL_SAFE | Base64.NO_WRAP | Base64.DEFAULT);

            (new DeleteMessageTask(this, _sessionId, hash)).execute();
            finish();

        } catch (NoSuchAlgorithmException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
