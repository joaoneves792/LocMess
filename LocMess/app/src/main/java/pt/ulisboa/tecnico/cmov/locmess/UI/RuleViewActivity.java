package pt.ulisboa.tecnico.cmov.locmess.UI;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import pt.ulisboa.tecnico.cmov.locmess.R;

public class RuleViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_view);

        Intent intent = getIntent();
        ((TextView) findViewById(R.id.textViewKey)).setText(intent.getStringExtra("RULE_KEY"));
        ((EditText) findViewById(R.id.editTextValue)).setText(intent.getStringExtra("RULE_VALUE"));

    }

    public void updateRule(View view) {

        String key = ((TextView) findViewById(R.id.textViewKey)).getText().toString();
        String value = ((EditText) findViewById(R.id.editTextValue)).getText().toString();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("NEW_RULE_KEY", key);
        returnIntent.putExtra("NEW_RULE_VALUE", value);
        setResult(Activity.RESULT_OK, returnIntent);

        finish();
    }

    public void deleteRule(View view) {

        String key = ((TextView) findViewById(R.id.textViewKey)).getText().toString();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("NEW_RULE_KEY", key);
        setResult(Activity.RESULT_OK, returnIntent);

        finish();
    }



}
