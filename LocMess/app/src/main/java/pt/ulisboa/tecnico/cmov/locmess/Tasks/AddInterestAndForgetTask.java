package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.springframework.web.client.RestClientException;

/**
 * Created by joao on 3/29/17.
 */

public class AddInterestAndForgetTask extends AddInterestTask{

    public AddInterestAndForgetTask(Activity appContext, long sessionId, String key, String value){
        super(appContext, sessionId, key, value);
    }


    @Override
    protected void onPostExecute(String result){
        //Forget the result
    }

}

