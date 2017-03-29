package pt.ulisboa.tecnico.cmov.locmess.Domain;

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

    public double getLongitudeRadians(){
        return Math.toRadians(_longitude);
    }

    public  double getLatitude(){
        return _latitude;
    }

    public double getLatitudeRadians(){
        return Math.toRadians(_latitude);
    }

    public double getRadius(){
        return _radius;
    }

    public String getType(){
        return _type;
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    private double distanceTo(GPSLocation otherLocation) {
        final int R = 6371; // Radius of the earth

        double lat1 = this.getLatitude();
        double lon1 = this.getLongitude();

        double lat2 = otherLocation.getLatitude();
        double lon2 = otherLocation.getLongitude();

        //Since we dont get elevations ignore it for now
        double el1 = 0;
        double el2 = 0;

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }


    public boolean equals(Location otherLocation){
        if(!otherLocation.getType().equals(_type))
            return false;
        GPSLocation otherGPSLocation = (GPSLocation)otherLocation;

        return this.distanceTo(otherGPSLocation) <= this.getRadius();
    }
}
