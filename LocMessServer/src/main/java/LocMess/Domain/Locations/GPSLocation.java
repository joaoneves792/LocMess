package LocMess.Domain.Locations;

/**
 * Created by joao on 3/25/17.
 */
public class GPSLocation extends Location{
    private double _latitude;
    private double _longitude;
    private double _radius;

    private final String _type = "GPS";

    public GPSLocation(String name, double latitude, double longitude, double radius) {
        super(name);
        _latitude = latitude;
        _longitude = longitude;
        _radius = radius;
    }

    public double getLongitude(){
        return _longitude;
    }

    public  double getLatitude(){
        return _latitude;
    }

    public double getRadius(){
        return _radius;
    }

    public String getType(){
        return _type;
    }
}
