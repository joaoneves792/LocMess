package pt.ulisboa.tecnico.cmov.locmess.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import pt.ulisboa.tecnico.cmov.locmess.R;

public class AddMessageRuleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_message_rule);
    }


    public void addRule(View view) {

        String key = ((EditText) findViewById(R.id.editTextKey)).getText().toString();
        String value = ((EditText) findViewById(R.id.editTextValue)).getText().toString();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("NEW_RULE_KEY", key);
        returnIntent.putExtra("NEW_RULE_VALUE", value);
        setResult(Activity.RESULT_OK, returnIntent);

        finish();
    }


}
