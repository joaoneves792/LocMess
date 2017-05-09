package pt.ulisboa.tecnico.cmov.locmess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.Domain.Location;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.GetLocationsTask;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.GetMessageServerLocationsTask;
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

        (new GetMessageServerLocationsTask(this, _sessionId)).execute();

    }

    public void cancel(View view) {
        finish();
    }

    public void selectDateTimes(View view) {

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroupLocations);

        try {
            final int checkedId = radioGroup.getCheckedRadioButtonId();
            String location = ((RadioButton) radioGroup.getChildAt(checkedId)).getText().toString();

            Intent intent = new Intent(this, PostMessageDateTime.class);

            intent.putExtra("TEXT", ((TextView) findViewById(R.id.editTextMessage)).getText().toString());
            intent.putExtra("LOCATION", location);
            startActivity(intent);

            finish();

        } catch (Exception e) {
            radioGroup.clearCheck();
            Toast.makeText(this, "Please select a location.", Toast.LENGTH_LONG).show();
        }

    }

}
