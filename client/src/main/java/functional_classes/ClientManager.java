package functional_classes;

import auxiliary_classes.CommandMessage;
import auxiliary_classes.ResponseMessage;
import exceptions.MessageFormatException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
            List<String> splitedClientInput = Arrays.asList(clientInput.split("\\s+"));
            String executedCommand = splitedClientInput.get(0);
            if (Objects.equals(executedCommand, "help")) {
                writer.help();
            } else {
                String param = "";
                if (splitedClientInput.size() <= 2) {
                    throw new MessageFormatException();
                }
                if (splitedClientInput.size() >= 4) {
                    param = String.join(" ", splitedClientInput.subList(1, splitedClientInput.size() - 2));
                    System.out.println(param);
                }
                String login = splitedClientInput.get(splitedClientInput.size() - 2);
                String password = splitedClientInput.get(splitedClientInput.size() - 1);
//                System.out.println(executedCommand + " " + login + " " + password);
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
                        commandMessage = new CommandMessage<>("CollectionAnalyzer", "clear", login, password);
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
                    case ("execute_script") -> clientReader.readFile(param);
                    case ("history") -> {
                        commandMessage = new CommandMessage<>("CollectionAnalyzer", "getLast12Commands", login, password);
                        writer.printResponse(clientSerializer.send(commandMessage));
                    }
                    case ("info") -> {
                        commandMessage = new CommandMessage<>("CollectionAnalyzer", "info", login, password);
                        writer.printResponse(clientSerializer.send(commandMessage));
                        System.out.println("Исполняемые в данный момент файлы: " + clientReader.getExecutedFiles());
                    }
                    case ("show") -> {
                        commandMessage = new CommandMessage<>("CollectionAnalyzer", "show", login, password);
                        writer.printResponse(clientSerializer.send(commandMessage));
                    }
                    case ("sum_of_length") -> {
                        commandMessage = new CommandMessage<>("CollectionAnalyzer", "sumOfLength", login, password);
                        writer.printResponse(clientSerializer.send(commandMessage));
                    }
                    case ("registration") -> {
                        commandMessage = new CommandMessage<Object>("DBUserHandler", "registration", login, password);
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
        } catch (NumberFormatException e) {
            System.out.println("Убедитесь, что ввели команду, числовой аргумент (если он нужен для выполнения этой команды), логин и пароль");
        } catch (MessageFormatException ignored) {
        } catch (Exception e) {
            System.out.println("Что-то пошло не так. ПРоверьте корректность данных");
        }
    }
}