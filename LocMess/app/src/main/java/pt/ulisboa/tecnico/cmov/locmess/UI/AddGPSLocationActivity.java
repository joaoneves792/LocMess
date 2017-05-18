package pt.ulisboa.tecnico.cmov.locmess.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.locmess.DataManager;
import pt.ulisboa.tecnico.cmov.locmess.Domain.GPSLocation;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.LocationException;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.GPSLocationListener;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.UploadLocationTask;

public class AddGPSLocationActivity extends AppCompatActivity {
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

        setContentView(R.layout.activity_add_gps_location);

        try{
            GPSLocationListener gps = GPSLocationListener.getInstance(this);


            EditText latitude = (EditText) findViewById(R.id.editTextLatitude);
            latitude.setText(Double.toString(gps.getLatitude()));
            EditText longitude = (EditText) findViewById(R.id.editTextLongitude);
            longitude.setText(Double.toString(gps.getLongitude()));
            EditText radius = (EditText) findViewById(R.id.editTextRadius);
            radius.setText("50");


        } catch (LocationException e) {
            Toast.makeText(this, "GPS is unavailable", Toast.LENGTH_LONG).show();
        }

    }


    public void useCurentLocation(View view) {
        try{
            GPSLocationListener gps = GPSLocationListener.getInstance(this);


            EditText latitude = (EditText) findViewById(R.id.editTextLatitude);
            latitude.setText(Double.toString(gps.getLatitude()));
            EditText longitude = (EditText) findViewById(R.id.editTextLongitude);
            longitude.setText(Double.toString(gps.getLongitude()));
            EditText radius = (EditText) findViewById(R.id.editTextRadius);
            radius.setText("50");


        } catch (LocationException e) {
            Toast.makeText(this, "GPS is unavailable", Toast.LENGTH_LONG).show();
        }
    }

    public void uploadLocation(View view) {
        String name =           ((EditText) findViewById(R.id.editTextName)).getText().toString();
        String latitudeText =   ((EditText) findViewById(R.id.editTextLatitude)).getText().toString();
        String longitudeText =  ((EditText) findViewById(R.id.editTextLongitude)).getText().toString();
        String radiusText =     ((EditText) findViewById(R.id.editTextRadius)).getText().toString();

        if(name.equals("") || latitudeText.equals("") || longitudeText.equals("") || radiusText.equals("")) {
            Toast.makeText(this, "Please provide all required information.", Toast.LENGTH_SHORT).show();
            return;
        }

        double latitude = Double.parseDouble(latitudeText);
        double longitude = Double.parseDouble(longitudeText);
        double radius = Double.parseDouble(radiusText);

        GPSLocation gpsLocation = new GPSLocation(name, latitude, longitude, radius);

        (new UploadLocationTask(this, _sessionId, gpsLocation)).execute();
        finish();
    }
}
