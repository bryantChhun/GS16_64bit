package operations;

import bindings.AO64_64b_Driver_CLibrary;
import constants.GSConstants;
import scripts.example;
import com.sun.jna.NativeLong;

/**
 * @author Bryant Chhun
 */
public class AO64_Simultaneous_Direct {

    AO64_64b_Driver_CLibrary lINSTANCE;
    example lex;

    public AO64_Simultaneous_Direct(AO64_64b_Driver_CLibrary INSTANCE, example ex){

        lINSTANCE= INSTANCE;
        lex = ex;

        System.out.println("\nSimultaneous Direct outputs");

        System.out.println("Intializing the board");
        lINSTANCE.AO64_66_Initialize(GSConstants.ulBdNum, GSConstants.ulError);
        System.out.println("Initialization Complete");

        System.out.println("Autocalibrating the board");
        if(lINSTANCE.AO64_66_Autocal(GSConstants.ulBdNum, GSConstants.ulError).intValue() != 1)
        {
            System.out.println("Autocal Failed");
            System.exit(1);
        } else {
            System.out.println("Autocal Passed");
        }

        simultaneous();

    }

    public void simultaneous(){

        GSConstants.ValueRead = new NativeLong();

        lINSTANCE.AO64_66_Enable_Clock(GSConstants.ulBdNum, GSConstants.ulError);
        lex.AO64_Connect_Outputs();

        System.out.println(String.format("BCR Reads: %s", lex.nativelong_to_hex(lINSTANCE.AO64_66_Read_Local32(GSConstants.ulBdNum, GSConstants.ulError, GSConstants.BCR))));
        System.out.println(String.format("BUFF_OP READS: %s", lex.nativelong_to_hex(lINSTANCE.AO64_66_Read_Local32(GSConstants.ulBdNum, GSConstants.ulError, GSConstants.BUFFER_OPS))));

        System.out.println("Please verify that all Channels are now at Zero Volts");
        try { System.in.read(); } catch (Exception ex) { System.out.println(ex); }

        for(int i = 0; i < GSConstants.numChan.intValue() ; i++){
            System.out.println(String.format("Loading Value for Channel %02d", i));
            GSConstants.ValueRead.setValue( (i<<GSConstants.id_off.intValue()) | 0xC000 );
            if(i == (GSConstants.numChan.intValue() - 1)){
                GSConstants.ValueRead.setValue( GSConstants.ValueRead.intValue() | (1 << GSConstants.eog.intValue()));
            }
            lINSTANCE.AO64_66_Write_Local32(GSConstants.ulBdNum, GSConstants.ulError, GSConstants.OUTPUT_DATA_BUFFER, GSConstants.ValueRead);
            if(i != (GSConstants.numChan.intValue() - 1)){
                System.out.println("Please Verify that all Channels remain at Zero Volts");
                try { System.in.read(); } catch (Exception ex) { System.out.println(ex); }
            } else {
                System.out.println("Please Verify that all Channels are now at Half PFS");
                try { System.in.read(); } catch (Exception ex) { System.out.println(ex); }
            }
        }

        lex.reset_output_to_zero();

        lINSTANCE.AO64_66_Disable_Clock(GSConstants.ulBdNum, GSConstants.ulError);
    }
}
