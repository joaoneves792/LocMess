package pt.ulisboa.tecnico.cmov.locmess.Domain;

import java.util.Collection;

/**
 * Created by joao on 3/25/17.
 */
public class WiFiLocation extends Location{
    private Collection<String> wifiIds;

    private final String type = "Wifi";

    public static final String TYPE = "Wifi";

    public WiFiLocation(String name, Collection<String> wifiIds) {
        super(name);
        this.wifiIds = wifiIds;
    }

    public WiFiLocation(){

    }

    public Collection<String> getWifiIds(){
        return wifiIds;
    }

    public String getType(){
        return type;
    }

    /*Another location is equal to this location if it contains at least all the ssids this one contains*/
    public boolean equals(Location otherLocation){
        if(!otherLocation.getType().equals(type))
            return false;
        WiFiLocation otherWiFiLocation = (WiFiLocation)otherLocation;
        for(String ssid : wifiIds){
            if(!otherWiFiLocation.getWifiIds().contains(ssid))
                return false;
        }
        return true;
    }
}
