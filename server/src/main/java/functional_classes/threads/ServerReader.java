package functional_classes.threads;


import auxiliary_classes.CommandMessage;
import functional_classes.commands_executors.CommandDistributor;
import movies_classes.Movies;

import java.util.Scanner;

public class ServerReader {
    public Scanner chosenScanner;
    CommandDistributor commandDistributor;

    public ServerReader(CommandDistributor commandDistributor) {
        chosenScanner = new Scanner(System.in);
        this.commandDistributor = commandDistributor;
    }

    public void readConsole() {
        if (chosenScanner.hasNextLine()) {
            if (chosenScanner.nextLine().trim().equals("exit")) {
                Movies result = commandDistributor.execution(new CommandMessage<>("CollectionWorker", "getMovies", null));
                boolean isSaved = commandDistributor.execution(new CommandMessage<>("FileWorker", "save", result));
                System.out.println(isSaved ? "Коллекция успешно сохранена!" : "Что-то пошло не так, коллекция не сохранена...");
                System.exit(0);
            }
        }
    }
}