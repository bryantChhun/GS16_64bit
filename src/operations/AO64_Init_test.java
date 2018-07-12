package operations;

import bindings.AO64_64b_Driver_CLibrary;
import com.sun.jna.ptr.NativeLongByReference;
import scripts.example;
import com.sun.jna.NativeLong;

public class AO64_Init_test {

    public NativeLong BCR, Reserved, Reserved1, BUFFER_OPS, FW_REV, AUTO_CAL, OUTPUT_DATA_BUFFER, BUFFER_SIZE, BUFFER_THRSHLD, RATE_A, RATE_B;
    private AO64_64b_Driver_CLibrary lINSTANCE;

    public AO64_Init_test(AO64_64b_Driver_CLibrary INSTANCE, NativeLong ulBdNum, NativeLongByReference ulError)
    {
        lINSTANCE = INSTANCE;
        System.out.println("\nChecking board Initialization Defaults");

        // java converts hex to int easily.  JNA converts NativeLong to hex
        // this could be easier if we changed the binding from NativeLong to Int, removing the need to create a NativeLong object.
        // also means we could use all constants defined in the Bindings interface, per INSTANCE.  Rather than re-define per class here.
        BCR = new NativeLong();
        BCR.setValue(0x00);
        RATE_A = new NativeLong();
        RATE_A.setValue(0x24);
        BUFFER_OPS = new NativeLong();
        BUFFER_OPS.setValue(0x0C);
        FW_REV = new NativeLong();
        FW_REV.setValue(0x10);

        // Reset board to defaults
        lINSTANCE.AO64_66_Initialize(ulBdNum, ulError);

        System.out.println("BCR Reads : ....... (0x481X) : " + nativelong_to_hex(lINSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, BCR )));
        System.out.println("SMPL_RATE Reads: .. (0x00C0) : " + nativelong_to_hex(lINSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, RATE_A )));
        System.out.println("BUFF_OP Reads : ... (0x1400) : " + nativelong_to_hex(lINSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, BUFFER_OPS)));
        System.out.println("FIRM_REV Reads: ...          : " + nativelong_to_hex(lINSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, FW_REV)));

    }

    public String nativelong_to_hex(NativeLong value)
    {
        int x = value.intValue();
        return Integer.toHexString(x);
    }
}
