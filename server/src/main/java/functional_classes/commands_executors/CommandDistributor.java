package functional_classes.commands_executors;

import auxiliary_classes.CommandMessage;
import functional_classes.database.DBConnector;
import functional_classes.database.DBUserHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

public class CommandDistributor {

    DBConnector dbConnector;
    private HashMap<String, Object> executors = new HashMap<>();

    public <T> void addExecutor(String className, T executor){
        executors.put(className, executor);
    }

    public <T> T execution(CommandMessage commandMessage) {
        String className = commandMessage.getClassname();
        System.out.println(className + " " + commandMessage.getCommandName());
        Object o;
        Method methodToInvoke;
        try {
            Class<?> c = Class.forName("functional_classes." + ((!className.contains(".")) ? ((Objects.equals(className, "CollectionAnalyzer")) ? "commands_executors." + className : "database." + className) : className));
            boolean letExecute = false;
            if (Objects.equals(commandMessage.getCommandName(), "registration")){
                letExecute = true;
                methodToInvoke = c.getMethod(commandMessage.getCommandName(), commandMessage.getLogin().getClass(), commandMessage.getPassword().getClass());
            } else if (((DBUserHandler) executors.get("DBUserHandler")).isUserExist(commandMessage.getLogin(), commandMessage.getPassword())) {
                letExecute = true;
                ((CollectionAnalyzer) executors.get("CollectionAnalyzer")).setCurrentLogin(commandMessage.getLogin());
                methodToInvoke = (commandMessage.getCommandData() != null ? c.getMethod(commandMessage.getCommandName(), commandMessage.getCommandData().getClass()) : c.getMethod(commandMessage.getCommandName()));
            } else {return (T) "Неуспешная попытка авторизации. Возможно, ошибка в логине или пароле";}

            if (commandMessage.getCommandData() != null) {
                o = (T) methodToInvoke.invoke(executors.get(className), commandMessage.getCommandData());
            } else if (Objects.equals(commandMessage.getCommandName(), "registration")){
                o = ((DBUserHandler) executors.get("DBUserHandler")).registration(commandMessage.getLogin(), commandMessage.getPassword());
            }  else {
                o = (T) methodToInvoke.invoke(executors.get(className));
            }
            return (T) o;
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | SecurityException |
                 IllegalArgumentException |
                 InvocationTargetException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void setDbManager(DBConnector dbConnector) {
        this.dbConnector = dbConnector;
    }
}