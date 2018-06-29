package scripts;

import com.sun.jna.NativeLong;


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
        example ex = new example();

        ex.AO64_Init_Test();

//        if(ex.auto_calibration().intValue() != 1)
//        {
//            System.out.println("Autocal failed");
//        }

        ex.close_handle();

        System.out.print("done with main");
    }

}