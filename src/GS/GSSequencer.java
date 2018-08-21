package GS;

import bindings.AO64_64b_Driver_CLibrary;
import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.NativeLongByReference;
import constants.GSConstants;
import coremem.ContiguousMemoryInterface;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Queue;
import java.util.LinkedList;

/**
 * for continuous function, this is the order of operations:
 * 1) init board
 * 2) set sample rate
 * 3) autocalibration
 * 4) set buffer threshold
 * 5) define constants:  event(notifyobject), dwords, ulchannel = dmaChannel
 *          ulWords = #words to write!
 *          buffer pointer
 *    define fields:    public final pointer data, handle myhandle
 *
 * 6) define data parameters:
 *          -
 */
public class GSSequencer {

    static AO64_64b_Driver_CLibrary INSTANCE;

    private static Queue<GSBuffer> DMAbuffer;

    private ContiguousMemoryInterface[] JNAdata;

    /**
     * constructor establishes communication with board
     * Must execute in order:
     *  1) Find Boards
     *  2) Get Handle
     *  3) Initialize
     *  4) Autocalibrate only ONCE per day or computer restart.
     */
    GSSequencer()
    {
        INSTANCE = AO64_64b_Driver_CLibrary.INSTANCE;
        GSConstants.ulBdNum = new NativeLong(1);
        GSConstants.ulError = new NativeLongByReference();

        find_boards();
        get_handle();
        set_board_params();

        InitializeBoard(INSTANCE);
        AutoCalibration(INSTANCE);

        DMAbuffer = new LinkedList<>();
    }


    public void setSampleRate()
    {

    }

    public void setBufferThreshold()
    {

    }

    /**
     * Play sends list of GSBuffer to tail of LinkedList AND starts the clock, if not already started
     * Play streams head of LinkedList into GSDAC card's DMA, if DMA threshold flag bit is low.
     * Reading from LinkedList head and writing to tail need to be asynchronous.
     *      The approach above allows one to write a unlimited amount of data to the outputs
     *      you won't need to wait for buffer threshold to drop, or for play to return before writing next chunk
     *      ** not necessary if this mechanism is already implemented in ClearControl...?
     *
     * @param data
     * @return
     */
    public boolean play(GSBuffer[] data)
    {
//        JNAdata[0] = data[0].getMemory();
//        JNAdata[0].getJNAPointer();
        //stack buffer
        //fill buffers
        //start clock
        // while more buffer to send
        //    if below threshold, send buffer
        //    sleep
        //
        //wait for play to finish
        //return true;

        // 1) iterate to write GSBuffer DATA to linkedlist (executor class call?)
        // 2) check for clock started, start if off
        // 3) while buffer capacity,  Wait until lower than capacity then send from LL (executor class call?)
        // 4) wait for LL to empty (no element exception)
        // 5) return true when complete
    }


    @Override
    public void finalize()
    {
        close_handle();
    }

    private void InitializeBoard(AO64_64b_Driver_CLibrary lINSTANCE)
    {
        lINSTANCE.AO64_66_Initialize(GSConstants.ulBdNum, GSConstants.ulError);
    }

    private void AutoCalibration(AO64_64b_Driver_CLibrary lINSTANCE)
    {
        if(lINSTANCE.AO64_66_Autocal(GSConstants.ulBdNum, GSConstants.ulError).intValue() != 1)
        {
            System.out.println("Autocal Failed");
            System.exit(1);
        } else {
            System.out.println("Autocal Passed");
        }
    }

    private NativeLong get_handle()
    {
        NativeLong out = INSTANCE.AO64_66_Get_Handle(GSConstants.ulError, GSConstants.ulBdNum);
        return out;
    }

    private NativeLong find_boards()
    {
        Memory p = new Memory(69);
        ByteBuffer DeviceInfo = p.getByteBuffer(0, p.size()).order(ByteOrder.nativeOrder());
        NativeLong out = INSTANCE.AO64_66_FindBoards(DeviceInfo, GSConstants.ulError);
        byte[] bytes;
        if(DeviceInfo.hasArray()){
            bytes = DeviceInfo.array();
        } else{
            bytes = new byte[DeviceInfo.remaining()];
            DeviceInfo.get(bytes);
        }
        String buf = new String(bytes, Charset.forName("UTF-8"));
        System.out.println("device info from FindBoards = "+ buf);
        return out;
    }

    private void set_board_params()
    {
        GSConstants.FW_REV = new NativeLong(0x10);
        GSConstants.numChan = new NativeLong();
        GSConstants.id_off = new NativeLong();
        GSConstants.eog = new NativeLong();
        GSConstants.eof = new NativeLong();
        GSConstants.disconnect = new NativeLong();
        GSConstants.ValueRead = INSTANCE.AO64_66_Read_Local32(GSConstants.ulBdNum, GSConstants.ulError, GSConstants.FW_REV);
        switch((GSConstants.ValueRead.intValue() >> 16) & 0x03){
            case 1:
            case 2: GSConstants.numChan.setValue(32); break;
            case 3: GSConstants.numChan.setValue(16); break;
            default: GSConstants.numChan.setValue(64);
        }
        if((GSConstants.ValueRead.intValue() & 0xFFFF) >= 0x400){
            GSConstants.id_off.setValue(24);
            GSConstants.eog.setValue(30);
            GSConstants.eof.setValue(31);
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
        for(int cntr = 0; cntr < GSConstants.numChan.intValue() ; cntr++){
            INSTANCE.AO64_66_Write_Local32(GSConstants.ulBdNum, GSConstants.ulError, GSConstants.OUTPUT_DATA_BUFFER, GSConstants.ReadValue[cntr]);
        }
    }

    public void close_handle()
    {
        INSTANCE.AO64_66_Close_Handle(GSConstants.ulBdNum, GSConstants.ulError);
    }

}
