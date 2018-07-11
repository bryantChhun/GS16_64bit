package scripts;

import com.sun.jna.NativeLong;
import operations.*;

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
        example ex = new example();

        // set default values
        AO64_Init_test init = new AO64_Init_test(ex.INSTANCE, ex.ulBdNum, ex.ulError);

        // write all channels with basic outputs
        AO64_Basic_output_test bo_test = new AO64_Basic_output_test(ex.INSTANCE, ex.ulBdNum, ex.ulError);

        //
        ex.close_handle();

        System.out.print("done with main");
    }

}