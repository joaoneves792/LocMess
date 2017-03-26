package LocMess.Responses;

import LocMess.Locations.Location;

import java.util.Enumeration;
import java.util.List;

/**
 * Created by joao on 3/25/17.
 */
public class LocationsList extends Response{
    private Enumeration<Location> _locations;

    public LocationsList(Enumeration<Location> locations) {
        super(true);
        _locations = locations;
    }

    public Enumeration<Location> getLocations(){
        return _locations;
    }

}
