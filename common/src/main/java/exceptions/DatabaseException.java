package exceptions;

public class DatabaseException extends SuperException{
    public DatabaseException(String message){
        super(message);
    }

    public DatabaseException(){
        super("Введены неверные данные для базу данных");
    }
}
