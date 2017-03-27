package LocMess.Domain.Locations;

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
}
