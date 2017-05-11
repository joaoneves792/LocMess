package pt.ulisboa.tecnico.cmov.locmess.UI;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.locmess.DataManager;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.AddInterestTask;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.DeleteInterestTask;

public class InterestViewActivity extends AppCompatActivity {

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

        setContentView(R.layout.activity_interest_view);

        Intent intent = getIntent();
        ((TextView) findViewById(R.id.textViewKey)).setText(intent.getStringExtra("INTEREST_KEY"));
        ((EditText) findViewById(R.id.editTextValue)).setText(intent.getStringExtra("INTEREST_VALUE"));
    }


    public void updateInterest(View view) {
        String key = ((TextView) findViewById(R.id.textViewKey)).getText().toString();
        String value = ((EditText) findViewById(R.id.editTextValue)).getText().toString();

        (new AddInterestTask(this, _sessionId, key, value)).execute();

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);

        finish();
    }

    public void deleteInterest(View view) {

        String key = ((TextView) findViewById(R.id.textViewKey)).getText().toString();

        (new DeleteInterestTask(this, _sessionId, key)).execute();
        Intent returnIntent = new Intent();

        setResult(Activity.RESULT_OK, returnIntent);

        finish();
    }


}
