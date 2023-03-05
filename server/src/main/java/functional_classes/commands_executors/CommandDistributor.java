package functional_classes.commands_executors;

import auxiliary_classes.CommandMessage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class CommandDistributor {

    private HashMap<String, Object> executors = new HashMap<>();

    public <T> void addExecutor(String className, T executor){
        executors.put(className, executor);
    }

    public <T> T execution(CommandMessage commandMessage) {

        String className = commandMessage.getClassname();
        Object o;
        try {
            Class<?> c = Class.forName("functional_classes.commands_executors." + className);
            Method methodToInvoke = (commandMessage.getCommandData() != null ? c.getMethod(commandMessage.getCommandName(), commandMessage.getCommandData().getClass()) : c.getMethod(commandMessage.getCommandName()));

            if (commandMessage.getCommandData() != null) {
                o = (T) methodToInvoke.invoke(executors.get(className), commandMessage.getCommandData());
            } else {
                o = (T) methodToInvoke.invoke(executors.get(className));
            }

            return (T) o;
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | SecurityException |
                 IllegalArgumentException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}