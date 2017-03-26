package LocMess;

import org.json.JSONObject;
import org.junit.Test;

/**
 * Created by joao on 3/26/17.
 */
public class AuthenticationTests extends LocMessTest {

    private static final String WRONG_USERNAME = "notinterestedinspam";
    private static final String WRONG_PASSWORD = "spamsucks!";

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
        String id = loginGetId(USERNAME, PASSWORD);
        assertSuccess(testSession(id));
        assertSuccess(logout(id));
        assertFail(testSession(id));
    }
}
