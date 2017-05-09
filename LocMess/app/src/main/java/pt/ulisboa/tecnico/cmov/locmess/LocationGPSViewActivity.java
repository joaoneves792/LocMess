package pt.ulisboa.tecnico.cmov.locmess;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.locmess.Domain.GPSLocation;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
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

        TextView _locationName = (TextView) findViewById(R.id.textViewLocationName);
        _locationName.setText(previousIntent.getStringExtra("LOCATIONNAME"));

        TextView type = (TextView) findViewById(R.id.textViewLocationType);
        type.setText("GPS");

        EditText latitude = (EditText) findViewById(R.id.editTextLatitude);
//        latitude.setText(Double.toString(gps.getLatitude()));
        EditText longitude = (EditText) findViewById(R.id.editTextLongitude);
//        longitude.setText(Double.toString(gps.getLongitude()));
        EditText radius = (EditText) findViewById(R.id.editTextRadius);
//        radius.setText("50");


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
