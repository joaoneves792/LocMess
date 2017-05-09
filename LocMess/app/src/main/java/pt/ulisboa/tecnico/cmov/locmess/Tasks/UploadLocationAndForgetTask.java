package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.app.Activity;

import pt.ulisboa.tecnico.cmov.locmess.Domain.Location;

/**
 * Created by joao on 3/29/17.
 */

public class UploadLocationAndForgetTask extends UploadLocationTask{


    public UploadLocationAndForgetTask(Activity appContext, long sessionId, Location location){
        super(appContext, sessionId, location);
    }


    @Override
    protected void onPostExecute(String result){
        //Forget the result
    }

}

