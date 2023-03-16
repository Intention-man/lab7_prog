package exceptions;


public class AuthorizationException extends SuperException{
    public AuthorizationException(String message){
        super(message);
    }

    public AuthorizationException(){
        super("Неуспешная попытка авторизации. Возможно, ошибка в логине или пароле");
    }
}
