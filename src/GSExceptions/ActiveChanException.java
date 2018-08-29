package GSExceptions;

public class ActiveChanException extends Exception{

    public ActiveChanException(String pString)
    {
        super(pString);
    }

    public ActiveChanException(String pErrorMessage, Throwable pThrowable)
    {
        super(pErrorMessage, pThrowable);
    }

}
