package LocMess;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by joao on 3/26/17.
 */
public class ProfileTests extends LocMessTest {


    protected static final String INTEREST1_KEY = "job";
    protected static final String INTEREST1_VALUE = "Student";
    protected static final String INTEREST1_DIFFERENT_VALUE = "Clown";

    protected static final String INTEREST2_KEY = "club";
    protected static final String INTEREST2_VALUE = "SCP";
    protected static final String INTEREST2_DIFFERENT_VALUE = "SLB";

    @Test
    public void addInterestTest()throws Exception{
        register(USERNAME, PASSWORD);
        String id = loginGetId(USERNAME, PASSWORD);

        addInterest(id, INTEREST1_KEY, INTEREST1_VALUE);
        String response = getInterests(id);
        assertSuccess(response);
        assertTrue(response.contains(INTEREST1_KEY));
        assertTrue(response.contains(INTEREST1_VALUE));
    }

    @Test
    public void addMultipleInterestsTest()throws Exception{
        addInterestTest();
        String id = loginGetId(USERNAME, PASSWORD);

        addInterest(id, INTEREST2_KEY, INTEREST2_VALUE);
        String response = getInterests(id);
        assertSuccess(response);
        assertTrue(response.contains(INTEREST1_KEY));
        assertTrue(response.contains(INTEREST1_VALUE));

        assertTrue(response.contains(INTEREST2_KEY));
        assertTrue(response.contains(INTEREST2_VALUE));

        /*JSONObject json = new JSONObject(response);
        System.out.print(json.get("interests").toString());*/
    }

    @Test
    public void deleteInterestTest()throws Exception{
        addMultipleInterestsTest();
        String id = loginGetId(USERNAME, PASSWORD);

        deleteInterest(id, INTEREST1_KEY);
        String response = getInterests(id);

        assertSuccess(response);
        assertFalse(response.contains(INTEREST1_KEY));
        assertFalse(response.contains(INTEREST1_VALUE));

        assertTrue(response.contains(INTEREST2_KEY));
        assertTrue(response.contains(INTEREST2_VALUE));

        deleteInterest(id, INTEREST2_KEY);
        response = getInterests(id);

        assertSuccess(response);
        assertFalse(response.contains(INTEREST1_KEY));
        assertFalse(response.contains(INTEREST1_VALUE));

        assertFalse(response.contains(INTEREST2_KEY));
        assertFalse(response.contains(INTEREST2_VALUE));
    }

    @Test
    public void globalInterestsTest()throws Exception{
        addMultipleInterestsTest();
        String id = loginGetId(USERNAME, PASSWORD);

        String response = listAllInterests(id);
        assertSuccess(response);

        assertTrue(response.contains(INTEREST1_KEY));
        assertFalse(response.contains(INTEREST1_VALUE));

        assertTrue(response.contains(INTEREST2_KEY));
        assertFalse(response.contains(INTEREST2_VALUE));

        JSONArray json = (JSONArray)new JSONObject(response).get("keys");
        assertEquals(2, json.length());

        deleteInterest(id, INTEREST1_KEY);
        response = listAllInterests(id);

        assertFalse(response.contains(INTEREST1_KEY));
        assertTrue(response.contains(INTEREST2_KEY));

        json = (JSONArray)new JSONObject(response).get("keys");
        assertEquals(1, json.length());

        deleteInterest(id, INTEREST2_KEY);
        response = listAllInterests(id);

        assertFalse(response.contains(INTEREST1_KEY));
        assertFalse(response.contains(INTEREST2_KEY));

        json = (JSONArray)new JSONObject(response).get("keys");
        assertEquals(0, json.length());
    }

    @Test
    public void multipleUsersInterestsTest()throws Exception{
        addMultipleInterestsTest();
        register(USERNAME2, PASSWORD2);

        String id1 = loginGetId(USERNAME, PASSWORD);
        String id2 = loginGetId(USERNAME2, PASSWORD2);

        addInterest(id2, INTEREST1_KEY, INTEREST1_DIFFERENT_VALUE);

        String response = getInterests(id1);

        assertSuccess(response);
        assertTrue(response.contains(INTEREST1_KEY));
        assertTrue(response.contains(INTEREST1_VALUE));
        assertTrue(response.contains(INTEREST2_KEY));
        assertTrue(response.contains(INTEREST2_VALUE));

        response = getInterests(id2);
        assertSuccess(response);
        assertTrue(response.contains(INTEREST1_KEY));
        assertTrue(response.contains(INTEREST1_DIFFERENT_VALUE));

        response = listAllInterests(id1);
        assertTrue(response.contains(INTEREST1_KEY));
        assertTrue(response.contains(INTEREST2_KEY));

        JSONArray json = (JSONArray)new JSONObject(response).get("keys");
        assertEquals(2, json.length());

    }
}
