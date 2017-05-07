package pt.ulisboa.tecnico.cmov.locmess;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.Domain.Location;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.LocationException;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.GetMessagesTask;

/**
 * Created by joao on 5/7/17.
 */

public class FetchMessagesBroadcastReceiver extends BroadcastReceiver implements LocationListener{

    private double _latitude;
    private double _longitude;
    private List<String> _ssids;

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LocMess");
        //Acquire the lock
        wl.acquire();

        //Toast.makeText(context,"Fetching Messages", Toast.LENGTH_SHORT).show();

        long sessionId;
        DataManager dm = DataManager.getInstance();
        try {
            sessionId = dm.getSessionId(context);
            if(-1 == sessionId){
                throw new StorageException();
            }
        }catch (StorageException e){
            Log.d(DataManager.STORAGE_TAG, "Failed to retrive session data!");
            wl.release();
            return;
        }
        try {
            getGPSCoordinates(context);

            _ssids = new LinkedList<>();
            _ssids.add("eduroam");

            GetMessagesTask fetchTask = new GetMessagesTask(context, sessionId, _latitude, _longitude, _ssids);

            fetchTask.execute();

        }catch (LocationException e){
            Log.d(DataManager.STORAGE_TAG, e.getMessage());
            wl.release();
            return;
        }

        wl.release();
    }

    private void getGPSCoordinates(Context context) throws LocationException{
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(android.content.Context.LOCATION_SERVICE);

            //getting GPS status
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                throw new LocationException("DISABLED");
            }

            android.location.Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(null == location){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60*1000, 10, this);
            }else{
                _latitude = location.getLatitude();
                _longitude = location.getLongitude();
            }

        }catch (SecurityException e){
            throw new LocationException("SECURITY");
        }
    }

    public void SetAlarm(Context context) {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, FetchMessagesBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        //After after 5 seconds
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 , pi);
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        //Ignore
    }

    public void onProviderEnabled(String provider) {
        //Ignore
    }

    public void onProviderDisabled(String provider) {
        //Ignore
    }

    public void onLocationChanged(android.location.Location location) {
        _latitude = location.getLatitude();
        _longitude = location.getLongitude();
    }

}