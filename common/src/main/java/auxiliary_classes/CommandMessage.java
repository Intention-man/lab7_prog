package auxiliary_classes;

import java.io.Serializable;

public class CommandMessage<T> implements Serializable {
    private String commandName;
    private String classname;
    private T commandData;

    public CommandMessage(String classname, String commandName, T commandData) {
        this.commandName = commandName;
        this.classname = classname;
        this.commandData = commandData;
    }

    public String getClassname() {
        return classname;
    }

    public String getCommandName() {
        return commandName;
    }

    public T getCommandData() {
        return commandData;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public void setCommandData(T commandData) {
        this.commandData = commandData;
    }
}
