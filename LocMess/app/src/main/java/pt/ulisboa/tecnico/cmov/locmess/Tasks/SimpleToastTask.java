package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.locmess.ServerCommands.ServerCommand;

/**
 * Created by joao on 3/29/17.
 */

public class SimpleToastTask extends AsyncTask<ServerCommand, Void, String> {

    Context _context;

    public SimpleToastTask(Context appContext){
        super();
        _context = appContext;
    }

    @Override
    protected String doInBackground(ServerCommand... commands){
        try {
            commands[0].doExecute();
            return commands[0].getResult();
        }catch (Exception e){
            Log.e("TAG", e.getClass().toString()+" : "+e.getMessage()+" : "+e.getStackTrace());
            return e.getClass().toString()+" : " +e.getMessage();

        }
    }

    @Override
    protected void onPostExecute(String result){
        Toast.makeText(_context, result, Toast.LENGTH_LONG).show();
    }

}

