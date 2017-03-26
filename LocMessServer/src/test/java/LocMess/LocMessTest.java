package LocMess;

import org.json.JSONObject;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;


/**
 * Created by joao on 3/25/17.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class LocMessTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String URL = "http://localhost:";


    public static final String USERNAME = "spammer92";
    public static final String PASSWORD = "123456";


    @org.junit.Before
    public void setUp() throws Exception {
        clearState();
    }


    protected void clearState()throws Exception{
       this.restTemplate.getForObject(URL+port+"/debug-clear-all", String.class);
    }

    protected void assertFail(String response)throws Exception{
        JSONObject json = new JSONObject(response);
        Boolean success = (Boolean)json.get("successful");
        String errorMessage = (String)json.get("message");
        assertFalse(errorMessage, success);
    }

    protected void assertSuccess(String response)throws Exception{
        JSONObject json = new JSONObject(response);
        Boolean success = (Boolean)json.get("successful");
        String errorMessage = (String)json.get("message");
        assertTrue(errorMessage, success);
    }

    protected String register(String username, String password)throws Exception{
        return this.restTemplate.getForObject(URL + port + "/register?username="+username+"&password="+password, String.class);
    }

    protected String login(String username, String password)throws Exception{
        return this.restTemplate.getForObject(URL + port + "/login?username="+username+"&password="+password, String.class);
    }

    protected String loginGetId(String username, String password)throws Exception{
        String response = this.restTemplate.getForObject(URL + port + "/login?username="+username+"&password="+password, String.class);
        JSONObject json = new JSONObject(response);
        return (json.get("sessionId")).toString();
    }


    protected String logout(String id){
        return this.restTemplate.getForObject(URL + port + "/logout?id="+id, String.class);
    }

    protected String testSession(String id){
        return this.restTemplate.getForObject(URL + port + "/test?id="+id, String.class);
    }


    protected String addGPSLocation(String id, String name, Double latitude, Double longitude, Double radius){
        Map<String, String> vars = new HashMap<>();
        vars.put("id", id);
        vars.put("name", name);
        vars.put("latitude", latitude.toString());
        vars.put("longitude", longitude.toString());
        vars.put("radius", radius.toString());

        return this.restTemplate.postForObject(URL + port + "/createLocation", vars, String.class);
    }

    protected String requestLocationList(String id){
        return this.restTemplate.getForObject(URL + port + "/listLocations?id="+id, String.class);
    }


}
