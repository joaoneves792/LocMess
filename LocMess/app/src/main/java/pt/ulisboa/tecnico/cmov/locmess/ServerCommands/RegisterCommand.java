package pt.ulisboa.tecnico.cmov.locmess.ServerCommands;

import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.Responses.Response;

/**
 * Created by joao on 3/28/17.
 */

public class RegisterCommand extends ServerCommand{

    private String _username;
    private String _password;

    private String _result;

    public RegisterCommand(String username, String password){
        _username = username;
        _password = password;
    }

    public void doExecute(){
        RestTemplate rest = new RestTemplate();
        //rest.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new FormHttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter());
        messageConverters.add(new MappingJacksonHttpMessageConverter());
        rest.setMessageConverters(messageConverters);
        _result = rest.getForObject("http://194.210.159.216:8080/register?username="+_username+"&password="+_password, String.class);
    }

    public String getResult(){
        return _result;
    }
}
