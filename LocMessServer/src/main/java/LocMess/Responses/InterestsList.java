package LocMess.Responses;

import LocMess.Domain.Locations.Location;

import java.util.Enumeration;
import java.util.Map;

/**
 * Created by joao on 3/25/17.
 */
public class InterestsList extends Response{
    private Map<String, String> _interests;

    public InterestsList(Map<String, String> interests) {
        super(true);
        _interests = interests;
    }

    public Map<String, String> getInterests(){
        return _interests;
    }

}
