package scripts;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.NativeLongByReference;
import constants.c;
import operations.*;

/**
 * @author Bryant Chhun
 */
public class Main {

    /**
     * testing order
     * 1) set board number (print statement)
     * 2) get handle
     * 3) register data from device, set channels, values etc.)
     * 4) print ('no aux board')
     * == Below are the tests ==
     * == will not require user input ==
     *
     * 5) BOARD INIT TEST
     * 6) AUTOCALIB and read back firmware
     * 7) Output channels basic
     * 8) Sequential Direct Output
     * 9) Simultaneous Direct Output
     * 10) Continuous Function Output
     * 11) Periodic Function Output
     * 12) Function Burst output
     * 13) Function Sequencing Output
     * 14) Multiboard Continuous Function output
     * 15) exit
     */

    public static void main(String[] args) {

        // initialize board with findboard + gethandle
        //example ex = new example();

        // set default values
        //new AO64_Init_test(ex.INSTANCE);

        // write all channels with basic outputs
        //new AO64_Basic_output_test(ex.INSTANCE, ex);

        // write all channels sequentially
        //new AO64_Sequential_Direct(ex.INSTANCE, ex);

        // write all channels simultaneously
        //new AO64_Simultaneous_Direct(ex.INSTANCE, ex);

        // write 16 channels with square wave?
        //new AO64_Continuous_Function(ex.INSTANCE, ex);

        Pointer ex8p = new Memory(64);
        NativeLong dataval = new NativeLong(); dataval.setValue(0xFF);
        ex8p.setNativeLong(0, dataval);

//        for(int i=0; i< 1000; i++){
//            NativeLong dataval = new NativeLong(); dataval.setValue(0xFFFF);
//            // should offset = loop*Native.getNativeSize(NativeLong.class) + i ???
//            ex8p.setNativeLong( i, dataval);
//        }

        System.out.println("ex8p class = " + ex8p.getClass().toString());
        System.out.println("ex8p size = " + ((Memory) ex8p).size());
        System.out.println("ex8p val 0 = " + ex8p.getNativeLong(0).longValue());
        System.out.println("ex8p val 1 = " + ex8p.getNativeLong(1).longValue());
//        System.out.println("ex8p val 7 = " + ex8p.getNativeLong(2).longValue());
//        System.out.println("ex8p val 11 = " + ex8p.getNativeLong(11).longValue());
//        System.out.println("ex8p val 15 = " + ex8p.getNativeLong(15).longValue());
//        System.out.println("ex8p val 16 = " + ex8p.getNativeLong(16).longValue());
//        System.out.println("ex8p val 19 = " + ex8p.getNativeLong(19).longValue());
//        System.out.println("ex8p val 23 = " + ex8p.getNativeLong(23).longValue());
//        System.out.println("ex8p val 27 = " + ex8p.getNativeLong(27).longValue());
//        System.out.println("ex8p val 31 = " + ex8p.getNativeLong(31).longValue());
        NativeLongByReference bufftest = new NativeLongByReference();
        bufftest.setPointer(ex8p);

        //ex.close_handle();

        System.out.print("done with main");
    }

}