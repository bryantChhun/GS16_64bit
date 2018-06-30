package scripts;

import bindings.AO64_64b_Driver_CLibrary;
import operations.*;
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
    public NativeLong ulNumBds, ulBdNum, numChan, id_off, eog, eof;
    public NativeLong ValueRead, ValueRead1;
    public NativeLong BCR, Reserved, Reserved1, BUFFER_OPS, FW_REV, AUTO_CAL, OUTPUT_DATA_BUFFER, BUFFER_SIZE, BUFFER_THRSHLD, RATE_A, RATE_B;
    public NativeLongByReference ulError, BuffPtr, NewBuffPtr;
    public NativeLong[] ReadValue;

    /**
     * constructor creates instance of the JNA library
     *  Also initializes the board.
     */
    public example() {
        System.out.println("constructing example");

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

        // Set ValueRead, numChan, id_off, eog, eof, based on FW_REV
        //set_board_params();
    }

    public void set_board_params()
    {
        id_off = new NativeLong();
        eog = new NativeLong();
        eof = new NativeLong();
        FW_REV = new NativeLong();
        FW_REV.setValue(0x10);
        ValueRead = INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, FW_REV);
        numChan = new NativeLong();
        numChan.setValue(64);
        if((ValueRead.intValue() & 0xFFFF) >= 0x400)
        {
            id_off.setValue(24);
            eog.setValue(30);
            eof.setValue(31);
        } else {
            id_off.setValue(16);
            eog.setValue(22);
            eof.setValue(23);
        }
        for(int i=0; i<numChan.intValue(); i++){
            // example uses this to reset outputs to midscale at initialization.
            //ReadValue[i] = ( (i<<id_off.intValue()) | (1<eog.intValue()) | 0x8000 );
        }

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

}
