package scripts;

import bindings.AO64_64b_Driver_CLibrary;
import bindings.AO64_64b_Driver_CLibrary.U32;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import java.nio.ByteBuffer;
import com.sun.jna.ptr.IntByReference;


public class example {

    private static AO64_64b_Driver_CLibrary INSTANCE; // = GS16AO64CLibrary.INSTANCE;
    private U32  ulBdNum, ulNumBds, ulAuxBdNum, ulError;
    private IntByReference BdNum;
    private ByteBuffer pDeviceInfo;
    private U32 numChan;
    private U32 numAuxChan;
    private U32 id_off,eog,eof;
    private U32 aux_id_off,aux_eog,aux_eof;
    private U32 disconnect;
    private U32 aux_disconnect;


    /**
     * constructor creates instance of the JNA library
     *
     */
    public example()
    {

/*        String rootPath = System.getProperty("user.dir");
        String libPath = (Platform.isWindows() ?
                rootPath + "\\src\\lib\\win64\\16AO64_eDriver_C.lib" :
                rootPath + "/src/lib/osx/16AO64_eDriver_C.dylib");      // currently, jnaerator does not make dylibs*/

/*        String rootPath = System.getProperty("user.dir");
        String libPath = rootPath + "\\GS16AO64_eDriver_C.dll";

        //GS16AO64_eDriver_CLibrary INSTANCE = (GS16AO64_eDriver_CLibrary)Native.loadLibrary(libPath, GS16AO64_eDriver_CLibrary.class);
        try {
            INSTANCE = (GS16AO64_eDriver_CLibrary)Native.loadLibrary(libPath, GS16AO64_eDriver_CLibrary.class);
        } catch (Exception ex) {
            System.out.print("exception loading library = "+ex);
        }*/

        INSTANCE = AO64_64b_Driver_CLibrary.INSTANCE;

        // hard set board num to 1 (int16)
        //Pointer BdNum = new Memory(4);
        //BdNum.setInt(0, 1);
        //ulBdNum = new U32(BdNum);

        // try using "by reference" type to make pointer
        BdNum = new IntByReference(1);
        //BdNum.setValue(1);
        ulBdNum = new U32(BdNum.getPointer());

        // assign address to ulError (16-bit)
        Pointer err = new Memory(4);
        ulError = new U32(err);
    }


    public U32 load_boards()
    {
        ulNumBds = INSTANCE.AO64_66_FindBoards(pDeviceInfo, ulError);
        return ulNumBds;
    }


    public void AO64_Init_Test()
    {

        //load_boards();

        Pointer BCR_ptr = new Memory("BCR".length() + 1 );
        BCR_ptr.setString(0, "BCR");
        U32 BCR_U32 = new U32(BCR_ptr);

        Pointer RATE_A_ptr = new Memory("RATE_A".length() + 1 );
        RATE_A_ptr.setString(0, "RATE_A");
        U32 RATE_A_U32 = new U32(RATE_A_ptr);

        Pointer BUFFER_OPS_ptr = new Memory("BUFFER_OPS".length() + 1 );
        BUFFER_OPS_ptr.setString(0, "BUFFER_OPS");
        U32 BUFFER_OPS_U32 = new U32(BUFFER_OPS_ptr);

        Pointer FW_REV_ptr = new Memory("FW_REV".length() + 1 );
        FW_REV_ptr.setString(0, "FW_REV");
        U32 FW_REV_U32 = new U32(FW_REV_ptr);

        System.out.println("Checking board Initialization Defaults");
        System.out.println(" Wrote 0x8000 to BCR");

        INSTANCE.AO64_66_Initialize(ulBdNum, ulError);
        //sleep or "Busy_Signal(20)"
        System.out.println(String.format("BCR Reads:........(0x481X) : %04d", INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, BCR_U32)));
        System.out.println(String.format("SMPL_Rate Reads:..(0x00C0) : %04d", INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, RATE_A_U32)));
        System.out.println(String.format("BUFF_OP Reads:....(0x1400) : %04d", INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, BUFFER_OPS_U32)));
        System.out.println(String.format("FIRM_REV Reads:...(      ) : %04d", INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, FW_REV_U32)));

    }


}
