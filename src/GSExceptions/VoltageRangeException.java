package GSExceptions;

public class VoltageRangeException extends Exception {

    public VoltageRangeException(String pString)
    {
        super(pString);
    }

    public VoltageRangeException(String pErrorMessage, Throwable pThrowable)
    {
        super(pErrorMessage, pThrowable);
    }

}