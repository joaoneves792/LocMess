package pt.ulisboa.tecnico.cmov.locmess;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import pt.ulisboa.tecnico.cmov.locmess.Tasks.GetUserMessagesTask;

public class MessagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
    }

    @Override
    protected void onResume(){
        super.onResume();
        (new GetUserMessagesTask(this, getIntent().getLongExtra("SESSIONID", -1))).execute();

    }
}
