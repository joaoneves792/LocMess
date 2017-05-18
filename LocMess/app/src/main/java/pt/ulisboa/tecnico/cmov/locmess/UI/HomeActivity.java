package pt.ulisboa.tecnico.cmov.locmess.UI;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.ulisboa.tecnico.cmov.locmess.DataManager;
import pt.ulisboa.tecnico.cmov.locmess.Domain.DeliverableMessage;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.LocationException;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.FetchMessagesBroadcastReceiver;
import pt.ulisboa.tecnico.cmov.locmess.GPSLocationListener;
import pt.ulisboa.tecnico.cmov.locmess.LocalCache;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.SimWifiP2pBroadcastReceiver;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.LogoutTask;
import pt.ulisboa.tecnico.cmov.locmess.WifiReceiver;

public class HomeActivity extends AppCompatActivity {

    private long _sessionId = 0;
    private String _username = "";

    private  ArrayList<NavItem> _navItems = new ArrayList<>();
    private DrawerLayout _drawerLayout;
    ListView _drawerList;
    RelativeLayout _drawerPane;
    private ActionBarDrawerToggle _drawerToggle;


    FetchMessagesBroadcastReceiver _fetchMessagesReceiver;
    WifiReceiver _wifiReceiver;
    SimWifiP2pBroadcastReceiver _wifiDirectReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /*Initialize the Message Cache*/
        LocalCache.getInstance(getApplicationContext());

        /*Setup burger menu*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _navItems.add(new NavItem(getString(R.string.post), getString(R.string.postMessage)));
        _navItems.add(new NavItem(getString(R.string.messages), getString(R.string.manageMessages)));
        _navItems.add(new NavItem(getString(R.string.locations), getString(R.string.manageLocations)));
        _navItems.add(new NavItem(getString(R.string.profile), getString(R.string.manageInterests)));
        _navItems.add(new NavItem(getString(R.string.settings), getString(R.string.appConfig)));

        // DrawerLayout
        _drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Populate the Navigtion Drawer with options
        _drawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        _drawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, _navItems);
        _drawerList.setAdapter(adapter);

        // Drawer Item click listeners
        _drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //selectItemFromDrawer(position);
                if(_navItems.get(position).mTitle.equals(getString(R.string.post))) {
                    postMessage(view);
                } else if(_navItems.get(position).mTitle.equals(getString(R.string.messages))) {
                    messages(view);
                } else if(_navItems.get(position).mTitle.equals(getString(R.string.locations))) {
                    locations(view);
                } else if(_navItems.get(position).mTitle.equals(getString(R.string.profile))) {
                    profile(view);
                } else if(_navItems.get(position).mTitle.equals(getString(R.string.settings))) {
                    settings(view);
                }
            }
        });

        _drawerToggle = new ActionBarDrawerToggle(this, _drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        _drawerLayout.setDrawerListener(_drawerToggle);

    }

    @Override
    protected void onResume(){
        super.onResume();
        DataManager dm = DataManager.getInstance();
        try {
            _sessionId = dm.getSessionId(getApplicationContext());
            _username = dm.getUsername(getApplicationContext());
            if(_username.equals("") || -1 == _sessionId){
                throw new StorageException();
            }
        }catch (StorageException e){
            Log.d(DataManager.STORAGE_TAG, "Failed to retrive session data!");
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
        ((TextView)findViewById(R.id.userName)).setText(_username);
        //Toast.makeText(this, new Long(getIntent().getLongExtra("SESSIONID", -1)).toString(), Toast.LENGTH_LONG).show();

        //setup the wifi scanner
        if(null == _wifiReceiver) {
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            _wifiReceiver = new WifiReceiver();
            getApplicationContext().registerReceiver(_wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            if (!wifi.isWifiEnabled()) {
                wifi.setWifiEnabled(true);
            }
//            LocalCache.getInstance().setWifiReceiver(_wifiReceiver);
        }
        // setup message fetching service
        if (_fetchMessagesReceiver == null){
            try{
                /*Initialize the GPS listener*/
                GPSLocationListener.getInstance(getApplicationContext());

            } catch (LocationException e) {
                Toast.makeText(this, "Failed initialize GPS", Toast.LENGTH_LONG).show();
            }
            /*Set Wifi receiver*/
            _fetchMessagesReceiver = new FetchMessagesBroadcastReceiver();

            _fetchMessagesReceiver.SetAlarm(getApplicationContext(), _wifiReceiver);
        }

        //Setup the decentralized message system
        if(_wifiDirectReceiver == null){
            // initialize the WDSim API
            SimWifiP2pSocketManager.Init(getApplicationContext());

            // register broadcast receiver
            IntentFilter filter = new IntentFilter();
            filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
            filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
            filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
            filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
            _wifiDirectReceiver = new SimWifiP2pBroadcastReceiver(this, _wifiReceiver);
            getApplicationContext().registerReceiver(_wifiDirectReceiver, filter);


        }

        // display messages on home
        ListView list = (ListView) findViewById(R.id.listViewMessages);

        List<DeliverableMessage> messages = LocalCache.getInstance().getMessages();

        ListAdapter messagesAdapter = new HomeActivity.MessagesListAdapter(this, messages);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sender = ((TextView) view.findViewById(R.id.textViewSender)).getText().toString();
                String location = ((TextView) view.findViewById(R.id.textViewLocation)).getText().toString();
                String message = ((TextView) view.findViewById(R.id.textViewMessageBody)).getText().toString();

                int senderLength = sender.getBytes().length;
                int locationLength = location.getBytes().length;
                int messageLength = message.getBytes().length;

                byte[] concatMessage = new byte[senderLength + locationLength + messageLength];
                System.arraycopy(sender.getBytes(), 0, concatMessage, 0, senderLength);
                System.arraycopy(location.getBytes(), 0, concatMessage, senderLength, locationLength);
                System.arraycopy(message.getBytes(), 0, concatMessage, senderLength + locationLength, messageLength);

                try {
                    MessageDigest md;
                    md = MessageDigest.getInstance("MD5");
                    md.update(concatMessage);
                    byte[] messageDigest = md.digest();
                    String hash = Base64.encodeToString(messageDigest, Base64.URL_SAFE | Base64.NO_WRAP | Base64.DEFAULT);

                    Intent intent = new Intent(getApplicationContext(), MessageViewActivity.class);
                    intent.putExtra(MessageViewActivity.MESSAGE_ID, hash);
                    startActivity(intent);

                } catch (NoSuchAlgorithmException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });

        list.setAdapter(messagesAdapter);



    }


    class NavItem {
        String mTitle;
        String mSubtitle;

        public NavItem(String title, String subtitle) {
            mTitle = title;
            mSubtitle = subtitle;
        }
    }

    class DrawerListAdapter extends BaseAdapter {

        Context mContext;
        ArrayList<NavItem> mNavItems;

        public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
            mContext = context;
            mNavItems = navItems;
        }

        @Override
        public int getCount() {
            return mNavItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mNavItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.drawer_item, null);
            }
            else {
                view = convertView;
            }

            TextView titleView = (TextView) view.findViewById(R.id.title);
            TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);
