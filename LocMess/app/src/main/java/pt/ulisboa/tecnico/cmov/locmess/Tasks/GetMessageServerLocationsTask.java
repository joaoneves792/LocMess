package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.Domain.GPSLocation;
import pt.ulisboa.tecnico.cmov.locmess.Domain.Location;
import pt.ulisboa.tecnico.cmov.locmess.Domain.WiFiLocation;
import pt.ulisboa.tecnico.cmov.locmess.MessageViewActivity;
import pt.ulisboa.tecnico.cmov.locmess.R;

public class GetMessageServerLocationsTask extends GetLocationsTask{


    public GetMessageServerLocationsTask(Activity appContext, long sessionId){
        super(appContext, sessionId);
    }

    @Override
    protected void onPostExecute(String result){

        // clear and repopulate locations list
        RadioGroup radioGroup = (RadioGroup) _context.findViewById(R.id.radioGroupLocations);
        radioGroup.removeAllViews();

        int i = 0;
        RadioButton button;
        for(Location location : _locations) {
            button = new RadioButton(_context);
            button.setId(i++);
            button.setText(location.getName());
            radioGroup.addView(button);
        }
    }

}
