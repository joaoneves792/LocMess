package pt.ulisboa.tecnico.cmov.locmess;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.ulisboa.tecnico.cmov.locmess.Domain.DecentralizedMessage;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.LocationException;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.GetDecentralizedMessagesTask;

public class SimWifiP2pBroadcastReceiver extends BroadcastReceiver implements SimWifiP2pManager.PeerListListener {

    public static final int PORT = 10001;
    public static final String TAG = "WiFiDirect";

    Activity _homeActivity;
    Context _appContext;

    WifiReceiver _wifiScanReceiver;

    SimWifiP2pManager _manager;
    SimWifiP2pManager.Channel _channel;

    List<DecentralizedMessage> _pendingMessages;



    public SimWifiP2pBroadcastReceiver(){

    }

    public SimWifiP2pBroadcastReceiver(Activity activity, WifiReceiver wifi) {
        super();
        _homeActivity = activity;
        _appContext = _homeActivity.getApplicationContext();

        _wifiScanReceiver = wifi;

        //Enable Wifi Direct
        Intent intent = new Intent(_appContext, SimWifiP2pService.class);
        _appContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);


        /*Initialize the wifiDirect server*/
        Executor ex = Executors.newSingleThreadExecutor();
        new GetDecentralizedMessagesTask(_appContext).executeOnExecutor(ex);
    }


    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            _manager = new SimWifiP2pManager(new Messenger(service));
            _channel = _manager.initialize(_homeActivity.getApplication(), Looper.getMainLooper(), null);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            _manager = null;
            _channel = null;
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
//            int state = intent.getIntExtra(SimWifiP2pBroadcast.EXTRA_WIFI_STATE, -1);
//            if (state == SimWifiP2pBroadcast.WIFI_P2P_STATE_ENABLED) {
//        		Toast.makeText(_appContext, "WiFi Direct enabled",Toast.LENGTH_SHORT).show();
//            } else {
//        		//Toast.makeText(mActivity, "WiFi Direct disabled",Toast.LENGTH_SHORT).show();
//            }

        } else if (SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
        	Toast.makeText(_appContext, "Peer list changed",Toast.LENGTH_SHORT).show();
            _manager.requestPeers(_channel, this);

            deliverMessages();

        } else if (SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION.equals(action)) {
//        	SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
//        			SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
//        	ginfo.print();
//    		Toast.makeText(_appContext, "Network membership changed", Toast.LENGTH_SHORT).show();

        } else if (SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION.equals(action)) {
//        	SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
//        			SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
//        	ginfo.print();
//    		Toast.makeText(_appContext, "Group ownership changed", Toast.LENGTH_SHORT).show();

        }
    }

    private void deliverMessages(){
        LocalCache cache = LocalCache.getInstance(_appContext);
        List<DecentralizedMessage> myMessages = cache.getMyDecentralizedMessages();
        _pendingMessages = new ArrayList<>();
        for(DecentralizedMessage m : myMessages){
            try {
                if (m.canDeliver(_wifiScanReceiver.getSSIDs(), GPSLocationListener.getInstance(_appContext))) {
                    _pendingMessages.add(m);
                }
            }catch (LocationException e){
                Toast.makeText(_appContext, "Failed to retrieve GPS coordinates", Toast.LENGTH_SHORT).show();
            }catch (ParseException e){
                Log.e("DECENTRALIZEDMESSAGE", "Corrupted message!" + e.getMessage());
                Toast.makeText(_appContext, "Corrupted message!", Toast.LENGTH_SHORT).show();
            }
        }
        if(_pendingMessages.size() == 0)
            return;
        Log.e("DECENTRALIZEDMESSAGE", _pendingMessages.size()+ " ");
        if(_manager != null){
            _manager.requestPeers(_channel, this);
        }
    }

    public void onPeersAvailable(SimWifiP2pDeviceList devices) {
        ArrayList<String> peers = new ArrayList<>();

        // compile list of devices in range
        for (SimWifiP2pDevice device : devices.getDeviceList()) {
            peers.add(device.deviceName);
        }
        LocalCache.getInstance().setPeerList(peers);

        for(SimWifiP2pDevice device : devices.getDeviceList()){
            String ip = device.getVirtIp();
            if(null != _pendingMessages) {
                for (DecentralizedMessage message : _pendingMessages) {
                    Log.e("DECENTRALIZEDMESSAGE", "Sending a message to " + ip);
                    new OutgoingCommTask().execute(new MessageIPPair(ip, message));
                }
            }
        }

    }

    private class MessageIPPair{
        public String ip;
        public DecentralizedMessage decentralizedMessage;

        MessageIPPair(String ip, DecentralizedMessage message){
            this.ip=ip;
            this.decentralizedMessage = message;
        }
    }

    public class OutgoingCommTask extends AsyncTask<MessageIPPair, String, String> {

        @Override
        protected String doInBackground(MessageIPPair... params) {
            try {
                SimWifiP2pSocket cliSocket = new SimWifiP2pSocket(params[0].ip, PORT);

                byte[] message = params[0].decentralizedMessage.getRequest();

                OutputStream output = cliSocket.getOutputStream();
                output.write(message, 0, message.length);

                BufferedReader input = new BufferedReader(new InputStreamReader(cliSocket.getInputStream()));

                String response = input.readLine();
                //publishProgress(response);

                if(DecentralizedMessage.canSend(response)){
                    output.write(params[0].decentralizedMessage.getDeliverableMessage().serialize());
                }

                cliSocket.close();
                cliSocket = null;
            } catch (UnknownHostException e) {
                Log.e(TAG, "Unknown Host:" + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IO error:" + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... response){
        }
    }
}
