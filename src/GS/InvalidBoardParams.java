package GS;

public class InvalidBoardParams extends Exception{

    public InvalidBoardParams(String pString)
    {
        super(pString);
    }

    public InvalidBoardParams(String pErrorMessage, Throwable pThrowable)
    {
        super(pErrorMessage, pThrowable);
    }

}