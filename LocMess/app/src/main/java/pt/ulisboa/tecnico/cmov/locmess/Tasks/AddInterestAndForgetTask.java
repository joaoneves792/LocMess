package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.app.Activity;

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

