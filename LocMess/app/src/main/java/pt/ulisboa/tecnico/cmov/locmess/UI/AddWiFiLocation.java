package pt.ulisboa.tecnico.cmov.locmess.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import pt.ulisboa.tecnico.cmov.locmess.DataManager;
import pt.ulisboa.tecnico.cmov.locmess.Domain.WiFiLocation;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.LocalCache;
import pt.ulisboa.tecnico.cmov.locmess.R;
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

    public void useCurentLocation(View view) {
//        ArrayList<String> ssidsList = LocalCache.getInstance().getWifiReceiver().getSSIDs();
        ArrayList<String> ssidsList = LocalCache.getInstance().getPeerList();

        String ssids = "";
        if(ssidsList != null) {
            // FIXME get ssids from termite?
            for (String ssid : ssidsList)
                ssids += ssid + "\n";

            EditText ssidEditText = (EditText) findViewById(R.id.editTextName);
            ssidEditText.setText(ssids);

        } else {
            Toast.makeText(this, "No detected SSIDs.", Toast.LENGTH_SHORT).show();
        }

    }


    public void uploadLocation(View view) {
        String name = ((EditText) findViewById(R.id.editTextName)).getText().toString();
        String ssids = ((EditText) findViewById(R.id.editTextName)).getText().toString().replaceAll("\n+", "\n");

        if(name.equals("") || ssids.equals("")) {
            Toast.makeText(this, "Please provide all required information.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] ssidsCollection = ssids.split("\n");

        WiFiLocation wiFiLocation = new WiFiLocation(name, Arrays.asList(ssidsCollection));

        (new UploadLocationTask(this, _sessionId, wiFiLocation)).execute();
        finish();
    }
}
