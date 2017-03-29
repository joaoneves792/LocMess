package pt.ulisboa.tecnico.cmov.locmess.Domain;

import java.util.Collection;

/**
 * Created by joao on 3/25/17.
 */
public class WiFiLocation extends Location{
    private Collection<String> _wifiIds;

    private final String _type = "Wifi";

    public WiFiLocation(String name, Collection<String> wifiIds) {
        super(name);
        _wifiIds = wifiIds;
    }

    public Collection<String> getWifiIds(){
        return _wifiIds;
    }

    public String getType(){
        return _type;
    }

    /*Another location is equal to this location if it contains at least all the ssids this one contains*/
    public boolean equals(Location otherLocation){
        if(!otherLocation.getType().equals(_type))
            return false;
        WiFiLocation otherWiFiLocation = (WiFiLocation)otherLocation;
        for(String ssid : _wifiIds){
            if(!otherWiFiLocation.getWifiIds().contains(ssid))
                return false;
        }
        return true;
    }
}
