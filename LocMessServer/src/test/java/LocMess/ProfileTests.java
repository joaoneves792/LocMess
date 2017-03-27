package LocMess;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.SplittableRandom;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by joao on 3/26/17.
 */
public class ProfileTests extends LocMessTest {


    private final String INTEREST1_KEY = "job";
    private final String INTEREST1_VALUE = "Student";

    private final String INTEREST2_KEY = "club";
    private final String INTEREST2_VALUE = "SCP";


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
}
