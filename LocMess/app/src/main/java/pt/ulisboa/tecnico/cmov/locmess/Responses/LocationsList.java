package pt.ulisboa.tecnico.cmov.locmess.Responses;


import java.util.Enumeration;

import pt.ulisboa.tecnico.cmov.locmess.Domain.Location;

/**
 * Created by joao on 3/25/17.
 */
public class LocationsList extends Response{
    private Enumeration<Location> locations;

    public LocationsList(){

    }

    public Enumeration<Location> getLocations(){
        return locations;
    }

}
