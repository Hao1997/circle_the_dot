public class EmptyStackException extends RuntimeException {

    public EmptyStackException(){
        super();
    }

    public EmptyStackException(String message){
        super(message);
    }

    public EmptyStackException(String message, Throwable cause ){
        super(message, cause);
    }

}
