package scripts;

import bindings.AO64_64b_Driver_CLibrary;
import com.sun.jna.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.NativeLongByReference;

public class example {

    private static AO64_64b_Driver_CLibrary INSTANCE;
    private NativeLong ulNumBds, ulBdNum, numChan, id_off, eog, eof;
    private NativeLong BCR, Reserved, Reserved1, BUFFER_OPS, FW_REV, AUTO_CAL, OUTPUT_DATA_BUFFER, BUFFER_SIZE, BUFFER_THRSHLD, RATE_A, RATE_B;
    private NativeLongByReference ulError;

    /**
     * constructor creates instance of the JNA library
     *  Also initializes the board.
     */
    public example() {
        INSTANCE = AO64_64b_Driver_CLibrary.INSTANCE;

        // MUST call FindBoards first
        find_boards();

        // hard set board = 1
        ulBdNum = new NativeLong();
        ulBdNum.setValue(1);

        // create Error pointer
        ulError = new NativeLongByReference();

        // MUST call Get_Handle second
        get_handle();

    }

    public NativeLong get_handle()
    {
        NativeLong out = new NativeLong();
        out = INSTANCE.AO64_66_Get_Handle(ulError, ulBdNum);
        check_error("get_handle");
        return out;
    }

    public void close_handle()
    {
        INSTANCE.AO64_66_Close_Handle(ulBdNum, ulError);
        check_error("close_handle");
    }

    public NativeLong find_boards()
    {
        // size is not bytes, it's # characters
        Memory p = new Memory(69);
        ByteBuffer DeviceInfo = p.getByteBuffer(0, p.size()).order(ByteOrder.nativeOrder());
        NativeLong out = INSTANCE.AO64_66_FindBoards(DeviceInfo, ulError);
        byte[] bytes;
        if(DeviceInfo.hasArray()){
            // maybe not necessary as we clearly don't initialize bytes with array.
            bytes = DeviceInfo.array();
        } else{
            bytes = new byte[DeviceInfo.remaining()];
            DeviceInfo.get(bytes);
        }
        String buf = new String(bytes, Charset.forName("UTF-8"));
        System.out.println("device info from FindBoards = "+ buf);
        return out;
    }

    // needs fixing.  Always outputs result even if ulError is null?
    public String check_error(String location)
    {
        if(ulError.equals(null))
        {
            return null;
        }
        if(ulError.getValue() != null)
        {
            return "ulError when calling %s = %s".format(location, ulError.getValue().toString());
        }
        return null;
    }

    public String nativelong_to_hex(NativeLong value)
    {
        int x = value.intValue();
        //String hex = Integer.toHexString(x);
        return Integer.toHexString(x);
    }

    public void AO64_Init_Test()
    {

        System.out.println("Checking board Initialization Defaults");

        // java converts hex to int easily.  Does JNA convert NativeLong to hex?
        BCR = new NativeLong();
        BCR.setValue(0x00);
        RATE_A = new NativeLong();
        RATE_A.setValue(0x24);
        BUFFER_OPS = new NativeLong();
        BUFFER_OPS.setValue(0x0C);
        FW_REV = new NativeLong();
        FW_REV.setValue(0x10);

        // Reset board to defaults
        INSTANCE.AO64_66_Initialize(ulBdNum, ulError);
        check_error("AO64_66_Initialize");

        System.out.println("BCR Reads : ....... (0x481X) : " + nativelong_to_hex(INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, BCR )));
        System.out.println("SMPL_RATE Reads: .. (0x00C0) : " + nativelong_to_hex(INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, RATE_A )));
        System.out.println("BUFF_OP Reads : ... (0x1400) : " + nativelong_to_hex(INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, BUFFER_OPS)));
        System.out.println("FIRM_REV Reads: ...          : " + nativelong_to_hex(INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, FW_REV)));

    }

    public NativeLong auto_calibration()
    {
        NativeLong out = INSTANCE.AO64_66_Autocal(ulBdNum, ulError);
        return out;
    }

    public void AO64_Basic_output_test()
    {
        System.out.println("output Channels basic operation and shorts:");

        System.out.println("Intializing the board");
        BCR = new NativeLong();
        BCR.setValue(0x00);
        NativeLong val_1 = new NativeLong();
        val_1.setValue(0x8000);
        NativeLong val_2 = new NativeLong();
        val_2.setValue(0x0030);

        INSTANCE.AO64_66_Write_Local32(ulBdNum, ulError, BCR, val_1);
        if(auto_calibration().intValue() != 1)
        {
            System.out.println("Autocal Failed");
        } else {
            System.out.println("Autocal passed");
        }

        INSTANCE.AO64_66_Write_Local32(ulBdNum, ulError, BCR, val_2);

    }

}
