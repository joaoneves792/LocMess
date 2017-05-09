package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.app.Activity;

import pt.ulisboa.tecnico.cmov.locmess.Domain.Location;
import pt.ulisboa.tecnico.cmov.locmess.LocalCache;

public class GetLocationIntoCacheTask extends GetLocationsTask{

    private String _desiredLocation;

    public GetLocationIntoCacheTask(Activity appContext, long sessionId, String desiredLocation){
        super(appContext, sessionId);
        _desiredLocation = desiredLocation;
    }

    @Override
    protected void onPostExecute(String result){

        for(Location l : _locations){
           if(l.getName().equals(_desiredLocation)){
               LocalCache.getInstance(_context.getApplicationContext()).insertIntoStorage(l);
           }
        }


    }

}
