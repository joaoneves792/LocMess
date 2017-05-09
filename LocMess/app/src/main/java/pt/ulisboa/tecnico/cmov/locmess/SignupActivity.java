package pt.ulisboa.tecnico.cmov.locmess;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.locmess.Tasks.RegisterTask;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Get the Intent that started this activity and extract the username
        Intent intent = getIntent();
        String message = intent.getStringExtra(LoginActivity.EXTRA_MESSAGE);

        // autofill username from login screen
        EditText editText = (EditText) findViewById(R.id.editTextUsername);
        editText.setText(message);

    }

    /** Called when the user taps the SIGNUP button */
    public void submit(View view) {
        String username = ((EditText)findViewById(R.id.editTextUsername)).getText().toString();
        String password = ((EditText)findViewById(R.id.editTextPassword)).getText().toString();
        String retypePassword = ((EditText)findViewById(R.id.editTextPasswordRetype)).getText().toString();
        if(password.equals(retypePassword)) {
            (new RegisterTask(this, username, password)).execute();
        }else{
            Toast.makeText(this, getResources().getString(R.string.badPasswordMatch), Toast.LENGTH_SHORT).show();
        }
    }


}
