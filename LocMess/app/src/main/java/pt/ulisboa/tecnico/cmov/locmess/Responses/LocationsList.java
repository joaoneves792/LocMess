package pt.ulisboa.tecnico.cmov.locmess.Responses;


import java.util.Enumeration;

import pt.ulisboa.tecnico.cmov.locmess.Domain.Location;

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
