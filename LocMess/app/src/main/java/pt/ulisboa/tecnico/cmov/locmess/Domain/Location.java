package pt.ulisboa.tecnico.cmov.locmess.Domain;

/**
 * Created by joao on 3/25/17.
 */
public abstract class Location {
    private String name;

    public Location(String name) {
        this.name = name;
    }

    public Location(){

    }

    public String getName(){
        return name;
    }

    public abstract String getType();

    public abstract boolean equals(Location otherLocation);
}
