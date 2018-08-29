package GSExceptions;

public class InvalidBoardParamsException extends Exception{

    public InvalidBoardParamsException(String pString)
    {
        super(pString);
    }

    public InvalidBoardParamsException(String pErrorMessage, Throwable pThrowable)
    {
        super(pErrorMessage, pThrowable);
    }

}