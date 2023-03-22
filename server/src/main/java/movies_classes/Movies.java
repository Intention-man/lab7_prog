package movies_classes;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


/**
 *    Movie - Movie collection class
 *    @param  {HashSet<Movie>} moviesList
 *    @param  {java.util.Date} initializationDate
 * */


public class Movies implements Serializable {

    private HashSet<Movie> moviesList;
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
        synchronized (moviesList) {
            return moviesList;
        }
    }

    public List<Movie> getSortedMovies(String field) {
        List<Movie> sortedList;
        synchronized (moviesList) {
            sortedList = moviesList.stream()
                    .sorted(Objects.equals(field, "id") ? Comparator.comparing(Movie::getId) : Comparator.comparing(Movie::getName))
                    .collect(Collectors.toList());
            return sortedList;
        }
    }

    public Date getInitializationDate() {
        synchronized (initializationDate) {
            return initializationDate;
        }
    }
}
