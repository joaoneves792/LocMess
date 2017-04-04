package pt.ulisboa.tecnico.cmov.locmess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.PostMessageTask;

public class PostMessageRules extends AppCompatActivity {

    private long _sessionId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            _sessionId = DataManager.getInstance().getSessionId(getApplicationContext());
        }catch (StorageException e){
            Toast.makeText(this, R.string.sessionFailed, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
        setContentView(R.layout.activity_post_message_rules);
    }


    public void cancel(View view) {
        finish();
    }

    public void postMessage(View view) {

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        final int checkedId = radioGroup.getCheckedRadioButtonId();
//        String mode = (String) ((RadioButton) radioGroup.getChildAt(checkedId)).getText();
        boolean whitelist = false;
        switch(checkedId) {
            case R.id.radioButtonWhitelist:
                whitelist = true;
                break;
            case R.id.radioButtonBlacklist:
                whitelist = false;
                break;
        }

        Intent previousIntent = getIntent();

        String title = previousIntent.getStringExtra("TITLE");
        String text = previousIntent.getStringExtra("TEXT");
        String location = previousIntent.getStringExtra("LOCATION");
        String startdate = previousIntent.getStringExtra("STARTDATE");
        String starttime = previousIntent.getStringExtra("STARTTIME");
        String enddate = previousIntent.getStringExtra("ENDDATE");
        String endtime = previousIntent.getStringExtra("ENDTIME");

        (new PostMessageTask(this, _sessionId, location, null, whitelist, starttime+"-"+startdate, endtime+"-"+enddate, text)).execute();

        finish();
    }


}
