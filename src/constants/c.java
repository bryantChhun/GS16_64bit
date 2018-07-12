package constants;

import com.sun.jna.NativeLong;
import com.sun.jna.ptr.NativeLongByReference;

import java.util.List;

/**
 * @author Bryant Chhun
 */
public class c {

    // board parameters
    public static NativeLong ulNumBds, ulBdNum, numChan, id_off, eog, eof, disconnect;
    public static NativeLongByReference ulError, BuffPtr, NewBuffPtr;

    // board register addresses
    public static NativeLong BCR, Reserved, Reserved1, BUFFER_OPS, FW_REV, AUTO_CAL, OUTPUT_DATA_BUFFER, BUFFER_SIZE, BUFFER_THRSHLD, RATE_A, RATE_B;

    // input/output values from board
    public static NativeLong ValueRead, ValueRead1;
    public static List<NativeLong> ReadValue;

}
