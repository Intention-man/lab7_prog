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
    private String name; //Поле не может быть null, Строка не может быть пустой
    private String passportID; //Длина строки должна быть не меньше 9, Строка не может быть пустой, Поле может быть null
    private N nationality; //Поле может быть null
    private Location location; //Поле может быть null


    public Person(){}

    public Person(String name, String passportID, N nationality, Location location)
    {
     this.name = name;
     this.passportID = passportID;
     this.nationality = nationality;
     this.location = location;
    }

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