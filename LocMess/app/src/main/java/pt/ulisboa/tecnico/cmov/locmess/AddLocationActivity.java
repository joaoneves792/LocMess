package pt.ulisboa.tecnico.cmov.locmess;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.locmess.Domain.GPSLocation;
import pt.ulisboa.tecnico.cmov.locmess.Domain.Location;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.UploadLocationTask;

public class AddLocationActivity extends AppCompatActivity {
    private long _sessionId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _sessionId = getIntent().getLongExtra("SESSIONID", -1);
        setContentView(R.layout.activity_add_location);
    }


    public void uploadLocation(View view) {
        String name = ((EditText) findViewById(R.id.editTextName)).getText().toString();
        double latitude = Double.parseDouble(((EditText) findViewById(R.id.editTextLatitude)).getText().toString());
        double longitude = Double.parseDouble(((EditText) findViewById(R.id.editTextLongitude)).getText().toString());
        double radius = Double.parseDouble(((EditText) findViewById(R.id.editTextRadius)).getText().toString());

        (new UploadLocationTask(this, _sessionId, new GPSLocation(name, latitude, longitude, radius))).execute();
    }
}
