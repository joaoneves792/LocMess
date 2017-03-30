package pt.ulisboa.tecnico.cmov.locmess.Domain;

/**
 * Created by joao on 3/25/17.
 */
public class GPSLocation extends Location{
    private double latitude;
    private double longitude;
    private double radius;

    private final String type = "GPS";

    public GPSLocation(String name, double lat, double longi, double rad) {
        super(name);
        latitude = lat;
        this.longitude = longi;
        this.radius = rad;
    }

    public GPSLocation(){

    }

    public double getLongitude(){
        return longitude;
    }

    public double getLongitudeRadians(){
        return Math.toRadians(longitude);
    }

    public  double getLatitude(){
        return latitude;
    }

    public double getLatitudeRadians(){
        return Math.toRadians(latitude);
    }

    public double getRadius(){
        return radius;
    }

    public String getType(){
        return type;
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
        if(!otherLocation.getType().equals(type))
            return false;
        GPSLocation otherGPSLocation = (GPSLocation)otherLocation;

        return this.distanceTo(otherGPSLocation) <= this.getRadius();
    }
}
