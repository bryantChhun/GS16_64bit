package GS;

public class InterruptDeviceTypeException extends Exception {

    public InterruptDeviceTypeException(String pString) {
        super(pString);
    }

    public InterruptDeviceTypeException(String pErrorMessage, Throwable pThrowable) {
        super(pErrorMessage, pThrowable);
    }
}