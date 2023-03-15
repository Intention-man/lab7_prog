package functional_classes;

import auxiliary_classes.CommandMessage;
import auxiliary_classes.ResponseMessage;

import java.util.HashMap;
import java.util.Objects;

;

/**
 * Console App Component, responsible for console work and interaction with users
 * It calls CollectionWorker's and FileWorker's methods
 * It manages collection movies, as every functional components
 */

public class ClientManager {

    // initialization

    ClientReader clientReader;
    Writer writer;
    ClientSerializer clientSerializer;

    public ClientManager(ClientSerializer clientSerializer, ClientReader clientReader, Writer writer) {
        this.clientSerializer = clientSerializer;
        this.clientReader = clientReader;
        this.writer = writer;
    }

    // main action

    public void startNewAction(String clientInput) {
        try {
//            System.out.println("Начата новая команда");
            String[] splitedClientInput = clientInput.replaceAll("\\s+", " ").split(" ");
            String executedCommand = splitedClientInput[0];
            if (Objects.equals(executedCommand, "help")) {
                writer.help();
            } else {
                String param = "";
                if (splitedClientInput.length == 4) {
                    param = (String) splitedClientInput[1];
                }
                String login = splitedClientInput[splitedClientInput.length - 2];
                String password = splitedClientInput[splitedClientInput.length - 1];
                System.out.println(executedCommand + " " + login + " " + password);
                CommandMessage<Object> commandMessage;
                commandMessage = new CommandMessage<>("CollectionAnalyzer", "addCommandToHistory", executedCommand, login, password);
                clientSerializer.send(commandMessage);

                switch (executedCommand) {
                    case ("add") -> {
                        commandMessage = new CommandMessage<>("CollectionAnalyzer", "addMovie", clientReader.readInputNewMovieData(), login, password);
                        writer.printResponse(clientSerializer.send(commandMessage));
                    }
                    case ("add_if_max") -> {
                        commandMessage = new CommandMessage<>("CollectionAnalyzer", "addIfMax", clientReader.readInputNewMovieData(), login, password);
                        writer.printResponse(clientSerializer.send(commandMessage));
                    }
                    case ("add_if_min") -> {
                        commandMessage = new CommandMessage<>("CollectionAnalyzer", "addIfMin", clientReader.readInputNewMovieData(), login, password);
                        writer.printResponse(clientSerializer.send(commandMessage));
                    }
                    case ("clear") -> {
                        commandMessage = new CommandMessage<>("CollectionAnalyzer", "clear", null, login, password);
                        writer.printResponse(clientSerializer.send(commandMessage));
                    }
                    case ("count_by_oscars_count") -> {
                        if (param.matches("\\d*")) {
                            commandMessage = new CommandMessage<Object>("CollectionAnalyzer", "countByOscarsCount", Long.parseLong(param), login, password);
                            writer.printResponse(clientSerializer.send(commandMessage));
                        } else {
                            System.out.println("Количество оскаров должно быть целым числом");
                        }
                    }
                    case ("execute_script") -> clientReader.readFile(executedCommand.substring(15));
                    case ("history") -> {
                        commandMessage = new CommandMessage<>("CollectionAnalyzer", "getLast12Commands", null, login, password);
                        writer.printResponse(clientSerializer.send(commandMessage));
                    }
                    case ("info") -> {
                        commandMessage = new CommandMessage<>("CollectionAnalyzer", "info", null, login, password);
                        writer.printResponse(clientSerializer.send(commandMessage));
                        System.out.println("Исполняемые в данный момент файлы: " + clientReader.getExecutedFiles());
                    }
                    case ("show") -> {
                        commandMessage = new CommandMessage<>("CollectionAnalyzer", "show", null, login, password);
                        writer.printResponse(clientSerializer.send(commandMessage));
                    }
                    case ("sum_of_length") -> {
                        commandMessage = new CommandMessage<>("CollectionAnalyzer", "sumOfLength", null, login, password);
                        writer.printResponse(clientSerializer.send(commandMessage));
                    }
                    case ("registration") -> {
                        commandMessage = new CommandMessage<Object>("DBUserHandler", "registration", null, login, password);
                        ResponseMessage response = clientSerializer.send(commandMessage);
                        writer.printResponse(response);
                    }
                    case ("remove_by_id") -> {
                        if (param.matches("\\d*")) {
                            commandMessage = new CommandMessage<Object>("CollectionAnalyzer", "removeById", Integer.parseInt(param), login, password);
                            ResponseMessage response = clientSerializer.send(commandMessage);
                            writer.printResponse(response);
                        } else {
                            System.out.println("id должно быть целым числом");
                        }
                    }
                    case ("remove_any_by_oscars_count") -> {
                        if (param.matches("\\d*")) {
                            commandMessage = new CommandMessage<Object>("CollectionAnalyzer", "removeAnyByOscarsCount", Long.parseLong(param), login, password);
                            ResponseMessage response = clientSerializer.send(commandMessage);
                            writer.printResponse(response);
                        } else {
                            System.out.println("Количество оскаров должно быть целым числом");
                        }
                    }
                    case ("update") -> {
                        if (param.matches("\\d*") && Integer.parseInt(param) >= 0) {
                            HashMap<Integer, Object> map = clientReader.readInputNewMovieData();
                            map.put(map.size(), Integer.parseInt(param));
                            commandMessage = new CommandMessage<Object>("CollectionAnalyzer", "updateMovie", map, login, password);
                            ResponseMessage response = clientSerializer.send(commandMessage);
                            writer.printResponse(response);
                        } else {
                            System.out.println("id должно быть целым числом");
                        }
                    }
                    default -> System.out.println("Введите команду из доступного перечня");
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}