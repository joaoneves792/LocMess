package LocMess;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static junit.framework.TestCase.*;

/**
 * Created by joao on 3/26/17.
 */
public class MessageTests extends LocMessTest {

    private String today;
    private String tomorrow;
    private String yesterday;
    private String overtomorrow;

    private final String MESSAGE1 = "Programming skills enhancing pills! Now available for just 100$. Call 934363257";
    private final String MESSAGE2 = "SLB sucks!";
    private final String MESSAGE3 = "SLB rules!";

    @Override
    @org.junit.Before
    public void setUp()throws Exception{
        clearState();
        register(USERNAME, PASSWORD);
        register(USERNAME2, PASSWORD2);

        String id1 = loginGetId(USERNAME, PASSWORD);
        addInterest(id1, ProfileTests.INTEREST1_KEY, ProfileTests.INTEREST1_VALUE);
        addGPSLocation(id1, LocationTests.LOCATION_NAME, LocationTests.LOCATION_LATITUDE, LocationTests.LOCATION_LONGITUDE, LocationTests.LOCATION_RADIUS);
        List<String> ssids = Arrays.asList(LocationTests.SSID1, LocationTests.SSID2);
        addWifiLocation(id1, LocationTests.WIFI_LOCATION_NAME, ssids);

        String id2 = loginGetId(USERNAME2, PASSWORD2);
        addInterest(id2, ProfileTests.INTEREST1_KEY, ProfileTests.INTEREST1_DIFFERENT_VALUE);
        addInterest(id2, ProfileTests.INTEREST2_KEY, ProfileTests.INTEREST2_DIFFERENT_VALUE);

        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate now = LocalDate.now(ZoneId.of("Europe/Lisbon"));
        LocalDateTime todayDate = LocalDateTime.of(now, midnight);
        LocalDateTime tomorrowDate = todayDate.plusDays(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm-MM/dd/yyyy", Locale.ENGLISH); //Example 14:30-12/24/2017
        today = todayDate.format(formatter);
        tomorrow = tomorrowDate.format(formatter);
        yesterday = todayDate.minusDays(1).format(formatter);
        overtomorrow = tomorrowDate.plusDays(1).format(formatter);

    }

    @Test
    public void postMessageTest()throws Exception{
        String id = loginGetId(USERNAME, PASSWORD);

        String response = postMessage(id, LocationTests.LOCATION_NAME, null, false, today, tomorrow, MESSAGE1);
        assertSuccess(response);

        response = getUserMessages(id);
        assertSuccess(response);
        assertTrue(response.contains(MESSAGE1));

        response = getMessages(id, LocationTests.LOCATION_LATITUDE, LocationTests.LOCATION_LONGITUDE, Arrays.asList(LocationTests.SSID3));
        assertSuccess(response);
        assertTrue(response.contains(MESSAGE1));
    }

    @Test
    public void post2MessagesTest()throws Exception{
        postMessageTest();
        String id = loginGetId(USERNAME, PASSWORD);
        String response = postMessage(id, LocationTests.WIFI_LOCATION_NAME, null, false, today, tomorrow, MESSAGE2);
        assertSuccess(response);

        response = getMessages(id, LocationTests.LOCATION_LATITUDE, LocationTests.LOCATION_LONGITUDE, Arrays.asList(LocationTests.SSID3));
        assertSuccess(response);
        assertTrue(response.contains(MESSAGE1));
        assertFalse(response.contains(MESSAGE2));

        response = getMessages(id, LocationTests.LOCATION2_LATITUDE, LocationTests.LOCATION2_LONGITUDE, Arrays.asList(LocationTests.SSID1, LocationTests.SSID2));
        assertSuccess(response);
        assertFalse(response.contains(MESSAGE1));
        assertTrue(response.contains(MESSAGE2));

    }

    @Test
    public void postBlacklistMessage()throws Exception{
        String id1 = loginGetId(USERNAME, PASSWORD);

        Map<String, String> rules = new HashMap<>();
        rules.put(ProfileTests.INTEREST2_KEY, ProfileTests.INTEREST2_DIFFERENT_VALUE);

        postMessage(id1, LocationTests.LOCATION_NAME,rules, false, today, tomorrow, MESSAGE2);

        String response = getMessages(id1, LocationTests.LOCATION_LATITUDE, LocationTests.LOCATION_LONGITUDE, Arrays.asList(LocationTests.SSID2));
        assertSuccess(response);
        assertTrue(response.contains(MESSAGE2));

        String id2 = loginGetId(USERNAME2, PASSWORD2);
        response = getMessages(id2, LocationTests.LOCATION_LATITUDE, LocationTests.LOCATION_LONGITUDE, Arrays.asList(LocationTests.SSID2));
        assertSuccess(response);
        assertFalse(response.contains(MESSAGE2));
    }

    @Test
    public void postWhitelistMessage()throws Exception{
        String id1 = loginGetId(USERNAME, PASSWORD);

        Map<String, String> rules = new HashMap<>();
        rules.put(ProfileTests.INTEREST2_KEY, ProfileTests.INTEREST2_DIFFERENT_VALUE);

        postMessage(id1, LocationTests.LOCATION_NAME,rules, true, today, tomorrow, MESSAGE3);

        String response = getMessages(id1, LocationTests.LOCATION_LATITUDE, LocationTests.LOCATION_LONGITUDE, Arrays.asList(LocationTests.SSID2));
        assertSuccess(response);
        assertFalse(response.contains(MESSAGE3));

        String id2 = loginGetId(USERNAME2, PASSWORD2);
        response = getMessages(id2, LocationTests.LOCATION_LATITUDE, LocationTests.LOCATION_LONGITUDE, Arrays.asList(LocationTests.SSID2));
        assertSuccess(response);
        assertTrue(response.contains(MESSAGE3));
    }

    @Test
    public void getYesterdaysMessages()throws Exception{
        String id = loginGetId(USERNAME, PASSWORD);

        String response = postMessage(id, LocationTests.LOCATION_NAME, null, false, yesterday, today, MESSAGE1);
        assertSuccess(response);

        response = getUserMessages(id);
        assertSuccess(response);
        assertTrue(response.contains(MESSAGE1));

        response = getMessages(id, LocationTests.LOCATION_LATITUDE, LocationTests.LOCATION_LONGITUDE, Arrays.asList(LocationTests.SSID3));
        assertSuccess(response);
        assertFalse(response.contains(MESSAGE1));

    }

    @Test
    public void getTomorrowsMessages()throws Exception{
        String id = loginGetId(USERNAME, PASSWORD);

        String response = postMessage(id, LocationTests.LOCATION_NAME, null, true, tomorrow, overtomorrow, MESSAGE1);
        assertSuccess(response);

        response = getUserMessages(id);
        assertSuccess(response);
        assertTrue(response.contains(MESSAGE1));

        response = getMessages(id, LocationTests.LOCATION_LATITUDE, LocationTests.LOCATION_LONGITUDE, Arrays.asList(LocationTests.SSID3));
        assertSuccess(response);
        assertFalse(response.contains(MESSAGE1));

    }

    @Test
    public void deleteMessageTest()throws Exception{
        String id = loginGetId(USERNAME, PASSWORD);

        String response = postMessage(id, LocationTests.LOCATION_NAME, null, false, today, tomorrow, MESSAGE1);
        assertSuccess(response);

        response = getUserMessages(id);
        assertSuccess(response);
        assertTrue(response.contains(MESSAGE1));

        JSONObject json = new JSONObject(response);
        JSONArray array = (JSONArray)json.get("messages");
        JSONObject firstMessage = (JSONObject)array.get(0);
        String messageId = firstMessage.get("id").toString();

        response = getMessages(id, LocationTests.LOCATION_LATITUDE, LocationTests.LOCATION_LONGITUDE, Arrays.asList(LocationTests.SSID3));
        assertSuccess(response);
        assertTrue(response.contains(MESSAGE1));


        deleteMessage(id, messageId);


        response = getUserMessages(id);
        assertSuccess(response);
        assertFalse(response.contains(MESSAGE1));

        response = getMessages(id, LocationTests.LOCATION_LATITUDE, LocationTests.LOCATION_LONGITUDE, Arrays.asList(LocationTests.SSID3));
        assertSuccess(response);
        assertFalse(response.contains(MESSAGE1));
    }
}
