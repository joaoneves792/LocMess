package LocMess;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

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

    @org.junit.Before
    public void setUp() throws Exception {
        clearState();
    }


    private void clearState()throws Exception{
       this.restTemplate.getForObject(URL+port+"/debug-clear-all", String.class);
    }

    private String register(String username, String password)throws Exception{
        return this.restTemplate.getForObject(URL + port + "/register?username="+username+"&password="+password, String.class);
    }

    private String login(String username, String password)throws Exception{
        return this.restTemplate.getForObject(URL + port + "/login?username="+username+"&password="+password, String.class);
    }

    @Test
    public void contextLoads() throws Exception {
    }

    @Test
    public void testRegister()throws Exception{
        String response = register(USERNAME, PASSWORD);
        assertFalse("Register FAILED!", response.contains("false"));
    }

    @Test
    public void loginTest()throws Exception{
        register(USERNAME, PASSWORD);
        String response = login(USERNAME, PASSWORD);
        assertFalse("Login FAILED!", response.contains("false"));
    }

    @Test
    public void failedLogin()throws Exception{
        register(USERNAME, PASSWORD);
        String response = login(WRONG_USERNAME, PASSWORD);
        assertTrue("Login accepted unregistered user", response.contains("false"));
        response = login(USERNAME, WRONG_PASSWORD);
        assertTrue("Login accepted wrong password", response.contains("false"));
        response = login(WRONG_USERNAME, WRONG_PASSWORD);
        assertTrue("Login accepted wrong username and password", response.contains("false"));
    }
}
