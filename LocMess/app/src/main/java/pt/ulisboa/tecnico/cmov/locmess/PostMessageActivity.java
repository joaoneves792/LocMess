package pt.ulisboa.tecnico.cmov.locmess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.PostMessageTask;

public class PostMessageActivity extends AppCompatActivity {

    private long _sessionId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            _sessionId = DataManager.getInstance().getSessionId(getApplicationContext());
        }catch (StorageException e){
            Toast.makeText(this, R.string.sessionFailed, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
        setContentView(R.layout.activity_post_message);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void postMessage(View view) {
        String text = ((EditText) findViewById(R.id.editTextBody)).getText().toString();
        String location = ((TextView) findViewById(R.id.textViewLocation)).getText().toString();

        String startDate = ((EditText) findViewById(R.id.editTextStartDate)).getText().toString();
        String endDate = ((EditText) findViewById(R.id.editTextEndDate)).getText().toString();

        (new PostMessageTask(this, _sessionId, location, null, true, startDate, endDate, text)).execute();
    }
}
