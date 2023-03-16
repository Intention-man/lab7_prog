package functional_classes;

import functional_classes.commands_executors.CollectionAnalyzer;
import functional_classes.commands_executors.CommandDistributor;
import functional_classes.database.DBCollectionHandler;
import functional_classes.database.DBConnector;
import functional_classes.database.DBUserHandler;
import functional_classes.threads.ServerReader;
import functional_classes.threads.ServerSerializer;
import movies_classes.Movies;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.*;

public class Server {
    private final Properties properties;
    static ExecutorService executor = Executors.newFixedThreadPool(1); // Создаем пул из 1 потока
    static ForkJoinPool forkJoinPool = new ForkJoinPool(); // Создаем пул для многопоточной отправки ответ

    public Server(Properties properties) {
        this.properties = properties;
    }

    public void serverStartup() {
        try {
            DBConnector dbConnector = new DBConnector(properties);
            dbConnector.createConnection();
            DBCollectionHandler dbCollectionHandler = new DBCollectionHandler(dbConnector.getConnection());
            Movies movies = new Movies();
            movies.setMoviesList(dbCollectionHandler.getAllMovies());
            DBUserHandler dbUserHandler = new DBUserHandler(dbConnector.getConnection());
            CollectionAnalyzer collectionAnalyzer = new CollectionAnalyzer(movies, dbCollectionHandler);
            CommandDistributor commandDistributor = new CommandDistributor();
            commandDistributor.addExecutor("CollectionAnalyzer", collectionAnalyzer);
            commandDistributor.addExecutor("DBCollectionHandler", dbCollectionHandler);
            commandDistributor.addExecutor("DBUserHandler", dbUserHandler);

            ServerReader serverReader = new ServerReader(commandDistributor);
            ServerSerializer serverSerializer = new ServerSerializer(commandDistributor);

            Thread t1 = new Thread(serverReader::readConsole);
            Thread t2 = new Thread(() -> {
                while (true) {
                    serverSerializer.waitForRequest();
                    start(serverSerializer);
                }
            });

            t2.start();
            t1.start();

        } catch (Exception | ExceptionInInitializerError e) {
            System.out.println(e);
        }
    }

    public static void start(ServerSerializer serverSerializer) {
        while (!Objects.equals(serverSerializer.getStage(), "get")) {
            executor.submit(() -> {
                        if (Objects.equals(serverSerializer.getStage(), "execute")) {
                            serverSerializer.executeCommand();
                            forkJoinPool.submit(() -> {
                                try {
                                    serverSerializer.sendResponse();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    throw new RuntimeException(e);
                                }
                            });
                            System.out.println("loop ends 1 stage");
                        }
                    }
            );
        }
    }
}