//            ImageView iconView = (ImageView) view.findViewById(R.id.icon);

            titleView.setText( mNavItems.get(position).mTitle );
            subtitleView.setText( mNavItems.get(position).mSubtitle );
            //iconView.setImageResource(mNavItems.get(position).mIcon);

            return view;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (_drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        _drawerToggle.syncState();
    }



    public void locations(View view) {
        Intent intent = new Intent(this, LocationsActivity.class);
        startActivity(intent);
    }

    public void messages(View view) {
        Intent intent = new Intent(this, MessagesActivity.class);
        startActivity(intent);
    }

    public void postMessage(View view) {
        Intent intent = new Intent(this, PostMessageActivity.class);
        startActivity(intent);
    }

    public void profile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void settings(View view) {
        Toast.makeText(this, "not implemented", Toast.LENGTH_LONG).show();
    }

    public void logout(View view){
        (new LogoutTask(this, _sessionId)).execute();
    }


    // class for the custom display of messages
    protected class MessagesListAdapter extends ArrayAdapter<DeliverableMessage> {

        public MessagesListAdapter(@NonNull Context context, List<DeliverableMessage> messages) {
            super(context, R.layout.posted_message_item, messages);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater messageInflater = LayoutInflater.from(getContext());
            View rowView = messageInflater.inflate(R.layout.posted_message_item, parent, false);

            DeliverableMessage message = getItem(position);
            TextView sender = (TextView) rowView.findViewById(R.id.textViewSender);
            TextView location = (TextView) rowView.findViewById(R.id.textViewLocation);
            TextView publishDate = (TextView) rowView.findViewById(R.id.textViewPublishDate);
            TextView messageBody = (TextView) rowView.findViewById(R.id.textViewMessageBody);

            sender.setText(message.getSender());
            location.setText(message.getLocation());
            publishDate.setText(message.getPublicationDate());
            messageBody.setText(message.getMessage());

            return rowView;
        }

    }


}
