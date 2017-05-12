package pt.ulisboa.tecnico.cmov.locmess.UI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.DataManager;
import pt.ulisboa.tecnico.cmov.locmess.Domain.WiFiLocation;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.LocationException;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.GPSLocationListener;
import pt.ulisboa.tecnico.cmov.locmess.LocalCache;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.UploadLocationTask;
import pt.ulisboa.tecnico.cmov.locmess.WifiReceiver;

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

            String ssids = "";

//            // FIXME get ssids from termite?
//            for (String ssid : /* fixme SSIDs from wifi scan */) {
//                    ssids += ssid + "\n";
//            }

            EditText ssidEditText = (EditText) findViewById(R.id.editTextSSID);
            ssidEditText.setText(ssids);
    }


    public void uploadLocation(View view) {
        String name = ((EditText) findViewById(R.id.editTextName)).getText().toString();
        String ssids = ((EditText) findViewById(R.id.editTextSSID)).getText().toString().replaceAll("\n+", "\n");
        String[] ssidsCollection = ssids.split("\n");

        WiFiLocation wiFiLocation = new WiFiLocation(name, Arrays.asList(ssidsCollection));

        (new UploadLocationTask(this, _sessionId, wiFiLocation)).execute();

        finish();
    }
}
