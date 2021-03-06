package pt.ulisboa.tecnico.cmov.locmess.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.locmess.DataManager;
import pt.ulisboa.tecnico.cmov.locmess.Domain.GPSLocation;
import pt.ulisboa.tecnico.cmov.locmess.Domain.Location;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.LocalCache;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.DeleteLocationTask;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.UploadLocationTask;

public class LocationGPSViewActivity extends AppCompatActivity {

    private long _sessionId = 0;
    String _locationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            _sessionId = DataManager.getInstance().getSessionId(getApplicationContext());

        } catch(StorageException e) {
            Toast.makeText(this, R.string.sessionFailed, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }

        setContentView(R.layout.activity_location_gps_view);

        Intent previousIntent = getIntent();

        _locationName = previousIntent.getStringExtra("LOCATIONNAME");
        TextView locationName = (TextView) findViewById(R.id.textViewLocationName);
        locationName.setText(_locationName);

        TextView type = (TextView) findViewById(R.id.textViewLocationType);
        type.setText("GPS");

        EditText latitude = (EditText) findViewById(R.id.editTextLatitude);
        EditText longitude = (EditText) findViewById(R.id.editTextLongitude);
        EditText radius = (EditText) findViewById(R.id.editTextRadius);

        GPSLocation gps;
        for (Location location : LocalCache.getInstance(getApplicationContext()).getLocations()) {
            if (location.getName().equals(_locationName) && location.getType().equals("GPS")) {
                gps = (GPSLocation) location;
                latitude.setText(Double.toString(gps.getLatitude()));
                longitude.setText(Double.toString(gps.getLongitude()));
                radius.setText("50");
                break;
            }
        }

    }


    public void cancel(View view) {
        finish();
    }

    public void update(View view) {
        double latitude = Double.parseDouble(((EditText) findViewById(R.id.editTextLatitude)).getText().toString());
        double longitude = Double.parseDouble(((EditText) findViewById(R.id.editTextLongitude)).getText().toString());
        double radius = Double.parseDouble(((EditText) findViewById(R.id.editTextRadius)).getText().toString());

        GPSLocation gpsLocation = new GPSLocation(_locationName, latitude, longitude, radius);

        (new UploadLocationTask(this, _sessionId, gpsLocation)).execute();
        finish();
    }

    public void deleteLocation(View view) {
        (new DeleteLocationTask(this, _sessionId, _locationName)).execute();
        finish();
    }







}
