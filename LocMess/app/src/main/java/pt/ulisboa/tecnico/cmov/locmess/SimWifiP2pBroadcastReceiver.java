package pt.ulisboa.tecnico.cmov.locmess;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;

public class SimWifiP2pBroadcastReceiver extends BroadcastReceiver {

    Activity _homeActivity;
    Context _appContext;

    SimWifiP2pManager mManager;
    boolean mBound;
    SimWifiP2pManager.Channel mChannel;
    PeerManager _peerManager;


    public SimWifiP2pBroadcastReceiver(){

    }

    public SimWifiP2pBroadcastReceiver(Activity activity) {
        super();
        _homeActivity = activity;
        _appContext = _homeActivity.getApplicationContext();

        //Enable Wifi Direct
        Intent intent = new Intent(_appContext, SimWifiP2pService.class);
        _appContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mBound = true;


    }


    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mManager = new SimWifiP2pManager(new Messenger(service));
            mChannel = mManager.initialize(_homeActivity.getApplication(), Looper.getMainLooper(), null);
            _peerManager = new PeerManager(mManager, mChannel);
            mBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mManager = null;
            mChannel = null;
            mBound = false;
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            /*
            int state = intent.getIntExtra(SimWifiP2pBroadcast.EXTRA_WIFI_STATE, -1);
            if (state == SimWifiP2pBroadcast.WIFI_P2P_STATE_ENABLED) {
        		Toast.makeText(_appContext, "WiFi Direct enabled",Toast.LENGTH_SHORT).show();
            } else {
        		//Toast.makeText(mActivity, "WiFi Direct disabled",Toast.LENGTH_SHORT).show();
            }
            */
        } else if (SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
        	//Toast.makeText(_appContext, "Peer list changed",Toast.LENGTH_SHORT).show();
            if(mManager != null){
                mManager.requestPeers(mChannel, _peerManager);
            }

        } else if (SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION.equals(action)) {
            /*
        	SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
        			SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
        	ginfo.print();
    		Toast.makeText(_appContext, "Network membership changed", Toast.LENGTH_SHORT).show();
            */
        } else if (SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION.equals(action)) {
            /*
        	SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
        			SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
        	ginfo.print();
    		Toast.makeText(_appContext, "Group ownership changed", Toast.LENGTH_SHORT).show();
            */
        }
    }
}
