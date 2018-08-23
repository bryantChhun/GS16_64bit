package operations;

import bindings.AO64_64b_Driver_CLibrary;
import constants.GSConstants;
import com.sun.jna.NativeLong;

/**
 * @author Bryant Chhun
 */
public class AO64_Init_test {

    private AO64_64b_Driver_CLibrary lINSTANCE;

    /**
     * Constructor sets defaults for board register locations
     *
     * @param INSTANCE Refers to JNA interface class
     */
    public AO64_Init_test(AO64_64b_Driver_CLibrary INSTANCE)
    {
        lINSTANCE = INSTANCE;
        System.out.println("\nChecking board Initialization Defaults");

        // java converts hex to int easily.  JNA converts NativeLong to hex
        // this could be easier if we changed the binding from NativeLong to Int, removing the need to create a NativeLong object.
        // also means we could use all constants defined in the Bindings interface, per INSTANCE.  Rather than re-define per class here.
        GSConstants.BCR = new NativeLong();
        GSConstants.BCR.setValue(0x00);
        GSConstants.RATE_A = new NativeLong();
        GSConstants.RATE_A.setValue(0x24);
        GSConstants.BUFFER_OPS = new NativeLong();
        GSConstants.BUFFER_OPS.setValue(0x0C);
        GSConstants.FW_REV = new NativeLong();
        GSConstants.FW_REV.setValue(0x10);

        // Reset board to defaults
        lINSTANCE.AO64_66_Initialize(GSConstants.ulBdNum, GSConstants.ulError);

        System.out.println("BCR Reads : ....... (0x481X) : " + nativelong_to_hex(lINSTANCE.AO64_66_Read_Local32(GSConstants.ulBdNum, GSConstants.ulError, GSConstants.BCR )));
        System.out.println("SMPL_RATE Reads: .. (0x00C0) : " + nativelong_to_hex(lINSTANCE.AO64_66_Read_Local32(GSConstants.ulBdNum, GSConstants.ulError, GSConstants.RATE_A )));
        System.out.println("BUFF_OP Reads : ... (0x1400) : " + nativelong_to_hex(lINSTANCE.AO64_66_Read_Local32(GSConstants.ulBdNum, GSConstants.ulError, GSConstants.BUFFER_OPS)));
        System.out.println("FIRM_REV Reads: ...          : " + nativelong_to_hex(lINSTANCE.AO64_66_Read_Local32(GSConstants.ulBdNum, GSConstants.ulError, GSConstants.FW_REV)));

    }

    public String nativelong_to_hex(NativeLong value)
    {
        int x = value.intValue();
        return Integer.toHexString(x);
    }
}
