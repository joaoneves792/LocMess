package pt.ulisboa.tecnico.cmov.locmess;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.locmess.Domain.GPSLocation;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.LocationException;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
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

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: request Permissions
                return;
            }

            LocationManager locationManager = (LocationManager) getSystemService(android.content.Context.LOCATION_SERVICE);

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                android.location.Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60 * 1000, 10, (LocationListener) this);
                }

                EditText latitude = (EditText) findViewById(R.id.editTextLatitude);
                latitude.setText(Double.toString(location.getLatitude()));
                EditText longitude = (EditText) findViewById(R.id.editTextLongitude);
                longitude.setText(Double.toString(location.getLongitude()));
                EditText radius = (EditText) findViewById(R.id.editTextRadius);
                radius.setText("20");

            } else {
                Toast.makeText(this, "GPS disabled.", Toast.LENGTH_SHORT).show();
            }

        } catch (SecurityException e) {
//            Toast.makeText(this, "GPS security exception.", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }


    public void useCurentLocation(View view) {
        try {
            LocationManager locationManager = (LocationManager) getSystemService(android.content.Context.LOCATION_SERVICE);

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                android.location.Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60 * 1000, 10, (LocationListener) this);
                }

                EditText latitude = (EditText) findViewById(R.id.editTextLatitude);
                latitude.setText(Double.toString(location.getLatitude()));
                EditText longitude = (EditText) findViewById(R.id.editTextLongitude);
                longitude.setText(Double.toString(location.getLongitude()));
                EditText radius = (EditText) findViewById(R.id.editTextRadius);
                radius.setText("20");

            } else {
                Toast.makeText(this, "GPS disabled.", Toast.LENGTH_SHORT).show();
            }

        } catch (SecurityException e) {
//            Toast.makeText(this, "GPS security exception.", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void uploadLocation(View view) {
        String name = ((EditText) findViewById(R.id.editTextSSID)).getText().toString();
        double latitude = Double.parseDouble(((EditText) findViewById(R.id.editTextLatitude)).getText().toString());
        double longitude = Double.parseDouble(((EditText) findViewById(R.id.editTextLongitude)).getText().toString());
        double radius = Double.parseDouble(((EditText) findViewById(R.id.editTextRadius)).getText().toString());

        (new UploadLocationTask(this, _sessionId, new GPSLocation(name, latitude, longitude, radius))).execute();
        finish();
    }
}
