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
        //NativeLong out = ex.find_boards();
        ex.AO64_Init_Test();
        //NativeLong out = ex.auto_cal();
        //System.out.println("findboards output = "+out.toString());
        //System.out.println(ex.get_handle());

       // NativeLong out = ex.find_boards();
       // System.out.println(out.toString());

//        System.out.println("testing bytes and int");
//        int a = 0;
//        byte a_ = (byte)a;
//        int c = 0xAA;
//        byte[] d = new byte[] {0x14};
//        int d_ = (int)0x14;
//
//        System.out.println(a);
//        System.out.println(a_);
//        System.out.println(c);
//        System.out.println("c to hex = "+Integer.toHexString(c));
//        System.out.println(d);
//        System.out.println(d_);

        System.out.print("done with main");
    }

}