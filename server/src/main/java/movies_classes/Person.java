package movies_classes;

import org.simpleframework.xml.Element;

import java.io.Serializable;

/**
 *    Person<N> - created specially for Movie
 *    @param  {String} name
 *    @param  {String} passportID
 *    @param  {N} nationality
 *    @param  {Location} location
 * */

public class Person<N> implements Serializable {
    @Element(name="name")
    private String name; //Поле не может быть null, Строка не может быть пустой
    @Element(name="passportID")
    private String passportID; //Длина строки должна быть не меньше 9, Строка не может быть пустой, Поле может быть null
    @Element(name="nationality", required=false)
    private N nationality; //Поле может быть null
    @Element(name="location", required=false)
    private Location location; //Поле может быть null


    public Person(){}

    public Person(String name, String passportID, N nationality, Location location)
    {
     this.name = name;
     this.passportID = passportID;
     this.nationality = nationality;
     this.location = location;
    }

//    public Person(List list)
//    {
//        System.out.println("I'm in location's constructor");
//        if (list.get(0).toString().matches("\\w*")) {this.name = (String) list.get(0);};
//        if (list.get(1).toString().matches("\\w*")) {this.passportID = (String) list.get(1);};
//        if (list.get(2).toString().matches("\\w*")) {this.nationality = (Country) list.get(2);};
//        if (list.get(3).l) {this.location = (Location) list.get(3);};
//    }

    public String getName() {
        return name;
    }

    public String getPassportID() {
        return passportID;
    }

    public N getNationality() {
        return nationality;
    }

    public Location getLocation() {
        return location;
    }
}