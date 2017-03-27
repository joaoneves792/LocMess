package LocMess;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static junit.framework.TestCase.*;

/**
 * Created by joao on 3/26/17.
 */
public class MessageTests extends LocMessTest {

    private String today;
    private String tomorrow;

    private final String MESSAGE1 = "Programming skills enhancing pills! Now available for just 100$. Call 934363257";
    private final String MESSAGE2 = "Clowns suck!";

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

        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate now = LocalDate.now(ZoneId.of("Europe/Lisbon"));
        LocalDateTime todayDate = LocalDateTime.of(now, midnight);
        LocalDateTime tomorrowDate = todayDate.plusDays(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm-MM/dd/yyyy", Locale.ENGLISH); //Example 14:30-12/24/2017
        today = todayDate.format(formatter);
        tomorrow = tomorrowDate.format(formatter);

    }

    @Test
    public void postMessageTest()throws Exception{
        String id = loginGetId(USERNAME, PASSWORD);

        String response = postMessage(id, LocationTests.LOCATION_NAME, null, true, today, tomorrow, MESSAGE1);
        assertSuccess(response);

        response = getUserMessages(id);
        assertSuccess(response);
        assertTrue(response.contains(MESSAGE1));
    }

}
