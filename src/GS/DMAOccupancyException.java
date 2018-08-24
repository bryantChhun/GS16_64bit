package GS;

public class DMAOccupancyException extends Exception {

    public DMAOccupancyException(String pString) {
        super(pString);
    }

    public DMAOccupancyException(String pErrorMessage, Throwable pThrowable) {
        super(pErrorMessage, pThrowable);
    }
}