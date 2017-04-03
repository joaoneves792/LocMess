package pt.ulisboa.tecnico.cmov.locmess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.GetUserProfileTask;

public class ProfileActivity extends AppCompatActivity {

    private long _sessionId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    @Override
    protected void onResume(){
        super.onResume();

        try {
            _sessionId = DataManager.getInstance().getSessionId(getApplicationContext());

        } catch(StorageException e) {
            Toast.makeText(this, R.string.sessionFailed, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }

        (new GetUserProfileTask(this, _sessionId)).execute();
    }

    public void addInterest(View view) {
        Intent intent = new Intent(this, AddInterestActivity.class);
        startActivity(intent);
    }




}
