package pt.ulisboa.tecnico.cmov.locmess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.Domain.Location;
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

        /*@Paulo if you get the locations from the server dont forget to include these: (from local cache)*/
        /*And also if the one the user selects is not on the list provided by the server the we have to upload it*/
        /*You might want to consider moving this to the Task*/
        List<Location> cachedLocations = LocalCache.getInstance(getApplicationContext()).getLocations();

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
