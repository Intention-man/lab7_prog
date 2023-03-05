package functional_classes;

import auxiliary_classes.CommandMessage;
import auxiliary_classes.ResponseMessage;

import java.util.HashMap;

;

/**
 * Console App Component, responsible for console work and interaction with users
 * It calls CollectionWorker's and FileWorker's methods
 * It manages collection movies, as every functional components
 */

public class ClientManager {

    // initialization

   ClientReader clientReader; Writer writer;  ClientSerializer clientSerializer;

    public ClientManager(ClientSerializer clientSerializer, ClientReader clientReader, Writer writer) {
        this.clientSerializer = clientSerializer;
        this.clientReader = clientReader;
        this.writer = writer;
    }

    // main action

    public void startNewAction(String executedCommand) {
        try {
            System.out.println("Начата новая команда");
            executedCommand = executedCommand.trim();
            CommandMessage<Object> commandMessage;
            commandMessage = new CommandMessage<Object>("CollectionWorker", "addCommandToHistory", executedCommand.split(" ")[0]);
            clientSerializer.send(commandMessage);

            switch (executedCommand.split(" ")[0]) {
                case ("add") -> {
                    commandMessage = new CommandMessage<Object>("CollectionWorker", "addMovie", clientReader.readInputNewMovieData());
                    writer.printResponse(clientSerializer.send(commandMessage));
                }
                case ("add_if_max") -> {
                    commandMessage = new CommandMessage<Object>("CollectionWorker", "addIfMax", clientReader.readInputNewMovieData());
                    writer.printResponse(clientSerializer.send(commandMessage));
                }
                case ("add_if_min") -> {
                    commandMessage = new CommandMessage<Object>("CollectionWorker", "addIfMin", clientReader.readInputNewMovieData());
                    writer.printResponse(clientSerializer.send(commandMessage));
                }
                case ("clear") -> {
                    commandMessage = new CommandMessage<>("CollectionWorker", "clear", null);
                    writer.printResponse(clientSerializer.send(commandMessage));
                }
                case ("count_by_oscars_count") -> {
                    commandMessage = new CommandMessage<Object>("CollectionWorker", "countByOscarsCount", Long.parseLong(executedCommand.split(" ")[1]));
                    writer.printResponse(clientSerializer.send(commandMessage));
                }
                case ("execute_script") -> clientReader.readFile(executedCommand.substring(15));
                case ("help") -> writer.help();
                case ("history") -> {
                    commandMessage = new CommandMessage<>("CollectionWorker", "getLast12Commands", null);
                    writer.printResponse(clientSerializer.send(commandMessage));
                }
                case ("info") -> {
                    commandMessage = new CommandMessage<>("CollectionWorker", "info", null);
                    writer.printResponse(clientSerializer.send(commandMessage));
                    System.out.println("Исполняемые в данный момент файлы: " + clientReader.getExecutedFiles());
                }
                case ("show") -> {
                    commandMessage = new CommandMessage<>("CollectionWorker", "show", null);
                    writer.printResponse(clientSerializer.send(commandMessage));
                }
                case ("sum_of_length") -> {
                    commandMessage = new CommandMessage<>("CollectionWorker", "sumOfLength", null);
                    writer.printResponse(clientSerializer.send(commandMessage));
                }
                case ("remove_by_id") -> {
                    if (executedCommand.matches("remove_by_id \\d*")) {
                        commandMessage = new CommandMessage<Object>("CollectionWorker", "removeById", Integer.parseInt(executedCommand.split(" ")[1]));
                        ResponseMessage response = clientSerializer.send(commandMessage);
                        if (response.getResponseData().equals(true)) {
                            writer.printResponse(response);
                        } else {
                            System.out.println("Фильма с таким id нет в коллекции");
                        }
                    } else {
                        System.out.println("id должно быть целым числом");
                    }
                }
                case ("remove_any_by_oscars_count") -> {
                    if (executedCommand.matches("remove_any_by_oscars_count \\d*")) {
                        commandMessage = new CommandMessage<Object>("CollectionWorker", "removeAnyByOscarsCount", Long.parseLong(executedCommand.split(" ")[1]));
                        ResponseMessage response = clientSerializer.send(commandMessage);
                        if (response.getResponseData().equals(true)) {
                            writer.printResponse(response);
                        } else {
                            System.out.println("В коллекци нет ни 1 фильма с таким количеством оскаров");
                        }
                    } else {
                        System.out.println("Количество оскаров должно быть целым числом");
                    }
                }
                case ("update") -> {
                    if (executedCommand.matches("update \\d*") && Integer.parseInt(executedCommand.split(" ")[1]) >= 0) {
                        HashMap<Integer, Object> map = clientReader.readInputNewMovieData();
                        map.put(map.size(), Integer.parseInt(executedCommand.split(" ")[1]));
                        commandMessage = new CommandMessage<Object>("CollectionWorker", "updateMovie", map);
                        ResponseMessage response = clientSerializer.send(commandMessage);
                        if (response.getResponseData().equals(true)) {
                            writer.printResponse(response);
                        } else {
                            System.out.println("В коллекци нет ни 1 фильма с таким id (введите add, чтобы создать)");
                        }
                    } else {
                        System.out.println("id должно быть целым числом");
                    }
                }
                default -> System.out.println("Введите команду из доступного перечня");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}