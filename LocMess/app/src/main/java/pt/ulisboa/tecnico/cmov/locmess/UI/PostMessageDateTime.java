package pt.ulisboa.tecnico.cmov.locmess.UI;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import pt.ulisboa.tecnico.cmov.locmess.DataManager;
import pt.ulisboa.tecnico.cmov.locmess.Exceptions.StorageException;
import pt.ulisboa.tecnico.cmov.locmess.R;

public class PostMessageDateTime extends AppCompatActivity
//        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
{

    private long _sessionId = 0;

    private int _startY, _startM, _startD, _startH, _startMin;
    private int _endY, _endM, _endD, _endH, _endMin;

    private DatePickerDialog.OnDateSetListener _startDateListener, _endDateListener;
    private TimePickerDialog.OnTimeSetListener _startTimeListener, _endTimeListener;

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

        setContentView(R.layout.activity_post_message_datetime);

        // setup the listeners
        _startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                _startY = year;
                _startM = month;
                _startD = dayOfMonth;

                TimePickerDialog timePickerDialog = new TimePickerDialog(PostMessageDateTime.this, _startTimeListener, _startH, _startMin, true);
                timePickerDialog.show();
            }
        };

        _endDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                _endY = year;
                _endM = month;
                _endD = dayOfMonth;

                TimePickerDialog timePickerDialog = new TimePickerDialog(PostMessageDateTime.this, _endTimeListener, _endH, _endMin, true);
                timePickerDialog.show();
            }
        };

        _startTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                _startH = hourOfDay;
                _startMin = minute;

                TextView startDateTime = (TextView) findViewById(R.id.textViewStartDateTime);
                startDateTime.setText(_startH + ":" + String.format("%02d", _startMin)
                        + " " + String.format("%02d", _startD) + "/" + String.format("%02d", _startM+1) + "/"+ _startY);

//                // update of end date/time
//                _endY = _startY;
//                _endM = _startM;
//                _endD = _startD + 1;
//                _endH = _startH;
//                _endMin = _startMin;
//
//                TextView endDateTime = (TextView) findViewById(R.id.textViewEndDateTime);
//                endDateTime.setText(_endH + ":" + String.format("%02d", _endMin)
//                        + " " + String.format("%02d", _endD) + "/" + String.format("%02d", _endM+1) + "/"+ _endY);
            }
        };
        _endTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                _endH = hourOfDay;
                _endMin = minute;

                TextView startDateTime = (TextView) findViewById(R.id.textViewEndDateTime);
                startDateTime.setText(_endH + ":" + String.format("%02d", _endMin)
                        + " " + String.format("%02d", _endD) + "/" + String.format("%02d", _endM+1) + "/"+ _endY);
            }
        };


        // default values for the start date/time
        Calendar calendar = Calendar.getInstance();
        _startY = calendar.get(Calendar.YEAR);
        _startM = calendar.get(Calendar.MONTH);
        _startD = calendar.get(Calendar.DAY_OF_MONTH);
        _startH = calendar.get(Calendar.HOUR_OF_DAY);
        _startMin = calendar.get(Calendar.MINUTE);

        TextView startDateTime = (TextView) findViewById(R.id.textViewStartDateTime);
        startDateTime.setText(_startH + ":" + String.format("%02d", _startMin)
                + " " + String.format("%02d", _startD) + "/" + String.format("%02d", _startM+1) + "/"+ _startY);

        // default values for the end date/time
        _endY = _startY;
        _endM = _startM;
        _endD = _startD + 1;
        _endH = _startH;
        _endMin = _startMin;

        TextView endDateTime = (TextView) findViewById(R.id.textViewEndDateTime);
        endDateTime.setText(_endH + ":" + String.format("%02d", _endMin)
                + " " + String.format("%02d", _endD) + "/" + String.format("%02d", _endM+1) + "/"+ _endY);

        Button startDateTimeButton = (Button) findViewById(R.id.buttonSetStart);
        startDateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(PostMessageDateTime.this, _startDateListener, _startY, _startM, _startD);
                datePickerDialog.show();
            }
        });

        Button endDateTimeButton = (Button) findViewById(R.id.buttonSetEnd);
        endDateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(PostMessageDateTime.this, _endDateListener, _endY, _endM, _endD);
                datePickerDialog.show();
            }
        });

    }

    public void cancel(View view) {
        finish();
    }

    public void selectRules(View view) {

        String startDateTime = _startH + ":" + String.format("%02d", _startMin)
                + "-" + String.format("%02d", _startM+1) + "/" + String.format("%02d", _startD) + "/" + _startY;

        String endDateTime = _endH + ":" + String.format("%02d", _endMin)
                + "-" + String.format("%02d", _endM+1) + "/" + String.format("%02d", _endD) + "/" + _endY ;

        //FIXME verify end date > start date

        Intent previousIntent = getIntent();
        Intent intent = new Intent(this, PostMessageRules.class);

//        intent.putExtra("TITLE", previousIntent.getStringExtra("TITLE"));
        intent.putExtra("TEXT", previousIntent.getStringExtra("TEXT"));
        intent.putExtra("DELIVERY_MODE", previousIntent.getStringExtra("DELIVERY_MODE"));
        intent.putExtra("LOCATION", previousIntent.getStringExtra("LOCATION"));
        intent.putExtra("STARTDATETIME", startDateTime);
        intent.putExtra("ENDDATETIME", endDateTime);
        startActivity(intent);

        finish();
    }

}
