package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.app.Activity;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import pt.ulisboa.tecnico.cmov.locmess.Domain.Location;
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
