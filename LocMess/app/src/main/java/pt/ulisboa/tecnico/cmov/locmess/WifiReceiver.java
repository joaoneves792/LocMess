package pt.ulisboa.tecnico.cmov.locmess;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joao on 5/7/17.
 */

public class WifiReceiver extends BroadcastReceiver {
    private ArrayList<String> _ssids;

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
        if(null == _ssids){
            _ssids = new ArrayList<>();
        }
        return _ssids;
    }

};


