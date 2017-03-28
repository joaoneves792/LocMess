package pt.ulisboa.tecnico.cmov.locmess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    String msg = "LoginActivity : ";
    public static final String EXTRA_MESSAGE = "login.USERNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(msg, "onCreate() event");
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
        //FIXME

    }





}
