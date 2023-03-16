package exceptions;

public class MessageFormatException extends SuperException{
    public MessageFormatException(String message){
        super(message);
    }

    public MessageFormatException(){
        super("Ваше сообщение должно выглядеть так: *команда*, *параметр(опционально)* *логин* *пароль*");
    }
}
