package pt.ulisboa.tecnico.cmov.locmess;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;

/**
 * Created by joao on 5/10/17.
 */

public class PeerManager implements SimWifiP2pManager.PeerListListener {

    public static final int PORT = 10001;
    public static final String TAG = "PEER_MANAGER";

    SimWifiP2pManager p2pManager;
    SimWifiP2pManager.Channel p2pChannel;

    PeerManager(SimWifiP2pManager manager, SimWifiP2pManager.Channel channel){
        p2pChannel = channel;
        p2pManager = manager;
    }

    public void onPeersAvailable(SimWifiP2pDeviceList devices){
        for(SimWifiP2pDevice device : devices.getDeviceList()){
            String ip = device.getVirtIp();
            new OutgoingCommTask().execute(ip);
        }

    }
    public class OutgoingCommTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                SimWifiP2pSocket cliSocket = new SimWifiP2pSocket(params[0], PORT);
                byte[] message = "HELLO WORLD\n".getBytes();
                cliSocket.getOutputStream().write(message, 0, message.length);
                new BufferedReader(new InputStreamReader(cliSocket.getInputStream())).readLine();
                cliSocket.close();
                cliSocket = null;
            } catch (UnknownHostException e) {
                Log.e(TAG, "Unknown Host:" + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IO error:" + e.getMessage());
            }
            return null;
        }
    }
}
