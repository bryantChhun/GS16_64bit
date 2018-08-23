package GS;

import bindings.AO64_64b_Driver_CLibrary;
import bindings.GS_NOTIFY_OBJECT;
import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.NativeLongByReference;
import constants.GSConstants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayDeque;

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

    private static AO64_64b_Driver_CLibrary INSTANCE;

    private GS_NOTIFY_OBJECT Event = new GS_NOTIFY_OBJECT();
    private HANDLE myHandle = new HANDLE();
    private DWORD EventStatus = new DWORD();
    private NativeLong ulValue;
    NativeLongByReference BuffPtr = new NativeLongByReference();

    // to do:
    //      diff constructors for different thresholds/sample rates?
    //      buffer size checks


    /**
     * constructor establishes communication with board
     * Must execute in order:
     *  1) Find Boards
     *  2) Get Handle
     *  3) Initialize
     *  4) Autocalibrate only ONCE per day or computer restart.
     */
    GSSequencer(int num_threshold_values, int sample_rate)
    {
        INSTANCE = AO64_64b_Driver_CLibrary.INSTANCE;

        GSConstants.ulBdNum = new NativeLong(1);
        GSConstants.ulError = new NativeLongByReference();

        findBoards();
        getHandle();
        setBoardParams();

        InitializeBoard();
        AutoCalibration();

        setBufferThreshold(num_threshold_values);
        setSampleRate(sample_rate);


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
    public boolean play(ArrayDeque<GSBuffer> data)
    {
        try{
            setEventHandlers();
            setEnableInterrupt(0, 0x04);
            setInterruptNotification();
        } catch (Exception ex) {}

        connectOutputs();
        openDMAChannel(1);

        sendDMABuffer(data.remove());
        sendDMABuffer(data.remove());

        // check here to compare current DMA occupancy vs threshold, if threshold flag is high-low
        startClock();

        NativeLong register = new NativeLong(0x1C);
        System.out.println("writing to outputs now");
        while(!data.isEmpty())
        {
            EventStatus.setValue(Kernel32.INSTANCE.WaitForSingleObject(myHandle, 1));
            System.out.print("buffer size before switch = "+INSTANCE.AO64_66_Read_Local32(GSConstants.ulBdNum, GSConstants.ulError, register).toString());
            switch(EventStatus.intValue()) {
                case 0x00://wait_object_0, object is signaled;
                    System.out.println(" object signaled ... writing to outputs");
                    sendDMABuffer(data.remove());
                    break;
                case 0x80://wait abandoned;
                    System.out.println(" Error ... Wait abandoned");
                    break;
                case 0x102://wait timeout.  object stat is non signaled
                    System.out.println(" Error ... Wait timeout");
                    break;
                case 0xFFFFFFFF:// wait failed.  Function failed.  call GetLastError for extended info.
                    System.out.println(" Error ... Wait failed");
                    break;
            }

        }

        return true;
    }

    private void sendDMABuffer(GSBuffer bufferElement)
    {
        GSConstants.ulWords = new NativeLong(0x10000);
        BuffPtr.setPointer(bufferElement.getMemory().getJNAPointer().share(0));
        INSTANCE.AO64_66_DMA_Transfer(GSConstants.ulBdNum, GSConstants.ulChannel, GSConstants.ulWords, BuffPtr, GSConstants.ulError);
    }
    
    /**
     * Set the desired sampling rate.
     * Board contains Rate-A and Rate-B.  Rate-B can be used for triggering, while Rate-A is used for sampling
     * calculated by fRate = Fclk / Nrate, with Fclk = 49.152 MHZ, Nrate = control register value
     * @param fRate desired sample rate
     * @return actual sample rate
     */
    private double setSampleRate(double fRate)
    {
        System.out.println("setting sample rate = "+fRate);
        return INSTANCE.AO64_66_Set_Sample_Rate(GSConstants.ulBdNum, fRate, GSConstants.ulError);
    }

    /**
     * Threshold number of values that triggers a buffer threshold flag interruption event
     * @param numValues
     */
    private void setBufferThreshold(int numValues)
    {
        System.out.println("setting buffer threshold = "+numValues);
        NativeLong val = new NativeLong(numValues);
        INSTANCE.AO64_66_Write_Local32(GSConstants.ulBdNum, GSConstants.ulError, GSConstants.BUFFER_THRSHLD, val);
    }

    /**
     * creates a GS_NOTIFY_OBJECT that receives Kernel32 event
     */
    private void setEventHandlers() throws HandleCreationException
    {
        myHandle = Kernel32.INSTANCE.CreateEvent(null, false, false, null);
        if( myHandle == null){
            throw new HandleCreationException("Insufficient Resources to create event handle");
            //System.exit(1);
        } else {
            Event.hEvent.setPointer(myHandle.getPointer().share(0, 16));
        }
    }

    /**
     * enable interrupt using established event handler
     * @param value See table 3.6-1 for interrupt event selection.  0x04 is for buffer thresh flag high-to-low.
     */
    private void setEnableInterrupt(int type, int value) throws InterruptDeviceTypeException, InterruptValueException
    {
        if (type!=0 && type!=1){
            throw new InterruptDeviceTypeException("Invalid interrupt device type: must be int 0 or 1");
        }

        if (type==0 && (value<1 || value>7)){
            throw new InterruptValueException("Invalid interrupt event value: must be 1 through 7");
        } else if (type == 1 && (value!=0 && value!=1)){
            throw new InterruptValueException("Invalid interrupt event value: must be 0 or 1 for DMA interrupt");
        } else
        {
            ulValue = new NativeLong(value);
            GSConstants.InterruptType = new NativeLong(type);
            INSTANCE.AO64_66_EnableInterrupt(GSConstants.ulBdNum, ulValue, GSConstants.InterruptType, GSConstants.ulError);
        }
    }

    private void setDisableInterrupt()
    {
        INSTANCE.AO64_66_DisableInterrupt(GSConstants.ulBdNum, ulValue, GSConstants.InterruptType, GSConstants.ulError);
    }

    /**
     * Uses GS_NOTIFY_OBJECT to receive interrupt notification event
     * EventStatus can be used to read the status of the interruption event
     */
    private void setInterruptNotification() throws EventHandlerException, InterruptDeviceTypeException
    {
        //CHECK THAT THIS IS THE RIGHT WAY TO SEE IF HANDLER IS SET
        if (Event == null) {
            throw new EventHandlerException("Event Handler not set");
        } else if (GSConstants.InterruptType == null) {
            throw new InterruptDeviceTypeException("Interrupt device type not created");
        } else{
            INSTANCE.AO64_66_Register_Interrupt_Notify(GSConstants.ulBdNum, Event, ulValue, GSConstants.InterruptType, GSConstants.ulError);
        }
    }

    private void stopInterruptNotification()
    {
        INSTANCE.AO64_66_Cancel_Interrupt_Notify(GSConstants.ulBdNum, Event, GSConstants.ulError);
    }

    private void startClock()
    {
        INSTANCE.AO64_66_Enable_Clock(GSConstants.ulBdNum, GSConstants.ulError);
    }

    private void stopClock()
    {
        INSTANCE.AO64_66_Disable_Clock(GSConstants.ulBdNum, GSConstants.ulError);
    }

    private void openDMAChannel(int channel)
    {
        GSConstants.ulChannel = new NativeLong(channel);
        INSTANCE.AO64_66_Open_DMA_Channel(GSConstants.ulBdNum, GSConstants.ulChannel, GSConstants.ulError);
    }

    private void closeDMAChannel()
    {
        INSTANCE.AO64_66_Close_DMA_Channel(GSConstants.ulBdNum, GSConstants.ulChannel, GSConstants.ulError);
    }

    @Override
    public void finalize()
    {
        stopInterruptNotification();
        setDisableInterrupt();
        stopClock();
        closeDMAChannel();
        closeHandle();
    }


    private NativeLong getHandle()
    {
        return INSTANCE.AO64_66_Get_Handle(GSConstants.ulError, GSConstants.ulBdNum);
    }

    private NativeLong findBoards()
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

    private void setBoardParams()
    {
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

    private void InitializeBoard()
    {
        System.out.println("Initializing Board");
        INSTANCE.AO64_66_Initialize(GSConstants.ulBdNum, GSConstants.ulError);
        GSConstants.BCR = new NativeLong(0x00);
        GSConstants.Reserved = new NativeLong(0x04);
        GSConstants.Reserved1 = new NativeLong(0x08);
        GSConstants.BUFFER_OPS = new NativeLong(0x0C);
        GSConstants.FW_REV = new NativeLong(0x10);
        GSConstants.AUTO_CAL = new NativeLong(0x14);
        GSConstants.OUTPUT_DATA_BUFFER = new NativeLong(0x18);
        GSConstants.BUFFER_SIZE = new NativeLong(0x1C);
        GSConstants.BUFFER_THRSHLD = new NativeLong(0x20);
        GSConstants.RATE_A = new NativeLong(0x24);
        GSConstants.RATE_B = new NativeLong(0x28);
    }

    private void AutoCalibration()
    {
        System.out.println("Autocalibrating the board");
        if(INSTANCE.AO64_66_Autocal(GSConstants.ulBdNum, GSConstants.ulError).intValue() != 1)
        {
            System.out.println("Autocal Failed");
            System.exit(1);
        } else {
            System.out.println("Autocal Passed");
        }
    }

    private static void connectOutputs()
    {
        if(GSConstants.disconnect.equals(0)){
            return;
        }
        NativeLong myData = INSTANCE.AO64_66_Read_Local32(GSConstants.ulBdNum, GSConstants.ulError, GSConstants.BCR);
        myData.setValue(myData.intValue() & ~0x4);
        INSTANCE.AO64_66_Write_Local32(GSConstants.ulBdNum, GSConstants.ulError, GSConstants.BCR, myData);

    }

    public static void resetOutputsToZero()
    {
        for(int cntr = 0; cntr < GSConstants.numChan.intValue() ; cntr++){
            INSTANCE.AO64_66_Write_Local32(GSConstants.ulBdNum, GSConstants.ulError, GSConstants.OUTPUT_DATA_BUFFER, GSConstants.ReadValue[cntr]);
        }
    }

    private void closeHandle()
    {
        INSTANCE.AO64_66_Close_Handle(GSConstants.ulBdNum, GSConstants.ulError);
    }

}
