package pt.ulisboa.tecnico.cmov.locmess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import pt.ulisboa.tecnico.cmov.locmess.Tasks.LoginTask;

public class LoginActivity extends AppCompatActivity {

    String msg = "LoginActivity : ";
    public static final String EXTRA_MESSAGE = "login.USERNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(msg, "onCreate() event");
    }


    @Override
    protected void onResume(){
        super.onResume();
        String username = getIntent().getStringExtra("USERNAME");
        String password = getIntent().getStringExtra("PASSWORD");
        if(null == username || null == password){
            return;
        }
        ((EditText)findViewById(R.id.editTextUsername)).setText(username);
        ((EditText)findViewById(R.id.editTextPassword)).setText(password);

    }

    /** Called when the user taps the SIGNUP button */
    public void signup(View view) {
        Intent intent = new Intent(this, SignupActivity.class);

        EditText editText = (EditText) findViewById(R.id.editTextUsername);
        String message = editText.getText().toString();

        // intent.putExtra(R.string.extra_message, message);
        intent.putExtra(EXTRA_MESSAGE, message);

        startActivity(intent);
    }

    /** Called when the user taps the SIGNUP button */
    public void login(View view) {
        String username = ((EditText)findViewById(R.id.editTextUsername)).getText().toString();
        String password = ((EditText)findViewById(R.id.editTextPassword)).getText().toString();
        (new LoginTask(this,username, password)).execute();
    }





}
