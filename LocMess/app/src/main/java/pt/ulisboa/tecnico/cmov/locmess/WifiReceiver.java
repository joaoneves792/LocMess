package pt.ulisboa.tecnico.cmov.locmess;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.Exceptions.LocationException;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.Responses.InterestsList;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.GetMessagesTask;

/**
 * Created by joao on 5/7/17.
 */

public class WifiReceiver extends BroadcastReceiver{
    private ArrayList<String> _ssids;

    WifiReceiver(){
        _ssids = new ArrayList<>();
    }

    @Override
    public void onReceive(Context c, Intent intent) {

        WifiManager wifi =(WifiManager) c.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        ArrayList<String> ssids = new ArrayList<>();
        List<ScanResult> wifiList;
        wifiList = wifi.getScanResults();
        for(ScanResult r : wifiList){
               ssids.add(r.SSID);
        }

        _ssids = ssids;
    }

    public ArrayList<String> getSSIDs(){
        return _ssids;
    }

};


