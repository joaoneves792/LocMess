package pt.ulisboa.tecnico.cmov.locmess.ServerCommands;

import org.springframework.web.client.RestTemplate;

/**
 * Created by joao on 3/28/17.
 */

public class LoginCommand extends ServerCommand{

    private String _username;
    private String _password;

    private String _result;

    public LoginCommand(String username, String password){
        _username = username;
        _password = password;
    }

    public void doExecute(){
        RestTemplate rest = new RestTemplate();
        _result = rest.getForObject("http://localhost:8080/login?username="+_username+"&password="+_password, String.class);
    }

    public String getResult(){
        return _result;
    }
}
