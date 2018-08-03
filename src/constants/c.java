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
 * constants used by the example.c program supplied by General Standards
 */
public class c {

    // board parameters
    public static NativeLong ulNumBds, ulBdNum, numChan, id_off, eog, eof, disconnect;
    public static NativeLongByReference ulError;

    // board register addresses
    public static NativeLong BCR, Reserved, Reserved1, BUFFER_OPS, FW_REV, AUTO_CAL, OUTPUT_DATA_BUFFER, BUFFER_SIZE, BUFFER_THRSHLD, RATE_A, RATE_B;

    // input/output values from board
    public static NativeLong ValueRead, ValueRead1;
    public static NativeLong[] ReadValue;
    public static NativeLong[] ulData; // this is an ARRAY of NativeLong.  Some examples have huge numbers of elements (0x20000)
    public static NativeLongByReference BuffPtr, NewBuffPtr;
    public static NativeLongByReference BuffPtr2;  //assign by allocating memory?
    public static Pointer testptr;


    // for DMA memory handling
    public static NativeLong LOCAL;
    public static NativeLong ulChannel;
    public static NativeLong ulWords;

}
