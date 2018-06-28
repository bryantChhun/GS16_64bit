package scripts;

import bindings.AO64_64b_Driver_CLibrary;
import bindings.GS_NOTIFY_OBJECT;
import com.sun.jna.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.ByteByReference;


import java.util.concurrent.TimeUnit;


public class example {

    public static AO64_64b_Driver_CLibrary INSTANCE;
    public NativeLong ulBdNum; //, BCR;
    public NativeLongByReference ulError;

    /**
     * constructor creates instance of the JNA library
     *
     */
    public example() {
        INSTANCE = AO64_64b_Driver_CLibrary.INSTANCE;
        ulBdNum = new NativeLong();
        ulBdNum.setValue(1);
        ulError = new NativeLongByReference();

        if(ulError.getValue() != null){
            System.out.println("ulError occurred in construction = "+ulError.getValue().toString());
        }
    }

    public void AO64_Init_Test()
    {

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

        System.out.println("Checking board Initialization Defaults");

        //ulBdNum = new NativeLong();
        //ulBdNum.setValue(1);
        //ulError = new NativeLongByReference();
        ulBdNum.setValue(0x01);
        NativeLong BCR = new NativeLong();
        BCR.setValue(0x00);
        NativeLong RATE_A = new NativeLong();
        RATE_A.setValue(0x24);

        System.out.println("try to initialize with ulBdNum, ulError");
        System.out.println("checking bdnum "+ulBdNum);
        System.out.println("checking Error "+ulError);
        System.out.println("checking bcr hex "+BCR.toString());
        System.out.println("checking RATE_A hex "+RATE_A.toString());

        try
        {
            TimeUnit.SECONDS.sleep(1);
        } catch(Exception ex) {
            System.out.println("no timeout delay");
        }

        if(ulError.getValue() != null){
            System.out.println("ulError before Initialization = "+ulError.getValue().toString());
        }

        INSTANCE.AO64_66_Initialize(ulBdNum, ulError);

        if(ulError.getValue() != null){
            System.out.println("ulError after Initialization = "+ulError.getValue().toString());
        }
        //INSTANCE.AO64_66_Autocal(ulBdNum, ulErrorIn);

//        System.out.println(INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, INSTANCE.BCR ).toString());
//        System.out.println(INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, INSTANCE.BUFFER_SIZE ).toString());
//        System.out.println(INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, INSTANCE.BUFFER_THRSHLD ).toString());
//        System.out.println(INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, INSTANCE.RATE_A ).toString());
//        System.out.println(INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, INSTANCE.RATE_B ).toString());

        //NativeLong a = INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, INSTANCE.BCR );

        NativeLong a = INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, RATE_A );

//        try{
//            a.wait(20);
//        } catch (Exception ex){
//            System.out.println("exception = "+ex);
//        }
//        NativeLong b = INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, INSTANCE.BUFFER_SIZE);
//        NativeLong c = INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, INSTANCE.BUFFER_THRSHLD );
//        NativeLong d = INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, INSTANCE.RATE_A );
//        NativeLong e = INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, INSTANCE.RATE_B );

//        System.out.println(a.longValue());
//        System.out.println(a.byteValue());
//        System.out.println(a.shortValue());
//        System.out.println(a.hashCode());
//        System.out.println(a.intValue());

        //System.out.println(INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, INSTANCE.RATE_A).toString());
//        byte[] BCR = new byte[] {0x00};
//        NativeLong bcr_read = INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, BCR);
//
//        System.out.println(INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, BCR).toString());
//
//        byte[] RATE_A = new byte[] {0x24};
//        System.out.println(INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, RATE_A).toString());


        //System.out.println(String.format("BCR Reads:........(0x481X) : %04d", INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, INSTANCE.BCR)));
        //System.out.println(String.format("SMPL_Rate Reads:..(0x00C0) : %04d", INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, RATE_A_U32)));
        //System.out.println(String.format("BUFF_OP Reads:....(0x1400) : %04d", INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, BUFFER_OPS_U32)));
        //System.out.println(String.format("FIRM_REV Reads:...(      ) : %04d", INSTANCE.AO64_66_Read_Local32(ulBdNum, ulError, FW_REV_U32)));
        //m.dump();
        //k.dump();
        //err2.dump();
    }

    public NativeLong get_handle()
    {
        return INSTANCE.AO64_66_Get_Handle(ulError, ulBdNum);
    }

    public NativeLong auto_cal()
    {
        GS_NOTIFY_OBJECT Event;
        System.out.println(ulBdNum.longValue());
        return INSTANCE.AO64_66_Autocal(ulBdNum, ulError);
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
        System.out.println("device info from Find Boards = "+ buf);
        return out;
    }

}
