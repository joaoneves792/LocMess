package pt.ulisboa.tecnico.cmov.locmess;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Handler;

import pt.ulisboa.tecnico.cmov.locmess.Domain.Location;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.LocationException;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.GetMessagesTask;

/**
 * Created by joao on 5/7/17.
 */

public class FetchMessagesBroadcastReceiver extends BroadcastReceiver{
    private static WifiReceiver wifiReceiver;
    @Override
    public void onReceive(Context context, Intent intent) {

        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi.startScan();

        Intent i = new Intent(context, FetchMessagesService.class);
        i.putExtra(FetchMessagesService.SSID_ARRAY, wifiReceiver.getSSIDs()); /*We might get delayed results but its impossible to syncronize and still work on the emulator*/
        context.getApplicationContext().startService(i);
    }


    public void SetAlarm(Context context)throws LocationException{
        /*Initialize the GPS listener*/
        GPSLocationListener.getInstance(context);

        /*Set Wifi receiver*/
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();
        context.getApplicationContext().registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        if(!wifi.isWifiEnabled())
        {
            wifi.setWifiEnabled(true);
        }

        /*Set up the Intent for this class periodicaly*/
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, FetchMessagesBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        //After after 5 seconds
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 , pi);

    }
}
