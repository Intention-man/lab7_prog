package functional_classes;

import functional_classes.commands_executors.CollectionWorker;
import functional_classes.commands_executors.CommandDistributor;
import functional_classes.commands_executors.FileWorker;
import functional_classes.database.DBWorker;
import functional_classes.threads.ServerReader;
import functional_classes.threads.ServerSerializer;
import functional_classes.threads.Synchronizer;
import movies_classes.Movies;

import java.util.Properties;

public class Server {
    private final Properties properties;

    public Server(Properties properties){
        this.properties = properties;
    }

    public void serverStartup() {
        try {
            DBWorker dbWorker = new DBWorker(properties);
            dbWorker.createConnection();
            dbWorker.executeQuery();
//        Movies movies = new Movies();
            FileWorker fileWorker = new FileWorker();
            Movies movies = fileWorker.fill();
            CollectionWorker collectionWorker = new CollectionWorker(movies);
            CommandDistributor commandDistributor = new CommandDistributor();
            commandDistributor.addExecutor("CollectionWorker", collectionWorker);
            commandDistributor.addExecutor("FileWorker", fileWorker);

            ServerReader serverReader = new ServerReader(commandDistributor);
            ServerSerializer serverSerializer = new ServerSerializer(commandDistributor);

            run(serverSerializer, serverReader);

        } catch (Exception | ExceptionInInitializerError e) {
            System.out.println(e);
        }
    }

    public static void run(ServerSerializer serverSerializer, ServerReader serverReader) {
        try {
            Synchronizer synchronizer = new Synchronizer();
            Thread t1 = new Thread(() -> {
                try {
                    synchronizer.consoleThread(serverReader);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            Thread t2 = new Thread(() -> {
                try {
                    synchronizer.clientChannelThread(serverSerializer);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            t2.start();
            t1.start();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
