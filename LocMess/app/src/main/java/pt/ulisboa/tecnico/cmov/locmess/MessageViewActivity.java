package pt.ulisboa.tecnico.cmov.locmess;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MessageViewActivity extends AppCompatActivity {

    public static final String MESSAGE_ID = "MESSAGE_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_view);
    }
}
