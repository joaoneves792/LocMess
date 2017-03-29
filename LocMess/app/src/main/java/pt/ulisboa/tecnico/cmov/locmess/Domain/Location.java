package pt.ulisboa.tecnico.cmov.locmess.Domain;

/**
 * Created by joao on 3/25/17.
 */
public abstract class Location {
    private String _name;

    public Location(String name) {
        _name = name;
    }

    public String getName(){
        return _name;
    }

    public abstract String getType();

    public abstract boolean equals(Location otherLocation);
}
