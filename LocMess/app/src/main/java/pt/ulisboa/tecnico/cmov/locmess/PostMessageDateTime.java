package pt.ulisboa.tecnico.cmov.locmess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;

public class PostMessageDateTime extends AppCompatActivity {

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
        setContentView(R.layout.activity_post_message_datetime);
    }


    public void cancel(View view) {
        finish();
    }

    public void selectRules(View view) {

        Intent previousIntent = getIntent();
        Intent intent = new Intent(this, PostMessageRules.class);

        intent.putExtra("TITLE", previousIntent.getStringExtra("TITLE"));
        intent.putExtra("TEXT", previousIntent.getStringExtra("TEXT"));
        intent.putExtra("LOCATION", previousIntent.getStringExtra("LOCATION"));
        intent.putExtra("STARTDATE", ((TextView) findViewById(R.id.editTextStartDate)).getText().toString());
        intent.putExtra("STARTTIME", ((TextView) findViewById(R.id.editTextStartTime)).getText().toString());
        intent.putExtra("ENDDATE", ((TextView) findViewById(R.id.editTextEndDate)).getText().toString());
        intent.putExtra("ENDTIME", ((TextView) findViewById(R.id.editTextEndTime)).getText().toString());
        startActivity(intent);

        finish();
    }



}
