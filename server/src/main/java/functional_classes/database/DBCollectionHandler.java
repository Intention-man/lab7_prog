package functional_classes.database;

import enums.Country;
import enums.MovieGenre;
import enums.MpaaRating;
import movies_classes.Coordinates;
import movies_classes.Location;
import movies_classes.Movie;
import movies_classes.Person;

import java.sql.*;
import java.util.HashSet;

public class DBCollectionHandler {
    Connection connection;

    public DBCollectionHandler(Connection connection) throws SQLException {
        this.connection = connection;
        createMoviesTableIfNotExist();
    }

    public boolean isMovieExist(String name) throws SQLException {
        synchronized (this) {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM movies WHERE name=(?)");
            statement.setString(1, name);
            return ContainerCommonParts.existenceQuery(statement);
        }
    }

    public synchronized boolean isMovieExist(int id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM movies WHERE id=(?)");
        statement.setInt(1, id);
        return ContainerCommonParts.existenceQuery(statement);
    }

    public synchronized void createMoviesTableIfNotExist() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("CREATE table IF NOT EXISTS movies (id SERIAL PRIMARY KEY, name VARCHAR(255) not null unique CHECK (trim(name) <> ''), coord_x integer not null CHECK (coord_x > -319), coord_y int, creation_date TIMESTAMP DEFAULT now(), oscars_count bigint CHECK (oscars_count > 0), length bigint CHECK (length > 0), genre VARCHAR(255), mpaa_rating VARCHAR(255), operator_name VARCHAR(255) not null CHECK (trim(operator_name) <> ''), passport_id VARCHAR(255) not null CHECK (length(passport_id) > 8) CHECK (trim(passport_id) <> ''), nationality VARCHAR(255), location_x bigint, location_y bigint, location_z float, creator VARCHAR(255))");
        statement.close();
    }

    public synchronized String addMovieToBD(Movie movie, String login) throws SQLException {
        if (isMovieExist(movie.getName())) {
            return "Фильм с таким названием уже существует";
        } else {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO movies VALUES (default, ?, ?, ?, default, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            statement = ContainerCommonParts.fillMovieRecordStatement(statement, movie);
            statement.setString(14, login);
            statement.executeUpdate();
            statement.close();
            if (isMovieExist(movie.getName())) {
                return "1";
            } else {
                return "Ошибка при попытке добавления фильма";
            }
        }
    }

    public synchronized boolean updateMovie(Movie movie) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "UPDATE movies SET name=?, coord_x=?, coord_y=?, oscars_count=?, length=?, genre=?, mpaa_rating=?, operator_name=?, passport_id=?, nationality=?, location_x=?, location_y=?, location_z=? WHERE id=?"
        );
        statement = ContainerCommonParts.fillMovieRecordStatement(statement, movie);
        statement.setInt(14, movie.getId());
        return (statement.executeUpdate() == 1);
    }

    public synchronized boolean removeMovie(int id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("DELETE FROM movies WHERE id = ?");
        statement.setInt(1, id);
        statement.executeUpdate();
        return !isMovieExist(id);
    }

    public synchronized boolean clearCollection(String login) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("DELETE FROM movies WHERE creator=?");
        statement.setString(1, login);
        statement.executeUpdate();
        return (getMoviesCountByThisCreator(login) == 0);
    }

    public synchronized HashSet<Movie> getAllMovies() throws SQLException {
        HashSet<Movie> moviesList = new HashSet<>();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM movies");
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            Movie movie = new Movie(resultSet.getInt("id"), resultSet.getString("name"), new Coordinates(
                    resultSet.getInt("coord_x"),
                    resultSet.getInt("coord_y")
            ), resultSet.getTimestamp("creation_date"), resultSet.getLong("oscars_count"), resultSet.getLong("length"),
                    resultSet.getString("genre") != null ? MovieGenre.valueOf(resultSet.getString("genre")) : null,
                    resultSet.getString("mpaa_rating") != null ? MpaaRating.valueOf(resultSet.getString("mpaa_rating")) : null, new Person(
                    resultSet.getString("operator_name"),
                    resultSet.getString("passport_id"),
                    resultSet.getString("nationality") != null ? Country.valueOf(resultSet.getString("nationality")) : null,
                    new Location(
                            resultSet.getString("location_x") != null ? resultSet.getLong("location_x") : null,
                            resultSet.getString("location_y") != null ? resultSet.getLong("location_y") : null,
                            resultSet.getString("location_z") != null ? resultSet.getDouble("location_z") : null)),
            resultSet.getString("creator"));
            moviesList.add(movie);
        }
        System.out.println(moviesList);
        return moviesList;
    }


    public synchronized int getMoviesCount() throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM movies");
        return ContainerCommonParts.countQuery(statement);
    }

    public synchronized int getMoviesCountByThisCreator(String login) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM movies WHERE creator=?");
        statement.setString(1, login);
        return ContainerCommonParts.countQuery(statement);
    }

    public synchronized void close() throws SQLException {
        connection.close();
    }
}