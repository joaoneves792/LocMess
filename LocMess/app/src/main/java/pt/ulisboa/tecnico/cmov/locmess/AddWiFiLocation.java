package pt.ulisboa.tecnico.cmov.locmess;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;

import pt.ulisboa.tecnico.cmov.locmess.Domain.WiFiLocation;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.UploadLocationTask;

public class AddWiFiLocation extends AppCompatActivity {

    private long _sessionId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            _sessionId = DataManager.getInstance().getSessionId(getApplicationContext());

        } catch(StorageException e) {
            Toast.makeText(this, R.string.sessionFailed, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_add_wifi_location);
    }


    public void uploadLocation(View view) {
        String name = ((EditText) findViewById(R.id.editTextName)).getText().toString();
        String ssids = ((EditText) findViewById(R.id.editTextSSID)).getText().toString();
        String[] ssidsCollection = ssids.split(";");

        WiFiLocation wiFiLocation = new WiFiLocation(name, Arrays.asList(ssidsCollection));

        (new UploadLocationTask(this, _sessionId, wiFiLocation)).execute();

        finish();
    }
}
