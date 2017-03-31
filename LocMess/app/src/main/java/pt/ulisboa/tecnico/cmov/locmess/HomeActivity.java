package pt.ulisboa.tecnico.cmov.locmess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    private long _sessionId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @Override
    protected void onResume(){
        super.onResume();
        _sessionId = getIntent().getLongExtra("SESSIONID", -1);
        Toast.makeText(this, new Long(getIntent().getLongExtra("SESSIONID", -1)).toString(), Toast.LENGTH_LONG).show();
    }


    public void locations(View view) {
        Intent intent = new Intent(this, LocationsActivity.class);
        intent.putExtra("SESSIONID", _sessionId);
        startActivity(intent);
    }

    public void messages(View view) {
        Intent intent = new Intent(this, MessagesActivity.class);
        intent.putExtra("SESSIONID", _sessionId);
        startActivity(intent);
    }

    public void postMessage(View view) {
        Intent intent = new Intent(this, PostMessageActivity.class);
        intent.putExtra("SESSIONID", _sessionId);
        startActivity(intent);
    }

    public void profile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void settings(View view) {
        Toast.makeText(this, "not implemented", Toast.LENGTH_LONG).show();
    }

}
