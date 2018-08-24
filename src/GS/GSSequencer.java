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
    private NativeLongByReference BuffPtr = new NativeLongByReference();

    /**
     * constructor establishes communication with board
     * Must execute in order:
     *  1) Find Boards
     *  2) Get Handle
     *  3) Initialize
     *  4) Autocalibrate only ONCE per day or computer restart.
     */
    GSSequencer(int num_threshold_values, int sample_rate) throws InvalidBoardParams
    {
        this(num_threshold_values,sample_rate,false);
    }

    GSSequencer(int num_threshold_values, int sample_rate, boolean runAutoCal) throws InvalidBoardParams
    {
        if(num_threshold_values < 0 || num_threshold_values > 256000 || sample_rate > 500000 || sample_rate < 1){
            throw new InvalidBoardParams(
                    "Threshold value out of range, or Sample rate out of range");
        }

        INSTANCE = AO64_64b_Driver_CLibrary.INSTANCE;

        GSConstants.ulBdNum = new NativeLong(1);
        GSConstants.ulError = new NativeLongByReference();

        findBoards();
        getHandle();
        setBoardParams();

        InitializeBoard();
        if(runAutoCal) {
            AutoCalibration();
        }

        setBufferThreshold(num_threshold_values);
        setSampleRate(sample_rate);

    }

    /**
     * Sends head of ArrayDeque list to buffers
     *      Event Handlers, Interrupts, Interrupt notification are used to monitor threshold event
     *      prefill buffer writes values to buffer before starting clock (might not be necessary)
     *
     * @param data ArrayDeque of GSBuffers.  Buffers constructed using CoreMem Contiguous Buffer
     * @return true when done playing
     */
    public boolean play(ArrayDeque<GSBuffer> data)
    {
        try{
            setEventHandlers();
            setEnableInterrupt(0, 0x04);
            setInterruptNotification();
        } catch (Exception ex) {System.out.println(ex);}

        connectOutputs();
        openDMAChannel(1);

        prefillBuffer(data);

        startClock();

        println("writing to outputs now");
        while(!data.isEmpty())
        {
            EventStatus.setValue(Kernel32.INSTANCE.WaitForSingleObject(myHandle, 1));
            println("buffer size before switch = "+ getLongRegister(GSConstants.BUFFER_SIZE).toString());
            switch(EventStatus.intValue()) {
                case 0x00://wait_object_0, object is signaled;
                    println(" object signaled ... writing to outputs");
                    if( checkDMAOverflow(data.peek()) ){
                        sendDMABuffer(data.peek(), data.peek().getValsWritten());
                        data.remove();
                    }
                    break;
                case 0x80://wait abandoned;
                    println(" Error ... Wait abandoned");
                    break;
                case 0x102://wait timeout.  object stat is non signaled
                    println(" Error ... Wait timeout");
                    break;
                case 0xFFFFFFFF:// wait failed.  Function failed.  call GetLastError for extended info.
                    println(" Error ... Wait failed");
                    break;
            }

        }

        return true;
    }

    /**
     * Writes to DMA buffer a JNAPointer to Coremem buffer.
     *  Also specifies number of words to write from this buffer.
     * @param bufferElement A single GSBuffer block
     * @param words the number of values written to the block.  This is different from the size of the block.
     */
    private void sendDMABuffer(GSBuffer bufferElement, int words)
    {
        BuffPtr.setPointer(bufferElement.getMemory().getJNAPointer().share(0));
        INSTANCE.AO64_66_DMA_Transfer(GSConstants.ulBdNum, GSConstants.ulChannel, new NativeLong(words), BuffPtr, GSConstants.ulError);
    }

    /**
     * Queries the GSDAC board for target buffer threshold anc current buffer size
     *      Requires that Interrupt event, Buffer Threshold high-low is enabled.
     *      Used ONLY during buffer prefill initialization
     * @return False if below threshold, true if above.
     * @throws DMAOccupancyException thrown if DMA high-low thresh is not set
     */
    private boolean checkDMAThreshSatisfied() throws DMAOccupancyException
    {
        // is there a better way to check if Interrupt request flag is set?
        // We can query the BCR directly, and do bit math to check the values, but is this better?
        if (GSConstants.InterruptValue.intValue() == 0x04)
        {
            int targetTHRSHLD = getLongRegister(GSConstants.BUFFER_THRSHLD).intValue();
            int currentSize = getLongRegister(GSConstants.BUFFER_SIZE).intValue();
            if(currentSize < targetTHRSHLD) {
                return false;
            } else {
                return true;
            }
        } else {
            throw new DMAOccupancyException(
                    "DMA threshold interruption not set");
        }
    }

    /**
     * check whether writing the next entry in the GSBuffer queue will fill the DMA to max
     * @param nextBufferEntry GSBuffer object received from ArrayDeque
     * @return false if DMA will overflow.  True if OK to write.
     */
    private boolean checkDMAOverflow(GSBuffer nextBufferEntry)
    {
        if(nextBufferEntry == null) {return false;}

        int nextBufferSize = nextBufferEntry.getValsWritten();
        int currentSize = getLongRegister(GSConstants.BUFFER_SIZE).intValue();
        if(currentSize + nextBufferSize > 256000){
            return false;
        } else {
            return true;
        }
    }

    /**
     * DMA initialization method to fill buffer before clock is started
     *      Continually writes next buffer entry if
     * @param buffer Entire ArrayDeque of GSBuffers
     */
    private void prefillBuffer(ArrayDeque<GSBuffer> buffer)
    {
        try{
            boolean checkThresh = checkDMAThreshSatisfied();
            do {
                if ( !checkThresh && checkDMAOverflow(buffer.peek()) ) {
                    sendDMABuffer(buffer.peek(), buffer.peek().getValsWritten());
                    buffer.remove();
                } else{
                    System.out.println("WARNING: next buffer value will overflow DMA");
                    break;
                }
                checkThresh = checkDMAThreshSatisfied();
            } while (!checkThresh);

        } catch (Exception ex) {System.out.println(ex);}

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
     * @param numValues Buffer takes maximum 256k values
     */
    private void setBufferThreshold(int numValues)
    {
        System.out.println("setting buffer threshold = "+numValues);
        setLongRegister(GSConstants.BUFFER_THRSHLD, new NativeLong(numValues));
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
     * @param value See table 3.6-1 for interrupt event selection. PUT TABLE HERE
     *
     *
     *
     *
     *
     *
     *              0x04 is for buffer thresh flag high-to-low.
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
        } else {
            GSConstants.InterruptValue = new NativeLong(value);
            GSConstants.InterruptType = new NativeLong(type);
            INSTANCE.AO64_66_EnableInterrupt(GSConstants.ulBdNum, GSConstants.InterruptValue, GSConstants.InterruptType, GSConstants.ulError);
        }
    }

    /**
     * disable most recently enabled interrupt (determined by ulValue)
     */
    private void setDisableInterrupt()
    {
        INSTANCE.AO64_66_DisableInterrupt(GSConstants.ulBdNum, GSConstants.InterruptValue, GSConstants.InterruptType, GSConstants.ulError);
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
            INSTANCE.AO64_66_Register_Interrupt_Notify(GSConstants.ulBdNum, Event, GSConstants.InterruptValue, GSConstants.InterruptType, GSConstants.ulError);
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

    /**
     * Only two DMA channels.  We hard set this to 1
     * @param channel 1 or 0
     */
    private void openDMAChannel(int channel)
    {
        GSConstants.ulChannel = new NativeLong(channel);
        INSTANCE.AO64_66_Open_DMA_Channel(GSConstants.ulBdNum, GSConstants.ulChannel, GSConstants.ulError);
    }

    private void closeDMAChannel()
    {
        INSTANCE.AO64_66_Close_DMA_Channel(GSConstants.ulBdNum, GSConstants.ulChannel, GSConstants.ulError);
    }


    /**
     * Return value never used but "FindBoards" must be called for board initialization
     * @return number of boards found.  We have only one.
     */
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

    /**
     * Return value is never used but "get_handle" must be called for board initialization
     * @return board handle
     */
    private NativeLong getHandle()
    {
        return INSTANCE.AO64_66_Get_Handle(GSConstants.ulError, GSConstants.ulBdNum);
    }

    /**
     * Not explicitly required for board initialization,
     *      but sets values for GSConstants class that are critical for operation.
     */
    private void setBoardParams()
    {
        GSConstants.numChan = new NativeLong();
        GSConstants.id_off = new NativeLong();
        GSConstants.eog = new NativeLong();
        GSConstants.eof = new NativeLong();
        GSConstants.disconnect = new NativeLong();

        GSConstants.ValueRead = getLongRegister(GSConstants.FW_REV);

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
        } else{
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

        println("numChan : ... : " + GSConstants.numChan);
        println("id_off: ..... : " + GSConstants.id_off);
        println("eog : ....... : " + GSConstants.eog);
        println("eof : ....... : " + GSConstants.eof);
    }

    /**
     * Required for board Initialization.
     * Sets values in GSConstants class that are critical for operation
     */
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

    /**
     * Necessary for board Initialization
     *      Need only be run ONCE when computer is restarted or loads on outputs change substantially.
     */
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

    /**
     * Flips "Disconnect outputs" bit to LOW in the BCR
     */
    private static void connectOutputs()
    {
        if(GSConstants.disconnect.equals(0)){
            return;
        }
        NativeLong myData = getLongRegister(GSConstants.BCR);
        myData.setValue(myData.intValue() & ~0x4);
        setLongRegister(GSConstants.BCR, myData);
    }

    /**
     * used by external processes to reset all outputs.
     */
    public static void resetOutputsToZero()
    {
        for(int cntr = 0; cntr < GSConstants.numChan.intValue() ; cntr++){
            setLongRegister(GSConstants.OUTPUT_DATA_BUFFER, GSConstants.ReadValue[cntr]);
        }
    }

    private void closeHandle()
    {
        INSTANCE.AO64_66_Close_Handle(GSConstants.ulBdNum, GSConstants.ulError);
    }

    private static NativeLong getLongRegister(NativeLong register) {
        return INSTANCE.AO64_66_Read_Local32(GSConstants.ulBdNum, GSConstants.ulError, register);
    }

    private static void setLongRegister(NativeLong register, NativeLong value) {
        INSTANCE.AO64_66_Write_Local32(GSConstants.ulBdNum, GSConstants.ulError, register, value);
    }

    private void println(String writing_to_outputs_now) {
        System.out.println(writing_to_outputs_now);
    }

    /**
     * when this class object is dereferenced, finalize will reset board values
     */
    @Override
    public void finalize()
    {
        resetOutputsToZero();
        stopInterruptNotification();
        setDisableInterrupt();
        stopClock();
        closeDMAChannel();
        closeHandle();
    }

}
