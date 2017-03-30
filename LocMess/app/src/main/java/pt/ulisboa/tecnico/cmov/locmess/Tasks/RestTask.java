package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.R;

/**
 * Created by joao on 3/29/17.
 */

public abstract class RestTask extends AsyncTask<Void, Void, String> {

    protected Activity _context;
    protected RestTemplate _rest;
    protected String _url;

    @Override
    protected void onPreExecute(){
        _rest = new RestTemplate();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new FormHttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter());
        messageConverters.add(new MappingJacksonHttpMessageConverter());
        _rest.setMessageConverters(messageConverters);
    }

    protected RestTask(Activity appContext){
        super();
        _context = appContext;
        _url = appContext.getResources().getString(R.string.serverURL);
    }

    @Override
    protected abstract String doInBackground(Void... params);

    @Override
    protected abstract void onPostExecute(String result);

}

