package functional_classes.commands_executors;

import movies_classes.Movie;
import movies_classes.Movies;

import java.util.*;

/**
 * Console App Component, executing most "backend" actions, related to the collection.
 * His methods called by Receiver.
 * It manages collection movies, as every functional components.
 */

public class CollectionWorker {

    // initialization

    private Movies movies;
    private List<String> commandsHistory = new ArrayList<>();

    public CollectionWorker(Movies movies) {
        this.movies = movies;
    }

    // commands execution

    public boolean addCommandToHistory(String command) {
        commandsHistory.add(command);
        return true;
    }

    public boolean addIfMax(HashMap data) {
        try {
            long maxLength = movies.getMoviesList().stream()
                    .max(Comparator.comparingLong(Movie::getLength))
                    .get().getLength();
            if ((long) data.get(4) > maxLength) {
                addMovie(data);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public boolean addIfMin(HashMap data) {
        try {
            long minLength = movies.getMoviesList().stream()
                    .min(Comparator.comparingLong(Movie::getLength))
                    .get().getLength();
            if ((long) data.get(4) < minLength) {
                addMovie(data);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public boolean addMovie(HashMap data) {
        int currentCount = movies.getSortedMovies("id").get(movies.getSortedMovies("id").size() - 1).getId() + 1;
        movies.getMoviesList().add(new Movie(currentCount, data));
        System.out.println(movies.getMoviesList());
        return true;
    }

    public boolean clear() {
        movies.getMoviesList().clear();
        return true;
    }

    public int countByOscarsCount(Long enteredCount) {
        return (int) movies.getMoviesList().stream()
                .filter(movie -> movie.getOscarsCount() == enteredCount)
                .count();
    }

    public List<String> getLast12Commands() {
        System.out.println("print all:" + commandsHistory);
        return new ArrayList<String>(commandsHistory.subList(commandsHistory.size() >= 12 ? commandsHistory.size() - 12 : 0, commandsHistory.size()));
    }

    public Movies getMovies() {
        return movies;
    }

    public HashSet<Movie> getMoviesList() {
        return movies.getMoviesList();
    }

    public List<String> info() {
        ArrayList<String> answer = new ArrayList<>();
        answer.add("Класс элементов коллекции: " + movies.getMoviesList().stream().toList().get(0).getClass());
        answer.add("Дата и время ининциализации коллекции: " + movies.getInitializationDate());
        answer.add("Количество элементов в колллекции: " + movies.moviesCount());
        answer.add("Список имеющихся в коллекции фильмов (id + название)");
        movies.getSortedMovies("name")
                .forEach(movie -> answer.add(movie.getId() + " - " + movie.getName()));
        return answer;
    }

    public boolean removeById(Integer enteredId) {
        if (movies.getMoviesList().stream()
                .anyMatch(movie -> movie.getId() == enteredId)) {
            Movie foundMovie = (Movie) movies.getMoviesList().stream()
                    .filter(movie -> movie.getId() == enteredId)
                    .findAny().get();
            movies.getMoviesList().remove(foundMovie);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeAnyByOscarsCount(Long enteredCount) {
        if (movies.getMoviesList().stream()
                .anyMatch(movie -> movie.getOscarsCount() == enteredCount)) {
            Movie foundMovie = movies.getMoviesList().stream().
                    filter(movie -> movie.getOscarsCount() == enteredCount).
                    findAny().get();
            movies.getMoviesList().remove(foundMovie);
            return true;
        } else {
            return false;
        }
    }

    public ArrayList show() {
        ArrayList<String> lines = new ArrayList<>();
        movies.getSortedMovies("name").forEach(movie -> lines.addAll(Arrays.asList(movie.getInstance())));
        return lines;
    }

    public long sumOfLength() {
        return movies.getMoviesList().stream().mapToLong(Movie::getLength).sum();
    }

    public boolean updateMovie(HashMap data) {
        int id = (int) data.get(data.size() - 1);
        if (movies.getMoviesList().stream()
                .anyMatch(movie -> movie.getId() == id)) {
            Movie foundMovie = movies.getMoviesList().stream()
                    .filter(movie -> movie.getId() == id)
                    .findAny().get();
            foundMovie.update(data);
            return true;
        } else {
            return false;
        }
    }
}