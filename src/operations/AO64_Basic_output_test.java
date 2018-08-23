package operations;

import bindings.AO64_64b_Driver_CLibrary;
import com.sun.jna.NativeLong;
import constants.GSConstants;
import scripts.example;

/**
 * @author Bryant Chhun
 */
public class AO64_Basic_output_test {

    private AO64_64b_Driver_CLibrary lINSTANCE;
    private example lex;

    /**
     * Constructor runs initialization, autocalibration, then walks through each
     * of the example "basic outputs", with a pause in between for you to measure the values
     * @param INSTANCE
     */
    public AO64_Basic_output_test(AO64_64b_Driver_CLibrary INSTANCE, example ex)
    {
        lINSTANCE = INSTANCE;
        lex = ex;

        System.out.println("\nOutput Channels basic operation and shorts:");

        System.out.println("Intializing the board");
        GSConstants.BCR = new NativeLong();
        GSConstants.BCR.setValue(0x00);
        NativeLong val_1 = new NativeLong();
        val_1.setValue(0x8000);
        NativeLong val_2 = new NativeLong();
        val_2.setValue(0x0030);
        lINSTANCE.AO64_66_Write_Local32(GSConstants.ulBdNum, GSConstants.ulError, GSConstants.BCR, val_1);
        System.out.println("Initialization Complete");

        System.out.println("Autocalibrating the board");
        if(lINSTANCE.AO64_66_Autocal(GSConstants.ulBdNum, GSConstants.ulError).intValue() != 1)
        {
            System.out.println("Autocal Failed");
            System.exit(1);
        } else {
            System.out.println("Autocal Passed");
        }

        System.out.println("Setting Offset Binary and Rate A Generator to HIGH");
        lINSTANCE.AO64_66_Write_Local32(GSConstants.ulBdNum, GSConstants.ulError, GSConstants.BCR, val_2);

        System.out.println("Setting all channels to 0x0020");
        set_AO64_to_0020();
        System.out.println("Setting all channels to 0x8000");
        set_AO64_to_8000();
        System.out.println("Setting all channels to 0xFFE0");
        set_AO64_to_FFE0();
        System.out.println("Setting all channels to walking one pattern");
        set_AO64_to_walking();

    }

    /**
     * this example uses only "Write_Local" to control all aspects of the board
     */
    private void set_AO64_to_FFE0()
    {
        NativeLong ValueRead = new NativeLong();
        GSConstants.OUTPUT_DATA_BUFFER = new NativeLong();
        GSConstants.OUTPUT_DATA_BUFFER.setValue(0x18);

        //Enable clocking
        NativeLong BUFFER_OPS = new NativeLong();
        BUFFER_OPS.setValue(0x0C);
        NativeLong val = new NativeLong();
        val.setValue(0x0020);
        // set clock bit to high
        lINSTANCE.AO64_66_Write_Local32(GSConstants.ulBdNum, GSConstants.ulError, BUFFER_OPS, val);

        //populate the output data buffer
        /* I interpret the following data buffer code as:
         * "iterate through all channels, for each channel write a bit-wise value corresponding to channel and shift it by id_off value"
         * "next, do bitwise OR with 0xFFE0 to append actual signal data to bits 0-16."
         * These are all instantly sent from buffer to outputs because clock is enabled BEFORE we load buffer
         */
        // write data to buffer, simultaneous clocking
        for(int cntr = 0; cntr < GSConstants.numChan.intValue() ; cntr++){
            ValueRead.setValue( (0xFFE0 | (cntr << GSConstants.id_off.intValue()) ) );
            if(cntr == (GSConstants.numChan.intValue() - 1)){
                // I think this is right.  Example uses |= which I have broken into two components
                // This reads as "insert a 1 = HIGH bit using bitwise OR and bitshift by eog=30.  This is the last channel tag
                ValueRead.setValue(ValueRead.intValue() | (1 << GSConstants.eog.intValue()));
            }
            lINSTANCE.AO64_66_Write_Local32(GSConstants.ulBdNum, GSConstants.ulError, GSConstants.OUTPUT_DATA_BUFFER, ValueRead);
        }

        // reset output values to midscale
        lex.reset_output_to_zero();

        // set clock bit to low
        val.setValue(0x0000);
        lINSTANCE.AO64_66_Write_Local32(GSConstants.ulBdNum, GSConstants.ulError, BUFFER_OPS, val);

    }

