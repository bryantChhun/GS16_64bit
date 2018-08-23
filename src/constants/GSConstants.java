package constants;

import com.sun.jna.NativeLong;
import com.sun.jna.ptr.NativeLongByReference;

/**
 * @author Bryant Chhun
 */

/**
 * constants used by the example.GSConstants program supplied by General Standards
 */
public class GSConstants {

    // board parameters
    public static NativeLong ulNumBds, ulBdNum, numChan, id_off, eog, eof, disconnect;
    public static NativeLongByReference ulError;

    // board register addresses
    public static NativeLong BCR, Reserved, Reserved1, BUFFER_OPS, FW_REV, AUTO_CAL, OUTPUT_DATA_BUFFER, BUFFER_SIZE, BUFFER_THRSHLD, RATE_A, RATE_B;

    // input/output values from board
    public static NativeLong ValueRead;
    public static NativeLong[] ReadValue;
    public static NativeLongByReference BuffPtr;

    // for DMA memory handling
    public static NativeLong InterruptType;
    public static NativeLong ulChannel;
    public static NativeLong ulWords;

    /**
     * used by tests when board is not present for initialization
     */
    public GSConstants()
    {
        id_off = new NativeLong(24);
        eog = new NativeLong(30);
        eof = new NativeLong(31);
    }
}
