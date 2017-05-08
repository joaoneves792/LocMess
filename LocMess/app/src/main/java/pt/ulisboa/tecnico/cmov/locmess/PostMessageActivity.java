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

    public void cancel(View view) {
        finish();
    }

    public void selectDateTimes(View view) {
        Intent intent = new Intent(this, PostMessageDateTime.class);
//        intent.putExtra("TITLE", ((TextView) findViewById(R.id.editTextTitle)).getText().toString());
        intent.putExtra("TEXT", ((TextView) findViewById(R.id.editTextMessage)).getText().toString());
        intent.putExtra("LOCATION", ((TextView) findViewById(R.id.editTextLocation)).getText().toString());
        startActivity(intent);

        finish();
    }

}
