package scripts;

import bindings.AO64_64b_Driver_CLibrary;
import constants.GSConstants;
import com.sun.jna.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

import com.sun.jna.NativeLong;
import com.sun.jna.ptr.NativeLongByReference;

/**
 * @author BryantChhun
 * Contains board initialization routines
 *
 */
public class example {

    public static AO64_64b_Driver_CLibrary INSTANCE;

    /**
     * constructor creates instance of the JNA library
     *  Also initializes the board with FindBoard and GetHandle
     */
    public example() {
        System.out.println("constructing example");

        INSTANCE = AO64_64b_Driver_CLibrary.INSTANCE;

        // MUST call FindBoards first
        find_boards();

        // hard set board = 1
        GSConstants.ulBdNum = new NativeLong();
        GSConstants.ulBdNum.setValue(1);

        // create Error pointer
        GSConstants.ulError = new NativeLongByReference();

        // MUST call Get_Handle second
        get_handle();

        // Set ValueRead, numChan, id_off, eog, eof, based on FW_REV
        set_board_params();
    }


    private NativeLong get_handle()
    {
        NativeLong out = INSTANCE.AO64_66_Get_Handle(GSConstants.ulError, GSConstants.ulBdNum);
        check_error("get_handle");
        return out;
    }

    public NativeLong find_boards()
    {
        // size is not bytes, it's # characters
        Memory p = new Memory(69);
        ByteBuffer DeviceInfo = p.getByteBuffer(0, p.size()).order(ByteOrder.nativeOrder());
        NativeLong out = INSTANCE.AO64_66_FindBoards(DeviceInfo, GSConstants.ulError);
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

    public void set_board_params()
    {
        GSConstants.FW_REV = new NativeLong();
        GSConstants.FW_REV.setValue(0x10);
        GSConstants.numChan = new NativeLong();
        GSConstants.id_off = new NativeLong();
        GSConstants.eog = new NativeLong();
        GSConstants.eof = new NativeLong();
        GSConstants.disconnect = new NativeLong();
        GSConstants.ValueRead = INSTANCE.AO64_66_Read_Local32(GSConstants.ulBdNum, GSConstants.ulError, GSConstants.FW_REV);
        switch((GSConstants.ValueRead.intValue() >> 16) & 0x03){                                     // EVALUATES TO ((240406 >> 16) & 0x03) == ((0x24) & 0x03) == 0
            case 1:
            case 2: GSConstants.numChan.setValue(32); break;
            case 3: GSConstants.numChan.setValue(16); break;
            default: GSConstants.numChan.setValue(64);                                           // Above evaluation means numChan = 64?
        }
        if((GSConstants.ValueRead.intValue() & 0xFFFF) >= 0x400){                                // EVALUATES TO (240406 & 0xFFFF) ==> ( (0x406) >= 0x400 )
            GSConstants.id_off.setValue(24);                                                  // id_off = 24
            GSConstants.eog.setValue(30);                                                     // eog = 30
            GSConstants.eof.setValue(31);                                                     // eof = 31
        }
        else{
            GSConstants.id_off.setValue(16);
            GSConstants.eog.setValue(22);
            GSConstants.eof.setValue(23);
        }
        if((GSConstants.ValueRead.intValue() & 0x1000000) == 0x00){
            GSConstants.disconnect.setValue(1);
        }
        // Example uses the below to reset outputs to midscale
        GSConstants.ReadValue = new NativeLong[16385];
        for(int i = 0; i<GSConstants.numChan.intValue(); i++){
            GSConstants.ReadValue[i] = new NativeLong( (i << GSConstants.id_off.intValue()) | (1 << GSConstants.eog.intValue()) | 0x8000 );
        }

        System.out.println("numChan : ... : " + GSConstants.numChan);
        System.out.println("id_off: ..... : " + GSConstants.id_off);
        System.out.println("eog : ....... : " + GSConstants.eog);
        System.out.println("eof : ....... : " + GSConstants.eof);
    }

    public void close_handle()
    {
        INSTANCE.AO64_66_Close_Handle(GSConstants.ulBdNum, GSConstants.ulError);
        check_error("close_handle");
    }

    // needs fixing.  Always outputs result even if ulError is null?
    public static String check_error(String location)
    {
        if(GSConstants.ulError.equals(null))
        {
            return null;
        }
        if(GSConstants.ulError.getValue() != null)
        {
            return "ulError when calling %s = %s".format(location, GSConstants.ulError.getValue().toString());
        }
        return null;
    }

    public static String nativelong_to_hex(NativeLong value)
    {
        int x = value.intValue();
        //String hex = Integer.toHexString(x);
        return Integer.toHexString(x);
    }

    public static void AO64_Connect_Outputs()
    {
        if(GSConstants.disconnect.equals(0)){
            return;
        }
        NativeLong myData = INSTANCE.AO64_66_Read_Local32(GSConstants.ulBdNum, GSConstants.ulError, GSConstants.BCR);
        myData.setValue(myData.intValue() & ~0x4);
        INSTANCE.AO64_66_Write_Local32(GSConstants.ulBdNum, GSConstants.ulError, GSConstants.BCR, myData);

    }

    public static void reset_output_to_zero()
    {
        System.out.println("End of write.  Setting all channels to midscale  Press key to reset");
        try { System.in.read(); } catch (Exception ex) { System.out.println(ex); }

        for(int cntr = 0; cntr < GSConstants.numChan.intValue() ; cntr++){
            INSTANCE.AO64_66_Write_Local32(GSConstants.ulBdNum, GSConstants.ulError, GSConstants.OUTPUT_DATA_BUFFER, GSConstants.ReadValue[cntr]);
        }
    }

}
