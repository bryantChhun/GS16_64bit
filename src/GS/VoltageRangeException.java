package GS;

public class VoltageRangeException extends Throwable {

    public VoltageRangeException(String pString)
    {
        super(pString);
    }

    public VoltageRangeException(String pErrorMessage, Throwable pThrowable)
    {
        super(pErrorMessage, pThrowable);
    }

}