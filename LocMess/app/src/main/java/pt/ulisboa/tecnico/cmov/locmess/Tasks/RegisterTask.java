package pt.ulisboa.tecnico.cmov.locmess.Tasks;

 import android.app.Activity;
 import android.content.Context;
 import android.content.Intent;
 import android.util.Log;
 import android.widget.Toast;

 import org.codehaus.jackson.map.ObjectMapper;
 import org.springframework.web.client.RestClientException;

 import java.io.IOException;

 import pt.ulisboa.tecnico.cmov.locmess.HomeActivity;
 import pt.ulisboa.tecnico.cmov.locmess.LoginActivity;
 import pt.ulisboa.tecnico.cmov.locmess.Responses.Response;

/**
 * Created by joao on 3/29/17.
 */

public class RegisterTask extends RestTask{

    private String _username;
    private String _password;

    private boolean _successful;

    public RegisterTask(Activity appContext, String username, String password){
        super(appContext);
        _username = username;
        _password = password;
    }

    @Override
    protected String doInBackground(Void... params){
        String result;
        try {
            result = _rest.getForObject(_url+"/register?username=" + _username + "&password=" + _password, String.class);

            _successful = false;
            ObjectMapper mapper = new ObjectMapper();
            Response simpleResponse = mapper.readValue(result, Response.class);
            _successful = simpleResponse.getSuccessful();

            return simpleResponse.getMessage();

        }catch (RestClientException e){
            Log.e("REST ERROR", e.getClass().toString()+" : "+e.getMessage());
            return e.getMessage();
        }catch (IOException e){
            Log.e("JSON ERROR", e.getMessage());
            _successful = false;
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result){
        Toast.makeText(_context, result, Toast.LENGTH_SHORT).show();
        if(_successful) {
            Intent intent = new Intent(_context, LoginActivity.class);
            intent.putExtra("USERNAME", _username);
            intent.putExtra("PASSWORD", _password);
            _context.startActivity(intent);
        }
    }

}

