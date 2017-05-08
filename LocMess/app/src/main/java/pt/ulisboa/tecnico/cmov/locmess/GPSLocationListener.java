package pt.ulisboa.tecnico.cmov.locmess;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.Exceptions.LocationException;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.GetMessagesTask;

/**
 * Created by joao on 5/7/17.
 */


public class GPSLocationListener  implements LocationListener{
    private static final int UPDATE_INTERVAL = 60*1000;
    private static final int UPDATE_MIN_DISTANCE_METERS = 10;
    public static final String GPS_TAG = "GPS";

    private static GPSLocationListener ourInstance;

    private static double _latitude;
    private static double _longitude;

    public static GPSLocationListener getInstance(Context context)throws LocationException{
        if(null == ourInstance){
            ourInstance = new GPSLocationListener(context);
        }
        return ourInstance;
    }

    GPSLocationListener(Context context)throws LocationException{
        _latitude = 0.0;
        _longitude = 0.0;

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //get GPS status
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            throw new LocationException("DISABLED");
        }
        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_INTERVAL, UPDATE_MIN_DISTANCE_METERS, this);
            android.location.Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(null == location){
                return;
            }else{
                _latitude = location.getLatitude();
                _longitude = location.getLongitude();
            }

        }catch (SecurityException e){
            throw new LocationException("SECURITY");
        }

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

    public double getLatitude(){
        return _latitude;
    }
    public double getLongitude(){
        return _longitude;
    }

}
