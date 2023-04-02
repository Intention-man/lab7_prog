package functional_classes.threads;


import functional_classes.Server;
import functional_classes.commands_executors.CommandDistributor;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class ServerReader {
    public Scanner chosenScanner;
    CommandDistributor commandDistributor;

    public ServerReader(CommandDistributor commandDistributor) {
        chosenScanner = new Scanner(System.in);
        this.commandDistributor = commandDistributor;
    }

    public void readConsole(Server server) throws SQLException, IOException {
        while (chosenScanner.hasNextLine()) {
            String clientInput = chosenScanner.nextLine().trim();
            String[] splitedClientInput = clientInput.replaceAll("\\s+", " ").split(" ");
            String executedCommand = splitedClientInput[0];
            if (executedCommand.equals("exit")) {
                server.closeAllAndExit();
            }
        }
    }
}