package functional_classes;

import auxiliary_classes.CommandMessage;
import auxiliary_classes.ResponseMessage;
import functional_classes.commands_executors.CollectionAnalyzer;
import functional_classes.commands_executors.CommandDistributor;
import functional_classes.database.DBCollectionHandler;
import functional_classes.database.DBConnector;
import functional_classes.database.DBUserHandler;
import functional_classes.threads.ServerReader;
import functional_classes.threads.ServerSerializer;
import movies_classes.Movies;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.*;

public class Server {
    private final Properties properties;


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
//            FileWorker fileWorker = new FileWorker();
//            Movies movies = fileWorker.fill();
            CollectionAnalyzer collectionAnalyzer = new CollectionAnalyzer(movies, dbCollectionHandler);
            CommandDistributor commandDistributor = new CommandDistributor();
            commandDistributor.setDbManager(dbConnector);
            commandDistributor.addExecutor("CollectionAnalyzer", collectionAnalyzer);
//            commandDistributor.addExecutor("FileWorker", fileWorker);
            commandDistributor.addExecutor("DBCollectionHandler", dbCollectionHandler);
            commandDistributor.addExecutor("DBUserHandler", dbUserHandler);

            ServerReader serverReader = new ServerReader(commandDistributor);
            ServerSerializer serverSerializer = new ServerSerializer(commandDistributor);

            start(serverSerializer, serverReader);

        } catch (Exception | ExceptionInInitializerError e) {
            System.out.println(e);
        }
    }

    public static void start(ServerSerializer serverSerializer, ServerReader serverReader) {

        ExecutorService executor = Executors.newFixedThreadPool(2); // Создаем пул из двух потоков
        ForkJoinPool forkJoinPool = new ForkJoinPool(); // Создаем пул для многопоточной отправки ответа
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(); // Создаем очередь

        Thread t1 = new Thread(serverReader::readConsole);

        Thread t2 = new Thread(() -> {
            serverSerializer.waitForRequest();
        });

        t2.start();
        t1.start();

        executor.submit(() -> {
            // Код для потока, который получает сообщения по сетевому протоколу и выполняет команды
            while (true) {
                if (serverSerializer.getReadyToExecute()) {
                    System.out.println("ready to execute command");
                    serverSerializer.executeCommand();
                    System.out.println("ready to send response");
                    forkJoinPool.submit(() -> {
                        try {
                            serverSerializer.sendResponse();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        });

//        executor.shutdown(); // Завершаем работу ExecutorService
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); // Ждем, пока оба потока завершат работу
        } catch (InterruptedException e) {
            System.out.println("Ошибка при завершении работы потоков");
            e.printStackTrace();
        }
    }


//        try {
//            Synchronizer synchronizer = new Synchronizer();
//            Thread t1 = new Thread(() -> {
//                try {
//                    synchronizer.consoleThread(serverReader);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//            Thread t2 = new Thread(() -> {
//                try {
//                    synchronizer.clientChannelThread(serverSerializer);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//            t2.start();
//            t1.start();
//        } catch (Exception e) {
//            System.out.println(e);
//        }
}