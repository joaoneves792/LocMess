package pt.ulisboa.tecnico.cmov.locmess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import pt.ulisboa.tecnico.cmov.locmess.Tasks.GetLocationsTask;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.LoginTask;

public class LocationsActivity extends AppCompatActivity {

    private long _sessionId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _sessionId = getIntent().getLongExtra("SESSIONID", -1);
        setContentView(R.layout.activity_locations);
    }

    @Override
    protected void onResume(){
        super.onResume();
        (new GetLocationsTask(this, getIntent().getLongExtra("SESSIONID", -1))).execute();
    }

    public void addLocation(View view) {
        Intent intent = new Intent(this, AddLocationActivity.class);
        intent.putExtra("SESSIONID", _sessionId);
        startActivity(intent);
    }

}
