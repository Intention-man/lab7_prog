package movies_classes;


import org.simpleframework.xml.Element;

import java.io.Serializable;

/**
 *    Location<T, U, S> - unnecessary to use class, created specially for Person
 *    @param  {T} locX
 *    @param  {U} locY
 *    @param  {S} locZ
 * */

public class Location<T, U, S> implements Serializable {
    private T locX;
    private U locY;
    private S locZ;

    public Location(){}

    public Location(T locX,  U locY, S locZ){
        this.locX = locX;
        this.locY = locY;
        this.locZ = locZ;
    }

    public T getLocX() {
        return locX;
    }

    public U getLocY() {
        return locY;
    }

    public S getLocZ() {
        return locZ;
    }
}
