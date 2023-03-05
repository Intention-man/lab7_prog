package exceptions;

public abstract class SuperException extends Exception{
    public SuperException(String message) {
        System.out.println(message);
    }
}
