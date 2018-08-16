package constants;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;

import java.util.List;

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
    public static NativeLong ValueRead, ValueRead1;
    public static NativeLong[] ReadValue;
    public static NativeLong[] ulData; // this is an ARRAY of NativeLong.
    public static NativeLongByReference BuffPtr, NewBuffPtr;
    public static Pointer testptr;

    // for DMA memory handling
    public static NativeLong LOCAL;
    public static NativeLong ulChannel;
    public static NativeLong ulWords;

    public GSConstants()
    {
        id_off = new NativeLong();
        id_off.setValue(24);
        eog = new NativeLong();
        eog.setValue(30);
        eof = new NativeLong();
        eof.setValue(31);
    }
}
