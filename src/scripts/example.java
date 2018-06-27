package scripts;

import bindings.AO64_64b_Driver_CLibrary;
import bindings.AO64_64b_Driver_CLibrary.U32;
import bindings.GS_PHYSICAL_MEM;
import com.sun.jna.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;

import java.util.concurrent.TimeUnit;


public class example {

    //private static AO64_64b_Driver_CLibrary INSTANCE; // = GS16AO64CLibrary.INSTANCE;
    private static AO64_64b_Driver_CLibrary INSTANCE;
    private U32  ulBdNum, ulNumBds, ulAuxBdNum, ulError;
    private U32 numChan;
    private U32 numAuxChan;
    private U32 id_off,eog,eof;
    private U32 aux_id_off,aux_eog,aux_eof;
    private U32 disconnect;
    private U32 aux_disconnect;
    private U32 handle;
    Pointer test = Pointer.createConstant(1);


    /**
     * constructor creates instance of the JNA library
     *
     */
    public example()
    {
        //final String JNA_LIBRARY_NAME = "AO64_64b_Driver_C";
        //final NativeLibrary JNA_NATIVE_LIB = NativeLibrary.getInstance(AO64_64b_Driver_CLibrary.JNA_LIBRARY_NAME);
        //INSTANCE = (AO64_64b_Driver_CLibrary)Native.loadLibrary("AO64_64b_Driver_C.dll", AO64_64b_Driver_CLibrary.class);

        //INSTANCE = AO64_64b_Driver_CLibrary.INSTANCE;

        // hard set board num to 1 (int16)
        //Pointer BdNum = new Memory(4);
        //BdNum.setInt(0, 1);
        //ulBdNum = new U32(BdNum);

        // try using "by reference" type to make pointer
        //BdNum = new IntByReference(1);
        //BdNum.setValue(1);
        //ulBdNum = new U32(BdNum.getPointer());
        //System.out.println("creating constant for Board number");
        //Pointer BdNum_ptr = Pointer.createConstant(1);
        //ulBdNum = new U32(BdNum_ptr);

        // assign address to ulError (16-bit)
        //Pointer err = new Memory(4);
        //ulError = new U32(err);

        //System.out.println("Allocating memory for error");
        //Memory err2 = new Memory(4);
        //ulError = new U32(err2);
    }

    public U32 get_handle()
    {
        handle = INSTANCE.AO64_66_Get_Handle(ulError, ulBdNum);
        return handle;
    }


    public U32 load_boards()
    {
        System.out.println("calling board");

        Memory p = new Memory(4);
        ByteBuffer DeviceInfo = p.getByteBuffer(0, p.size()).order(ByteOrder.nativeOrder());
        ulNumBds = INSTANCE.AO64_66_FindBoards(DeviceInfo, ulError);

        System.out.println("board return = "+ ulNumBds);
        return ulNumBds;
    }


    public void AO64_Init_Test()
    {

        INSTANCE = AO64_64b_Driver_CLibrary.INSTANCE;

        System.out.println("is our library instance valid?  Try some calls");
        System.out.println(INSTANCE.BCR);
        System.out.println(INSTANCE.Reserved);
        System.out.println(INSTANCE.Reserved1);
        System.out.println(INSTANCE.BUFFER_OPS);
        try
        {
            TimeUnit.SECONDS.sleep(1);
        } catch(Exception ex) {
            System.out.println("no timeout delay");
        }


        //load_boards();

//        Pointer BCR_ptr = new Memory("BCR".length() + 1 );
//        BCR_ptr.setString(0, "BCR");
//        U32 BCR_U32 = new U32(BCR_ptr);
//
//        Pointer RATE_A_ptr = new Memory("RATE_A".length() + 1 );
//        RATE_A_ptr.setString(0, "RATE_A");
//        U32 RATE_A_U32 = new U32(RATE_A_ptr);
//
//        Pointer BUFFER_OPS_ptr = new Memory("BUFFER_OPS".length() + 1 );
//        BUFFER_OPS_ptr.setString(0, "BUFFER_OPS");
//        U32 BUFFER_OPS_U32 = new U32(BUFFER_OPS_ptr);
//
//        Pointer FW_REV_ptr = new Memory("FW_REV".length() + 1 );
//        FW_REV_ptr.setString(0, "FW_REV");
//        U32 FW_REV_U32 = new U32(FW_REV_ptr);

        System.out.println("Checking board Initialization Defaults");

        //Pointer BdNum = new Memory(4);
        //BdNum.setInt(0, 1);
        //Memory BdNum2 = new Memory(4);
        //ulBdNum = new U32(BdNum2);
        //U32 ulBdNum = new U32();

        //Pointer err = new Memory(8);
        LongByReference err = new LongByReference();
        ulError = new U32(err.getPointer());

        //System.out.println("calling FindBoards");
        //Memory m = new Memory(4);
        //lDeviceInfo = m.getByteBuffer(0, m.size()).order(ByteOrder.nativeOrder());
        //ulNumBds = INSTANCE.AO64_66_FindBoards(lDeviceInfo, ulError);
        //ulBdNum = ulNumBds
        //Pointer test = Pointer.createConstant(1);
        //ulBdNum = new U32(test);

        //Memory k = new Memory(8);
        //k.setInt(0, 1);
        LongByReference k = new LongByReference(1);
        ulBdNum = new U32(k.getPointer());

        //byte[] buffer = new byte[] {};
        //int len;
        //Pointer ulBdNum = new Memory(8);
        //ulBdNum.setInt(0, 1);
        //ulBdNum.setByte(4, buffer, buffer.length);

        System.out.println("try to initialize with ulBdNum, ulError");
        System.out.println("checking bdnum "+ulBdNum);
        System.out.println("checking Error "+ulError);

        System.out.println("checking bdnum method "+ulBdNum.getPointer());
        System.out.println("checking Error method "+ulError.getPointer());

        try
        {
            TimeUnit.SECONDS.sleep(1);
        } catch(Exception ex) {
            System.out.println("no timeout delay");
        }

        //INSTANCE.AO64_66_Initialize(ulBdNum, ulError);
        INSTANCE.AO64_66_Autocal(ulBdNum, ulError);

        //System.out.println(String.format("BCR Reads:........(0x481X) : %04d", INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, BCR_U32)));
        //System.out.println(String.format("SMPL_Rate Reads:..(0x00C0) : %04d", INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, RATE_A_U32)));
        //System.out.println(String.format("BUFF_OP Reads:....(0x1400) : %04d", INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, BUFFER_OPS_U32)));
        //System.out.println(String.format("FIRM_REV Reads:...(      ) : %04d", INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, FW_REV_U32)));
        //m.dump();
        //k.dump();
        //err2.dump();
    }


}
