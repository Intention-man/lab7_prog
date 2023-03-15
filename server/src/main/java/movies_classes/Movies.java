package movies_classes;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


/**
 *    Movie - Movie collection class
 *    @param  {HashSet<Movie>} moviesList
 *    @param  {java.util.Date} initializationDate
 * */

@Root(name="movies")
public class Movies implements Serializable {

    @ElementList(name="moviesList")
    private HashSet<Movie> moviesList;
    @Attribute
    private Date initializationDate;


    public Movies(){
        this.initializationDate = new Date();
    }

    public void setMoviesList(HashSet<Movie> moviesList){
        this.moviesList = moviesList;
    }

    public int moviesCount(){
        return moviesList.size();
    }

    public HashSet<Movie> getMoviesList(){
        return moviesList;
    }

    public List<Movie> getSortedMovies(String field) {
        List<Movie> sortedList;
        sortedList = moviesList.stream()
                .sorted(Objects.equals(field, "id") ? Comparator.comparing(Movie::getId) : Comparator.comparing(Movie::getName))
                        .collect(Collectors.toList());

        return sortedList;
    }

    public Date getInitializationDate() {
        return initializationDate;
    }
}
