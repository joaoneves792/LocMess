package pt.ulisboa.tecnico.cmov.locmess.UI;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.locmess.DataManager;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.LoginTask;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.TestSessionTask;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "login.USERNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            getActionBar().setTitle("Login");
            getSupportActionBar().setTitle("Login");
        } catch (NullPointerException e) {
            Toast.makeText(this, "null", Toast.LENGTH_LONG);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Test the session of the last logged in user
        DataManager dm = DataManager.getInstance();
        try {
            String username = dm.getLastLoggedInUsername(getApplicationContext());
            dm.setUser(username);
            long sessionId = dm.getSessionId(getApplicationContext());
            (new TestSessionTask(this, sessionId)).execute(); //If the session is still valid then we jump to HomeActiivity
        } catch (StorageException e) {
            //do nothing and let the user proceed to login
        }

        String username = getIntent().getStringExtra("USERNAME");
        String password = getIntent().getStringExtra("PASSWORD");
        if(null == username || null == password){
            try {
                username = DataManager.getInstance().getLastLoggedInUsername(getApplicationContext());
                password = "";
            } catch (StorageException e) {
                return;
            }
        }

        ((EditText)findViewById(R.id.editTextUsername)).setText(username);
        ((EditText)findViewById(R.id.editTextPassword)).setText(password);

    }

    /** Called when the user taps the SIGNUP button */
    public void signup(View view) {
        Intent intent = new Intent(this, SignupActivity.class);

        EditText editText = (EditText) findViewById(R.id.editTextUsername);
        String message = editText.getText().toString();

        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
        finish();
    }

    /** Called when the user taps the LOGIN button */
    public void login(View view) {
        String username = ((EditText)findViewById(R.id.editTextUsername)).getText().toString();
        String password = ((EditText)findViewById(R.id.editTextPassword)).getText().toString();
        (new LoginTask(this, username, password)).execute();
        finish();
    }





}