    /**
     * this example uses "Enable_Clock" and "Disable_Clock" instead of direct bit writing
     */
    private void set_AO64_to_0020()
    {
        // enable clocking
        lINSTANCE.AO64_66_Enable_Clock(GSConstants.ulBdNum, GSConstants.ulError);
        GSConstants.ValueRead = new NativeLong();

        // write values to buffer, Simultaneous clocking
        for(int cntr = 0; cntr < GSConstants.numChan.intValue(); cntr++ ){
            GSConstants.ValueRead.setValue(0x0020 | (cntr << GSConstants.id_off.intValue()));
            if(cntr == (GSConstants.numChan.intValue() - 1)){
                GSConstants.ValueRead.setValue(GSConstants.ValueRead.intValue() | (1 << GSConstants.eog.intValue()));
            }
            lINSTANCE.AO64_66_Write_Local32(GSConstants.ulBdNum, GSConstants.ulError, GSConstants.OUTPUT_DATA_BUFFER, GSConstants.ValueRead);
        }

        // pause, then reset output values.
        lex.reset_output_to_zero();

        // disable clocking
        lINSTANCE.AO64_66_Disable_Clock(GSConstants.ulBdNum, GSConstants.ulError);

    }

    private void set_AO64_to_8000()
    {
        // enable clocking
        lINSTANCE.AO64_66_Enable_Clock(GSConstants.ulBdNum, GSConstants.ulError);
        GSConstants.ValueRead = new NativeLong();

        // write values to buffer, Simultaneous clocking
        for(int cntr = 0; cntr < GSConstants.numChan.intValue(); cntr++ ){
            GSConstants.ValueRead.setValue(0x8000 | (cntr << GSConstants.id_off.intValue()));
            if(cntr == (GSConstants.numChan.intValue() - 1)){
                GSConstants.ValueRead.setValue(GSConstants.ValueRead.intValue() | (1 << GSConstants.eog.intValue()));
            }
            lINSTANCE.AO64_66_Write_Local32(GSConstants.ulBdNum, GSConstants.ulError, GSConstants.OUTPUT_DATA_BUFFER, GSConstants.ValueRead);
        }

        // pause, then reset output values.
        lex.reset_output_to_zero();

        // disable clocking
        lINSTANCE.AO64_66_Disable_Clock(GSConstants.ulBdNum, GSConstants.ulError);

    }

    /**
     * this example uses Enable and Disable clock.
     * It also achieves a walking pattern using SEQUENTIAL clocking
     *  this means all channels get an EOG tag, stopping the output, requiring another trigger to continue.
     */
    private void set_AO64_to_walking()
    {
        lINSTANCE.AO64_66_Enable_Clock(GSConstants.ulBdNum, GSConstants.ulError);
        NativeLong val = new NativeLong();

        for(int loop = 0; loop < GSConstants.numChan.intValue() ; loop++){
            for(int cntr = 0; cntr < GSConstants.numChan.intValue(); cntr++){
                // set last chan tag for EVERY channel, SEQUENTIAL clocking
                GSConstants.ValueRead.setValue( (cntr << GSConstants.id_off.intValue()) | (1 << GSConstants.eog.intValue()) );    // SEQUENTIAL clocking
                if(loop == cntr){
                    val.setValue(GSConstants.ValueRead.intValue() | 0xFFE0);
                    lINSTANCE.AO64_66_Write_Local32(GSConstants.ulBdNum, GSConstants.ulError, GSConstants.OUTPUT_DATA_BUFFER, val);
                } else {
                    val.setValue(GSConstants.ValueRead.intValue() | 0x8000);
                    lINSTANCE.AO64_66_Write_Local32(GSConstants.ulBdNum, GSConstants.ulError, GSConstants.OUTPUT_DATA_BUFFER, val);
                }
            }
            System.out.println(String.format("Verify that Ch %02d set to FFE0, and others are midscale or zero", loop));
            try { System.in.read(); } catch (Exception ex) { System.out.println(ex); }
        }

        val.setValue(GSConstants.ValueRead.intValue() | 0x8000);
        lINSTANCE.AO64_66_Write_Local32(GSConstants.ulBdNum, GSConstants.ulError, GSConstants.OUTPUT_DATA_BUFFER, val);

        lINSTANCE.AO64_66_Disable_Clock(GSConstants.ulBdNum, GSConstants.ulError);

    }


}
