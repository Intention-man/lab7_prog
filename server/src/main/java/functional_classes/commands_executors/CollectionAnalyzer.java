package functional_classes.commands_executors;

import functional_classes.database.DBCollectionHandler;
import movies_classes.Movie;
import movies_classes.Movies;

import java.sql.SQLException;
import java.util.*;

/**
 * Main Command Executor. It's commands invoke by CommandDistributor. It invokes DB commands and executes actions with collection in memory
 */

public class CollectionAnalyzer {

    // initialization

    private Movies movies;
    private DBCollectionHandler dbCollectionHandler;
    private List<String> commandsHistory = new ArrayList<>();
    private String login;

    public CollectionAnalyzer(Movies movies, DBCollectionHandler dbCollectionHandler) {
        this.movies = movies;
        this.dbCollectionHandler = dbCollectionHandler;
    }

    // commands execution

    public boolean addCommandToHistory(String command) {
        commandsHistory.add(command);
        return true;
    }

    public String addIfMax(HashMap data) {
        try {
            long maxLength = movies.getMoviesList().stream()
                    .max(Comparator.comparingLong(Movie::getLength))
                    .get().getLength();
            if ((long) data.get(4) > maxLength) {
                return addMovie(data);
            } else {
                return "Фильм не самый длинный";
            }
        } catch (Exception e) {
            return "Ошибка, е-мае!(";
        }
    }

    public String addIfMin(HashMap data) {
        try {
            long minLength = movies.getMoviesList().stream()
                    .min(Comparator.comparingLong(Movie::getLength))
                    .get().getLength();
            if ((long) data.get(4) < minLength) {
                return addMovie(data);
            } else {
                return "Фильм не кратчайший по длине";
            }
        } catch (Exception e) {
            return "Ошибка, е-мае!(";
        }
    }

    public String addMovie(HashMap data) throws SQLException {
        int newId = movies.getSortedMovies("id").size() > 0 ? movies.getSortedMovies("id").get(movies.getSortedMovies("id").size() - 1).getId() + 1 : 1;
        Movie newMovie = new Movie(newId, data);
        String response = dbCollectionHandler.addMovieToBD(newMovie, login);
        if (response.length() == 1) {
            System.out.println(movies.getMoviesList().size());
            movies.getMoviesList().add(newMovie);
            System.out.println(movies.getMoviesList().size());
            return "Успех!";
        } else {
            return response;
        }
    }

    public boolean clear() throws SQLException {
        if (dbCollectionHandler.clearCollection(login)) {
            movies.getMoviesList().clear();
            return true;
        } else {
            return false;
        }
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

    public List<String> info() {
        System.out.println("info!!!");
        ArrayList<String> answer = new ArrayList<>();
        answer.add("Класс элементов коллекции: " + (movies.getMoviesList().size() > 0 ? movies.getMoviesList().stream().toList().get(0).getClass() : "Movie"));
        answer.add("Дата и время ининциализации коллекции: " + movies.getInitializationDate());
        answer.add("Количество элементов в колллекции: " + movies.moviesCount());
        answer.add("Список имеющихся в коллекции фильмов (id + название)");
        movies.getSortedMovies("name")
                .forEach(movie -> answer.add(movie.getId() + " - " + movie.getName()));
        return answer;
    }

    public String removeById(Integer enteredId) throws SQLException {
        if (movies.getMoviesList().stream()
                .anyMatch(movie -> movie.getId() == enteredId)) {
            Movie foundMovie = (Movie) movies.getMoviesList().stream()
                    .filter(movie -> movie.getId() == enteredId)
                    .findAny().get();
            if (Objects.equals(foundMovie.getCreator(), login)) {
                if (dbCollectionHandler.removeMovie(enteredId)) {
                    movies.getMoviesList().remove(foundMovie);
                    return "Успешно удалено";
                }
                return "Ошибка при удалении";
            } else {
                return "Вы не являетесь создателем экземпляра коллекции";
            }
        } else {
            return "Фильма с таким id нет в коллекции";
        }
    }

    public String removeAnyByOscarsCount(Long enteredCount) throws SQLException {
        if (movies.getMoviesList().stream()
                .anyMatch(movie -> movie.getOscarsCount() == enteredCount)) {
            Movie foundMovie = movies.getMoviesList().stream().
                    filter(movie -> movie.getOscarsCount() == enteredCount).
                    findAny().get();
            if (Objects.equals(foundMovie.getCreator(), login)) {
                if (dbCollectionHandler.removeMovie(foundMovie.getId())) {
                    String name = foundMovie.getName();
                    movies.getMoviesList().remove(foundMovie);
                    return "Фильм " + name + " удален";
                }
                return "Ошибка при удалении";
            } else {
                return "Вы не являетесь создателем экземпляра коллекции";
            }
        } else {
            return "Нет ни 1 фильма с таким количеством оскаров";
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

    public String updateMovie(HashMap data) throws SQLException {
        int id = (int) data.get(data.size() - 1);
        if (movies.getMoviesList().stream()
                .anyMatch(movie -> movie.getId() == id)) {
            Movie foundMovie = movies.getMoviesList().stream()
                     .filter(movie -> movie.getId() == id)
                    .findAny().get();
            Movie newMovie = new Movie(id, data);
            if (Objects.equals(foundMovie.getCreator(), login)) {
                if (dbCollectionHandler.updateMovie(newMovie)) {
                    foundMovie.update(data);
                    return "Успешно обновлено";
                }
                return "Обновление не удалось";
            } else {
                return "Вы не являетесь создателем экземпляра коллекции";
            }
        } else {
            return "В коллекции нет фильма с таким id";
        }
    }

    public void setCurrentLogin(String login) {
        this.login = login;
    }
}