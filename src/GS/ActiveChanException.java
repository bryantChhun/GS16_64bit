package GS;

public class ActiveChanException extends Throwable{

    public ActiveChanException(String pString)
    {
        super(pString);
    }

    public ActiveChanException(String pErrorMessage, Throwable pThrowable)
    {
        super(pErrorMessage, pThrowable);
    }

}
