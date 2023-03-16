package movies_classes;

import org.simpleframework.xml.Element;

import java.io.Serializable;

/**
 *    Coordinates - created specially for Movie
 *    @param  {Integer} coordX
 *    @param  {int} coordY
 * */

public class Coordinates implements Serializable {
    private Integer coordX; //Значение поля должно быть больше -319, Поле не может быть null
    private int coordY;

    public Coordinates(){}

    public Coordinates(Integer coordX, int coordY)
    {
        this.coordX = coordX;
        this.coordY = coordY;
    }

    public Integer getCoordX() {
        return coordX;
    }

    public int getCoordY() {
        return coordY;
    }
}