package pt.ulisboa.tecnico.cmov.locmess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.AddInterestTask;

public class AddInterestActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_add_interest);
    }

    public void submitInterest(View view) {
        String key = ((EditText) findViewById(R.id.editTextKey)).getText().toString();
        String value = ((EditText) findViewById(R.id.editTextValue)).getText().toString();

        (new AddInterestTask(this, _sessionId, key, value)).execute();
        finish();
    }

}
