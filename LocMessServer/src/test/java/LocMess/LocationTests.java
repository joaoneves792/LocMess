package LocMess;

import org.json.JSONObject;
import org.junit.Test;

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

    @Test
    public void addLocationTest()throws Exception{
        register(USERNAME, PASSWORD);
        String id = loginGetId(USERNAME, PASSWORD);

        assertSuccess(addGPSLocation(id, LOCATION_NAME, LOCATION_LATITUDE, LOCATION_LONGITUDE, LOCATION_RADIUS));

        String locationList = requestLocationList(id);
        assertSuccess(locationList);
        assertTrue(locationList.contains(LOCATION_NAME));
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

}
