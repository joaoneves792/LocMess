package pt.ulisboa.tecnico.cmov.locmess;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Arrays;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.Domain.GPSLocation;
import pt.ulisboa.tecnico.cmov.locmess.Domain.WiFiLocation;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.AddInterestTask;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.DeleteInterestTask;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.DeleteLocationTask;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.DeleteMessageTask;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.GetInterestKeysTask;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.GetLocationsTask;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.GetMessagesTask;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.GetUserMessagesTask;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.LogoutTask;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.PostMessageTask;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.UploadLocationTask;

public class TestHomeActivity extends AppCompatActivity {

    private long _sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @Override
    protected void onResume(){
        super.onResume();
        _sessionId = getIntent().getLongExtra("SESSIONID", -1);

        List<String> ssids = Arrays.asList("eduroam", "ZON-B34F");
        (new GetMessagesTask(this, _sessionId, 3.45235423432, -13.32423423, ssids)).execute();
    }

    protected void logout(View view){
        (new LogoutTask(this, _sessionId)).execute();
    }


    protected void addArco(View view){
        GPSLocation arco = new GPSLocation("Arco do Cego", 3.45235423432, -13.32423423, 20);
        (new UploadLocationTask(this, _sessionId, arco)).execute();
    }

    protected void addRNL(View view){
        List<String> ssids = Arrays.asList("eduroam", "ZON-B34F");
        WiFiLocation rnl = new WiFiLocation("RNL", ssids);
        (new UploadLocationTask(this, _sessionId, rnl)).execute();
    }

    protected void getLocations(View view){
        (new GetLocationsTask(this, _sessionId)).execute();
    }

    protected void deleteRNL(View view){
        (new DeleteLocationTask(this, _sessionId, "RNL")).execute();
    }

    protected void addInterests(View view){
        (new AddInterestTask(this, _sessionId, "job", "Student")).execute();
        (new AddInterestTask(this, _sessionId, "club", "SCP")).execute();
    }

    protected void listInterests(View view){
        //(new GetUserProfileTask(this, _sessionId)).execute();
        (new GetInterestKeysTask(this, _sessionId)).execute();
    }

    protected void deleteInterest(View view){
        (new DeleteInterestTask(this, _sessionId, "club")).execute();
    }

    protected void post(View view){
        (new PostMessageTask(this, _sessionId, "RNL", null, true, "00:00-1/1/2010", "00:00-12/25/2020", "Programming skills enhancing pills! Now available for just 100$. Call 934363257")).execute();

    }

    protected void get(View view){
        (new GetUserMessagesTask(this, _sessionId)).execute();
    }

    protected void deleteMessage(View view){
        (new DeleteMessageTask(this, _sessionId, 2)).execute();
    }
}

