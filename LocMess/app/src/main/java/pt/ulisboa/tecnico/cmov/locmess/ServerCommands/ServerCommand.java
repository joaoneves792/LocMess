package pt.ulisboa.tecnico.cmov.locmess.ServerCommands;

import android.os.AsyncTask;

/**
 * Created by joao on 3/28/17.
 */

public abstract class ServerCommand{

    public abstract void doExecute();

    public abstract String getResult();
}
