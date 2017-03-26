package LocMess;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;


/**
 * Created by joao on 3/25/17.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class APITest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String URL = "http://localhost:";

    private static final String USERNAME = "spammer92";
    private static final String PASSWORD = "123456";

    private static final String WRONG_USERNAME = "notinterestedinspam";
    private static final String WRONG_PASSWORD = "spamsucks!";

    private static final String LOCATION_NAME = "Arco do Cego";
    private static final double LOCATION_LATITUDE = 38.7343829;
    private static final double LOCATION_LONGITUDE = -9.1403882;
    private static final double LOCATION_RADIUS = 20;

    @org.junit.Before
    public void setUp() throws Exception {
        clearState();
    }


    private void clearState()throws Exception{
       this.restTemplate.getForObject(URL+port+"/debug-clear-all", String.class);
    }

    private void assertFail(String response)throws Exception{
        JSONObject json = new JSONObject(response);
        Boolean success = (Boolean)json.get("successful");
        String errorMessage = (String)json.get("message");
        assertFalse(errorMessage, success);
    }

    private void assertSuccess(String response)throws Exception{
        JSONObject json = new JSONObject(response);
        Boolean success = (Boolean)json.get("successful");
        String errorMessage = (String)json.get("message");
        assertTrue(errorMessage, success);
    }

    private String register(String username, String password)throws Exception{
        return this.restTemplate.getForObject(URL + port + "/register?username="+username+"&password="+password, String.class);
    }

    private String login(String username, String password)throws Exception{
        return this.restTemplate.getForObject(URL + port + "/login?username="+username+"&password="+password, String.class);
    }

    private String logout(String id){
        return this.restTemplate.getForObject(URL + port + "/logout?id="+id, String.class);
    }

    private String testSession(String id){
        return this.restTemplate.getForObject(URL + port + "/test?id="+id, String.class);
    }


    private String addGPSLocation(String id, String name, Double latitude, Double longitude, Double radius){
        Map<String, String> vars = new HashMap<>();
        vars.put("id", id);
        vars.put("name", name);
        vars.put("latitude", latitude.toString());
        vars.put("longitude", longitude.toString());
        vars.put("radius", radius.toString());

        return this.restTemplate.postForObject(URL + port + "/createLocation", vars, String.class);
    }

    private String requestLocationList(String id){
        return this.restTemplate.getForObject(URL + port + "/listLocations?id="+id, String.class);
    }

    @Test
    public void contextLoads() throws Exception {
    }

    @Test
    public void testRegister()throws Exception{
        String response = register(USERNAME, PASSWORD);
        assertSuccess(response);
    }

    @Test
    public void testRegisterTwice()throws Exception{
       testRegister();
       assertFail(register(USERNAME, PASSWORD));
    }

    @Test
    public void loginTest()throws Exception{
        register(USERNAME, PASSWORD);
        String response = login(USERNAME, PASSWORD);
        assertSuccess(response);
    }

    @Test
    public void failedLogin()throws Exception{
        register(USERNAME, PASSWORD);
        String response = login(WRONG_USERNAME, PASSWORD);
        assertFail(response);
        response = login(USERNAME, WRONG_PASSWORD);
        assertFail(response);
        response = login(WRONG_USERNAME, WRONG_PASSWORD);
        assertFail(response);
    }

    @Test
    public void logoutTest()throws Exception{
        register(USERNAME, PASSWORD);
        String response = login(USERNAME, PASSWORD);
        JSONObject json = new JSONObject(response);
        String id = (json.get("sessionId")).toString();
        assertSuccess(testSession(id));
        assertSuccess(logout(id));
        assertFail(testSession(id));
    }

    @Test
    public void addLocationTest()throws Exception{
        register(USERNAME, PASSWORD);
        String response = login(USERNAME, PASSWORD);
        JSONObject json = new JSONObject(response);
        String id = (json.get("sessionId")).toString();
        assertSuccess(addGPSLocation(id, LOCATION_NAME, LOCATION_LATITUDE, LOCATION_LONGITUDE, LOCATION_RADIUS));

        String locationList = requestLocationList(id);
        assertSuccess(locationList);
        assertTrue(locationList.contains(LOCATION_NAME));
    }
}
