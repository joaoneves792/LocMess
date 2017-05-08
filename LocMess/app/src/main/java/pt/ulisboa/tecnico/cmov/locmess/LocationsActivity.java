package pt.ulisboa.tecnico.cmov.locmess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.Domain.DeliverableMessage;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.GetLocationsTask;

public class LocationsActivity extends AppCompatActivity {

    private long _sessionId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

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

        (new GetLocationsTask(this, _sessionId)).execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_location_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.addGPSLocation:
                intent = new Intent(this, AddGPSLocationActivity.class);
                startActivity(intent);
                return true;

            case R.id.addWiFiLocation:
                intent = new Intent(this, AddWiFiLocation.class);
                startActivity(intent);
                return true;

            case R.id.addCurrentLocation:
                /*@Paulo if you actually get around to implementing this dont forget to insert the new location on the LocalCache!!!*/
                Toast.makeText(getApplicationContext(), "FIXME Add Current Location not yet implemented.", Toast.LENGTH_LONG).show();
                return true;

            default:
                // action not recognised, invoke superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}




//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        _sessionId = getIntent().getLongExtra("SESSIONID", -1);
//        setContentView(R.layout.activity_locations);
//
//        Button addLocationButton = (Button) findViewById(R.id.buttonAddLocation);
//        addLocationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                PopupMenu addLocationPopupMenu = new PopupMenu(getApplicationContext(), v);
//
//                addLocationPopupMenu.inflate(R.menu.add_location_menu);
//                addLocationPopupMenu.show();
//
//                addLocationPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//
//                        Toast.makeText(getApplicationContext(), item.toString(), Toast.LENGTH_SHORT).show();
//                        return false;
//                    }
//                });
//            }
//        });
//
//    }

