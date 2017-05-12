package pt.ulisboa.tecnico.cmov.locmess.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import pt.ulisboa.tecnico.cmov.locmess.DataManager;
import pt.ulisboa.tecnico.cmov.locmess.Domain.GPSLocation;
import pt.ulisboa.tecnico.cmov.locmess.Domain.Location;
import pt.ulisboa.tecnico.cmov.locmess.Domain.WiFiLocation;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.LocalCache;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.DeleteLocationTask;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.UploadLocationTask;

public class LocationWiFiViewActivity extends AppCompatActivity {

    private long _sessionId = 0;
    String _locationName;

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

        setContentView(R.layout.activity_location_wi_fi_view);

        Intent previousIntent = getIntent();

        _locationName = previousIntent.getStringExtra("LOCATIONNAME");
        TextView locationName = (TextView) findViewById(R.id.textViewLocationName);
        locationName.setText(_locationName);

        TextView type = (TextView) findViewById(R.id.textViewLocationType);
        type.setText("WiFi");

        WiFiLocation wifi;
        for (Location location : LocalCache.getInstance(getApplicationContext()).getLocations()) {
            if (location.getName().equals(_locationName) && location.getType().equals("Wifi")) {
                wifi = (WiFiLocation) location;

                String ssids = "";
                for (String ssid : wifi.getWifiIds()) {
                    ssids += ssid + "\n";
                }

                ((EditText) findViewById(R.id.editTextSSID)).setText(ssids);

                break;
            }
        }

    }


    public void cancel(View view) {
        finish();
    }

    public void update(View view) {
        String name = ((TextView) findViewById(R.id.textViewLocationName)).getText().toString();
        String ssids = ((EditText) findViewById(R.id.editTextSSID)).getText().toString().replaceAll("\n+", "\n");
        String[] ssidsCollection = ssids.split("\n");

        WiFiLocation wiFiLocation = new WiFiLocation(name, Arrays.asList(ssidsCollection));

        (new UploadLocationTask(this, _sessionId, wiFiLocation)).execute();
        finish();
    }

    public void deleteLocation(View view) {
        (new DeleteLocationTask(this, _sessionId, _locationName)).execute();
        finish();
    }

}
