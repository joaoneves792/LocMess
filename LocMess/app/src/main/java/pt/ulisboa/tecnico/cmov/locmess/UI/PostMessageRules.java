package pt.ulisboa.tecnico.cmov.locmess.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ulisboa.tecnico.cmov.locmess.DataManager;
import pt.ulisboa.tecnico.cmov.locmess.Domain.DecentralizedMessage;
import pt.ulisboa.tecnico.cmov.locmess.Domain.DeliverableMessage;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.LocalCache;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.PostMessageTask;

public class PostMessageRules extends AppCompatActivity {

    private long _sessionId = 0;

    private ArrayList<Map.Entry<String,String>> _ruleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            _sessionId = DataManager.getInstance().getSessionId(getApplicationContext());
        } catch (StorageException e) {
            Toast.makeText(this, R.string.sessionFailed, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }

        _ruleList = new ArrayList<>();

        setContentView(R.layout.activity_post_message_rules);

    }


    public void cancel(View view) {
        finish();
    }

    public void postMessage(View view) {

        // get message distribution policy (blacklist default)
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        final int checkedId = radioGroup.getCheckedRadioButtonId();
        boolean whitelist = false;
        switch(checkedId) {
            case R.id.radioButtonWhitelist:
                whitelist = true;
                break;
            case R.id.radioButtonBlacklist:
                whitelist = false;
                break;
        }

        Intent previousIntent = getIntent();
        String text = previousIntent.getStringExtra("TEXT");
        String location = previousIntent.getStringExtra("LOCATION");
        String startDateTime = previousIntent.getStringExtra("STARTDATETIME");
        String endDateTime = previousIntent.getStringExtra("ENDDATETIME");
        String decentralized = previousIntent.getStringExtra("DELIVERY_MODE");

        Map<String, String> rules = new HashMap<>();
        for(Map.Entry<String, String> entry : _ruleList){
            rules.put(entry.getKey(), entry.getValue());
        }
        if(rules.size() == 0){
            rules = null;
        }

        if (decentralized.equals("centralized")) {
            // send message to server
            (new PostMessageTask(this, _sessionId, location, null, whitelist, startDateTime, endDateTime, text)).execute();

        } else {
            try{
                String sender = DataManager.getInstance().getUsername(getApplicationContext());
                DecentralizedMessage decentralizedMessage = new DecentralizedMessage(sender, location, text, startDateTime, endDateTime, whitelist, rules);
                LocalCache.getInstance(getApplicationContext()).insertIntoStorage(decentralizedMessage);
                List<DeliverableMessage> messages = new ArrayList<>();
                messages.add(decentralizedMessage.getDeliverableMessage());
                LocalCache.getInstance(getApplicationContext()).storeMessages(messages);
            }catch (StorageException e){
                Toast.makeText(this, "Failed to create Decentralized message.", Toast.LENGTH_SHORT).show();
            }
        }


        finish();
    }


    public void addRule(View view){

        _ruleList.add(new AbstractMap.SimpleEntry<>("", ""));

        ListView list = (ListView) findViewById(R.id.listViewRules);
        ListAdapter locationsAdapter = new RuleListAdapter(this, _ruleList);

//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(this, InterestViewActivity.class);
//                startActivity(intent);
//            }
//        });

        list.setAdapter(locationsAdapter);
    }

    // class for the custom display of message rules' list
    protected class RuleListAdapter extends ArrayAdapter<Map.Entry<String,String>> {

        public RuleListAdapter(@NonNull Context context, ArrayList<Map.Entry<String,String>> rules) {
            super(context, R.layout.rule_item, rules);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater interestInflater = LayoutInflater.from(getContext());
            View rowView = interestInflater.inflate(R.layout.rule_item, parent, false);

            Map.Entry<String,String> rule = getItem(position);
            TextView key = (TextView) rowView.findViewById(R.id.textViewKey);
            TextView value = (TextView) rowView.findViewById(R.id.textViewValue);

            key.setText(rule.getKey());
            value.setText(rule.getValue());

            return rowView;
        }

    }



}
