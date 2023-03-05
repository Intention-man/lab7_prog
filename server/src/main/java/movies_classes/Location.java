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
    @Element(required=false, name = "locX")
    private T locX;
    @Element(required=false, name = "locY")
    private U locY;
    @Element(required=false, name = "locZ")
    private S locZ;

    public Location(){}

    public Location(@Element(required=false, name = "locX")T locX, @Element(required=false, name = "locY") U locY, @Element(required=false, name = "locZ") S locZ){
        this.locX = locX;
        this.locY = locY;
        this.locZ = locZ;
    }


//    public Location(List list)
//    {
//        System.out.println("I'm in location's constructor");
//        if (list.get(0).toString().matches("\\d*")) {this.locX = (long) list.get(0);};
//        if (list.get(1).toString().matches("\\d*")) {this.locY = (long) list.get(1);};
//        if (list.get(2).toString().matches("\\d*.\\d*")) {this.locZ = (double) list.get(2);};
//    }



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
