package GS;

public class InterruptValueException extends Exception {

    public InterruptValueException(String pString) {
        super(pString);
    }

    public InterruptValueException(String pErrorMessage, Throwable pThrowable) {
        super(pErrorMessage, pThrowable);
    }
}