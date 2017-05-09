package pt.ulisboa.tecnico.cmov.locmess;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

import pt.ulisboa.tecnico.cmov.locmess.Exceptions.LocationException;

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
