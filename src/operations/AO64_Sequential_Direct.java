package operations;

import bindings.AO64_64b_Driver_CLibrary;
import com.sun.jna.NativeLong;
import constants.c;
import scripts.example;

/**
 * @author Bryant Chhun
 */
public class AO64_Sequential_Direct {

    AO64_64b_Driver_CLibrary lINSTANCE;
    example lex;

    public AO64_Sequential_Direct(AO64_64b_Driver_CLibrary INSTANCE, example ex){

        lINSTANCE= INSTANCE;
        lex = ex;

        System.out.println("\nSequential direct outputs :");

        System.out.println("Intializing the board");
        lINSTANCE.AO64_66_Initialize(c.ulBdNum, c.ulError);
        System.out.println("Initialization Complete");

        System.out.println("Autocalibrating the board");
        if(lINSTANCE.AO64_66_Autocal(c.ulBdNum, c.ulError).intValue() != 1)
        {
            System.out.println("Autocal Failed");
            System.exit(1);
        } else {
            System.out.println("Autocal Passed");
        }

        sequence();

    }

    /**
     * just like "walking" sequence from Basic Output Test.
     */
    public void sequence(){

        NativeLong val = new NativeLong();

        // enable clock
        lINSTANCE.AO64_66_Enable_Clock(c.ulBdNum, c.ulError);

        // connect to all outputs
        lex.AO64_Connect_Outputs();
        System.out.println("Please verify that all channels are now at Zero Volts");
        try { System.in.read(); } catch (Exception ex) { System.out.println(ex); }

        // write buffer
        for(int i=0; i < c.numChan.intValue(); i++){
            val.setValue( (i << c.id_off.intValue()) | (1 << c.eog.intValue()) | 0xC000 );
            lINSTANCE.AO64_66_Write_Local32(c.ulBdNum, c.ulError, c.OUTPUT_DATA_BUFFER, val);
            System.out.println(String.format("Please verify that Chan %02d is now at Half PFS...", i));
            try { System.in.read(); } catch (Exception ex) { System.out.println(ex); }
        }

        lex.reset_output_to_zero();

        // disable clock
        lINSTANCE.AO64_66_Disable_Clock(c.ulBdNum, c.ulError);

    }
}
