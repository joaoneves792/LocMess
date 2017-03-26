package LocMess.Locations;

import java.util.List;

/**
 * Created by joao on 3/25/17.
 */
public class WiFiLocation extends Location{
    private List<String> _wifiIds;

    private final String _type = "Wifi";

    public WiFiLocation(String name, List<String> wifiIds) {
        super(name);
        _wifiIds = wifiIds;
    }

    public List<String> getWifiIds(){
        return _wifiIds;
    }

    public String getType(){
        return _type;
    }
}
