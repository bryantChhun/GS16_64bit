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
        System.out.println("/nOutput Channels basic operation and shorts:");

        System.out.println("Intializing the board");
        BCR = new NativeLong();
        BCR.setValue(0x00);
        NativeLong val_1 = new NativeLong();
        val_1.setValue(0x8000);
        NativeLong val_2 = new NativeLong();
        val_2.setValue(0x0030);

        lINSTANCE.AO64_66_Write_Local32(ulBdNum, ulError, BCR, val_1);
        if(lINSTANCE.AO64_66_Autocal(ulBdNum, ulError).intValue() != 1)
        {
            System.out.println("Autocal Failed");
        } else {
            System.out.println("Autocal passed");
        }

        lINSTANCE.AO64_66_Write_Local32(ulBdNum, ulError, BCR, val_2);

    }

    private void Set_AO64_to_FFE0()
    {
        NativeLong val = new NativeLong();
        val.setValue(0x0020);
        lINSTANCE.AO64_66_Write_Local32(ulBdNum, ulError, BCR, val);
    }

    public NativeLong auto_calibration(AO64_64b_Driver_CLibrary INSTANCE)
    {
        NativeLong out = INSTANCE.AO64_66_Autocal(ulBdNum, ulError);
        return out;
    }
}
