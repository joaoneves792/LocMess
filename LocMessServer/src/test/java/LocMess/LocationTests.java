package LocMess;

import org.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by joao on 3/26/17.
 */
public class LocationTests extends LocMessTest {


    private static final String LOCATION_NAME = "Arco do Cego";
    private static final double LOCATION_LATITUDE = 38.7343829;
    private static final double LOCATION_LONGITUDE = -9.1403882;
    private static final double LOCATION_RADIUS = 20;


    private static final String LOCATION2_NAME = "Taguspark";
    private static final double LOCATION2_LATITUDE = 48.7343829;
    private static final double LOCATION2_LONGITUDE = -19.1403882;
    private static final double LOCATION2_RADIUS = 200;

    private static final String WIFI_LOCATION_NAME = "RNL";
    private static final String SSID1 = "eduroam";
    private static final String SSID2 = "ZON-B4C31";
    private static final String SSID3 = "MEO-CD35F";

    @Test
    public void addLocationTest()throws Exception{
        register(USERNAME, PASSWORD);
        String id = loginGetId(USERNAME, PASSWORD);

        assertSuccess(addGPSLocation(id, LOCATION_NAME, LOCATION_LATITUDE, LOCATION_LONGITUDE, LOCATION_RADIUS));

        String locationList = requestLocationList(id);
        assertSuccess(locationList);
        assertTrue(locationList.contains(LOCATION_NAME));
        assertTrue(locationList.contains("GPS"));
        assertFalse(locationList.contains("Wifi"));
    }

    @Test
    public void addWifiLocationTest()throws Exception{
        register(USERNAME, PASSWORD);
        String id = loginGetId(USERNAME, PASSWORD);

        List<String> ssids = Arrays.asList(SSID1, SSID2, SSID3);
        assertSuccess(addWifiLocation(id, WIFI_LOCATION_NAME, ssids));
        String locationList = requestLocationList(id);
        assertSuccess(locationList);
        assertTrue(locationList.contains(WIFI_LOCATION_NAME));
        assertFalse(locationList.contains("GPS"));
        assertTrue(locationList.contains("Wifi"));

        assertTrue(locationList.contains(SSID1));
        assertTrue(locationList.contains(SSID2));
        assertTrue(locationList.contains(SSID3));
    }

    @Test
    public void addMultipleLocationsTest()throws Exception{
        addLocationTest();
        String id = loginGetId(USERNAME, PASSWORD);

        assertSuccess(addGPSLocation(id, LOCATION2_NAME, LOCATION2_LATITUDE, LOCATION2_LONGITUDE, LOCATION2_RADIUS));

        String locationList = requestLocationList(id);
        assertSuccess(locationList);
        assertTrue(locationList.contains(LOCATION_NAME));
        assertTrue(locationList.contains(LOCATION2_NAME));

    }

    @Test
    public void removeLocationTest()throws Exception{
        addMultipleLocationsTest();
        String id = loginGetId(USERNAME, PASSWORD);

        deleteLocation(id, LOCATION_NAME);

        String locationList = requestLocationList(id);
        assertTrue(locationList.contains(LOCATION2_NAME));
        assertFalse(locationList.contains(LOCATION_NAME));
    }

}
