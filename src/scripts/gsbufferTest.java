package scripts;

import GS.ActiveChanException;
import GS.BufferTooLargeException;
import GS.GSBuffer;
import GS.VoltageRangeException;
import constants.GSConstants;


public class gsbufferTest {

    private static GSBuffer buffertest;
    private static GSConstants gsconst;

    public static void main(String[] args) {

        try {
            buffertest = new GSBuffer(gsconst, 2000, 64);
        } catch (BufferTooLargeException ex) {System.out.println("voltage to int conversion: "+ex);}

        try {
            buffertest.appendValue(-0.5,1);
        } catch(ActiveChanException ex) {} catch (VoltageRangeException vex) {System.out.println("voltage to int conversion: "+vex);}

        System.out.println("all done!");
        System.exit(0);
    }

}
