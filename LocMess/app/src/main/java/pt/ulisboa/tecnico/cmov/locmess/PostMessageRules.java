package pt.ulisboa.tecnico.cmov.locmess;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pt.ulisboa.tecnico.cmov.locmess.Domain.Location;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.GetLocationsTask;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.PostMessageTask;

public class PostMessageRules extends AppCompatActivity {

    private long _sessionId = 0;

    private ArrayList<Map.Entry<String,String>> _ruleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            _sessionId = DataManager.getInstance().getSessionId(getApplicationContext());
        }catch (StorageException e){
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

        String title = previousIntent.getStringExtra("TITLE");
        String text = previousIntent.getStringExtra("TEXT");
        String location = previousIntent.getStringExtra("LOCATION");
        String startdate = previousIntent.getStringExtra("STARTDATE");
        String starttime = previousIntent.getStringExtra("STARTTIME");
        String enddate = previousIntent.getStringExtra("ENDDATE");
        String endtime = previousIntent.getStringExtra("ENDTIME");

        (new PostMessageTask(this, _sessionId, location, null, whitelist, starttime+"-"+startdate, endtime+"-"+enddate, text)).execute();

        finish();
    }


    public void addRule(View view){

        _ruleList.add(new AbstractMap.SimpleEntry<>("", ""));

        ListView list = (ListView) findViewById(R.id.listViewRules);
        ListAdapter locationsAdapter = new RuleListAdapter(this, _ruleList);

//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(this, MessageViewActivity.class);
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
