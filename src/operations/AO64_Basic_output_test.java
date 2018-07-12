package operations;

import bindings.AO64_64b_Driver_CLibrary;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.NativeLongByReference;

public class AO64_Basic_output_test {

    public NativeLong ulNumBds, ulBdNum, numChan, id_off, eog, eof;
    public NativeLong BCR, Reserved, Reserved1, BUFFER_OPS, FW_REV, AUTO_CAL, OUTPUT_DATA_BUFFER, BUFFER_SIZE, BUFFER_THRSHLD, RATE_A, RATE_B;
    public NativeLongByReference ulError;
    private AO64_64b_Driver_CLibrary lINSTANCE;

    public AO64_Basic_output_test(AO64_64b_Driver_CLibrary INSTANCE, NativeLong ulBdNum, NativeLongByReference ulError)
    {
        lINSTANCE = INSTANCE;
        System.out.println("\nOutput Channels basic operation and shorts:");

        System.out.println("Intializing the board");
        BCR = new NativeLong();
        BCR.setValue(0x00);
        NativeLong val_1 = new NativeLong();
        val_1.setValue(0x8000);
        NativeLong val_2 = new NativeLong();
        val_2.setValue(0x0030);

        lINSTANCE.AO64_66_Write_Local32(ulBdNum, ulError, BCR, val_1);
        System.out.println("Initialization Complete");
        System.out.println("Autocalibrating the board");
        if(lINSTANCE.AO64_66_Autocal(ulBdNum, ulError).intValue() != 1)
        {
            System.out.println("Autocal Failed");
        } else {
            System.out.println("Autocal Passed");
        }

        System.out.println("Setting Offset Binary and Rate A Generator to HIGH");
        lINSTANCE.AO64_66_Write_Local32(ulBdNum, ulError, BCR, val_2);

        System.out.println("Setting all channels to 0x0020");
        System.out.println("Setting all channels to 0x8000");
        System.out.println("Setting all channels to 0xFFE0");
        System.out.println("Setting all channels to walking one pattern");

    }

    private void set_AO64_to_0020()
    {
        NativeLong val = new NativeLong();
        val.setValue(0x0020);
        lINSTANCE.AO64_66_Write_Local32(ulBdNum, ulError, BCR, val);
    }

    private void set_AO64_to_8000()
    {

    }

    private void set_AO64_to_FFE0()
    {
        int cntr;
        NativeLong ValueRead = new NativeLong();
        OUTPUT_DATA_BUFFER = new NativeLong();
        OUTPUT_DATA_BUFFER.setValue(0x18);

        //Enable clocking
        System.out.println("Setting all channels to FFE0, enable clocking");
        NativeLong BUFFER_OPS = new NativeLong();
        BUFFER_OPS.setValue(0x0C);
        NativeLong val = new NativeLong();
        val.setValue(0x0020);
        // set clock bit to high
        lINSTANCE.AO64_66_Write_Local32(ulBdNum, ulError, BUFFER_OPS, val);

        //populate the output data buffer
        /* I interpret the following data buffer code as:
         * "iterate through all channels, for each channel write a bit-wise value corresponding to channel and shift it by id_off value"
         * "next, do bitwise OR with 0xFFE0 to append actual signal data to bits 0-16."
         * These are all instantly sent to the outputs because clock is enabled BEFORE we load buffer
         */
        System.out.println("Setting all channels to FFE0, writing values");
        for(cntr=0 ; cntr < numChan.intValue() ; cntr++){
            ValueRead.setValue( (0xFFE0 | (cntr << id_off.intValue()) ) );
            if(cntr == (numChan.intValue() - 1)){
                // I think this is right.  Example uses |= which I have broken into two components
                // This reads as "insert a 1 = HIGH bit using bitwise OR and bitshift by eog=30.  This is the last channel tag
                ValueRead.setValue(ValueRead.intValue() | (1 << eog.intValue()));
            }
            lINSTANCE.AO64_66_Write_Local32(ulBdNum, ulError, OUTPUT_DATA_BUFFER, ValueRead);
        }

        System.out.println("Setting all channels to FFE0, end of write.  Press key to reset");
        try { System.in.read(); } catch (Exception ex) { System.out.println(ex); }

        // reset output values to midscale
        for(cntr=0 ; cntr < numChan.intValue() ; cntr++){
            lINSTANCE.AO64_66_Write_Local32(ulBdNum, ulError, OUTPUT_DATA_BUFFER, ReadValue[cntr]);
        }

        // set clock bit to low
        val.setValue(0x0000);
        lINSTANCE.AO64_66_Write_Local32(ulBdNum, ulError, BUFFER_OPS, val);

    }

    private void set_AO64_to_walking()
    {

    }

    public NativeLong auto_calibration(AO64_64b_Driver_CLibrary INSTANCE)
    {
        NativeLong out = INSTANCE.AO64_66_Autocal(ulBdNum, ulError);
        return out;
    }
}
