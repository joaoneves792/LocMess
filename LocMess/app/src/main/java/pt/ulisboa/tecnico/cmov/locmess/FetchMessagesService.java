package pt.ulisboa.tecnico.cmov.locmess;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.Exceptions.LocationException;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.GetMessagesTask;

/**
 * Created by joao on 5/7/17.
 */

public class FetchMessagesService extends Service{
    public static final String FETCH_INTENT = "FETCH_INTENT";
    public static final String SSID_ARRAY = "SSID_ARRAY";


    public class LocalBinder extends Binder {
        FetchMessagesService getService() {
            return FetchMessagesService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    private final IBinder mBinder = new LocalBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LocMess");
        //Acquire the lock
        wl.acquire();

        //Toast.makeText(context,"Fetching Messages", Toast.LENGTH_SHORT).show();

        long sessionId;
        DataManager dm = DataManager.getInstance();
        try {
            sessionId = dm.getSessionId(getApplicationContext());
            if(-1 == sessionId){
                throw new StorageException();
            }
        }catch (StorageException e){
            Log.d(DataManager.STORAGE_TAG, "Failed to retrive session data!");
            wl.release();
            return START_NOT_STICKY;
        }
        try {
            GPSLocationListener gps = GPSLocationListener.getInstance(getApplicationContext());

            List<String> ssids = intent.getStringArrayListExtra(SSID_ARRAY);

            GetMessagesTask fetchTask = new GetMessagesTask(getApplicationContext(), sessionId, gps.getLatitude(), gps.getLongitude(), ssids);

            fetchTask.execute();
        }catch (LocationException e){
            Log.d(DataManager.STORAGE_TAG, e.getMessage());
            wl.release();
            return START_NOT_STICKY;
        }

        wl.release();
        return START_NOT_STICKY;
    }

}

