package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.cmov.locmess.Domain.DecentralizedMessage;
import pt.ulisboa.tecnico.cmov.locmess.Domain.DeliverableMessage;
import pt.ulisboa.tecnico.cmov.locmess.Domain.Location;
import pt.ulisboa.tecnico.cmov.locmess.Domain.Profile;
import pt.ulisboa.tecnico.cmov.locmess.LocalCache;
import pt.ulisboa.tecnico.cmov.locmess.SimWifiP2pBroadcastReceiver;

/**
 * Created by joao on 3/29/17.
 */

public class GetDecentralizedMessagesTask extends GetMessagesTask {

    private List<DeliverableMessage> _receivedMessages;

    SimWifiP2pSocketServer mSrvSocket;

    public GetDecentralizedMessagesTask(Context appContext) {
        super(appContext, -1, 0.0, 0.0, null);
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            mSrvSocket = new SimWifiP2pSocketServer(SimWifiP2pBroadcastReceiver.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if(null == mSrvSocket) {
                    Log.e("WifiDirect Server", "Failed to initialize server");
                    return null;
                }
                SimWifiP2pSocket sock = mSrvSocket.accept();
                try {
                    BufferedReader sockIn = new BufferedReader(
                            new InputStreamReader(sock.getInputStream()));
                    String st = sockIn.readLine();
                    Profile myProfile = LocalCache.getInstance(_appContext.getApplicationContext()).getStoredProfile();

                    String reply = DecentralizedMessage.getReply(st, myProfile);
                    sock.getOutputStream().write(reply.getBytes());
                    if(DecentralizedMessage.canSend(reply)){
                        String serializedMessage = sockIn.readLine();
                        this.publishProgress(serializedMessage);
                    }
                } catch (IOException e) {
                    Log.d("Error reading socket:", e.getMessage());
                } finally {
                    sock.close();
                }
            } catch (IOException e) {
                Log.d("Error socket:", e.getMessage());
                break;
                //e.printStackTrace();
            }
        }
        return null;
    }


    @Override
    protected void onProgressUpdate(String... values) {
        List<DeliverableMessage> receivedMessages = new LinkedList<>();
        for(String serializedMessage : values) {
            DeliverableMessage message = DeliverableMessage.deserialize(serializedMessage);
            receivedMessages.add(message);
        }
        handleMessage(receivedMessages);
    }


    @Override
    protected void onPostExecute(String result) {
        Log.e("SERVER", "FAILED");
    }
}

