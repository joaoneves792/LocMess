package pt.ulisboa.tecnico.cmov.locmess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.PostMessageTask;

public class PostMessageRules extends AppCompatActivity {

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

    public void postMessage(View view) {

        Intent previousIntent = getIntent();

        String title = previousIntent.getStringExtra("TITLE");
        String text = previousIntent.getStringExtra("TEXT");
        String location = previousIntent.getStringExtra("LOCATION");
        String startdate = previousIntent.getStringExtra("STARTDATE");
        String starttime = previousIntent.getStringExtra("STARTTIME");
        String enddate = previousIntent.getStringExtra("ENDDATE");
        String endtime = previousIntent.getStringExtra("ENDTIME");

        (new PostMessageTask(this, _sessionId, location, null, true, starttime+"-"+startdate, endtime+"-"+enddate, text)).execute();

        finish();
    }

    //    public void postMessage(View view) {
//        String text = ((EditText) findViewById(R.id.editTextBody)).getText().toString();
//        String location = ((EditText) findViewById(R.id.editTextLocation)).getText().toString();
//        (new PostMessageTask(this, _sessionId, location, null, true, "12:34-01/02/1234", "12:34-01/02/1234", text)).execute();
//
////        String location = ((TextView) findViewById(R.id.textViewLocation)).getText().toString();
////        String startDate = ((EditText) findViewById(R.id.editTextStartDate)).getText().toString();
////        String endDate = ((EditText) findViewById(R.id.editTextEndDate)).getText().toString();
////
////        (new PostMessageTask(this, _sessionId, location, null, true, startDate, endDate, text)).execute();
//
//        finish();
//    }


}
