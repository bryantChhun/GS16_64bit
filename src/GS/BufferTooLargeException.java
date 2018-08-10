package GS;

public class BufferTooLargeException extends Throwable {

    public BufferTooLargeException(String pString)
    {
        super(pString);
    }

    public BufferTooLargeException(String pErrorMessage, Throwable pThrowable)
    {
        super(pErrorMessage, pThrowable);
    }

}