package pt.ulisboa.tecnico.cmov.locmess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.GetUserMessagesTask;

public class MessagesActivity extends AppCompatActivity {

    private long _sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
    }

    @Override
    protected void onResume(){
        super.onResume();
        try {
            _sessionId = DataManager.getInstance().getSessionId(getApplicationContext());
        }catch (StorageException e){
            Toast.makeText(this, R.string.sessionFailed, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
        (new GetUserMessagesTask(this, _sessionId)).execute();

    }
}
