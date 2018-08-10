package GS;

import java.util.Set;
import java.util.HashSet;
import coremem.buffers.ContiguousBuffer;
import constants.c;
import coremem.offheap.OffHeapMemory;
import coremem.offheap.OffHeapMemoryAccess;

import java.nio.Buffer;

/**
 * Build a buffer to pass to GS-DAQ card via JNA interface
 * Structure is:
 *      Buffer
 *          Timepoint1
 *              Channel1, Value1
 *              Channel2, Value2
 *              ...
 *          Timepoint2
 *              ChannelX, ValueY
 *              ...
 */
public class GSBuffer {

    private int maxTP;
    private int maxChan;
    private int maxSizeInBytes;
    private ContiguousBuffer buffer;
    private Class dataType;

    private Set<Integer> channels = new HashSet<Integer>();

    /**
     *
     * @param ptimepoints number of timepoints addressed
     * @param pchannels: maximum number of channels that will be addressed
     */
    GSBuffer(int ptimepoints, int pchannels) throws BufferTooLargeException {
        maxTP = ptimepoints;
        maxChan = pchannels;
        maxSizeInBytes = maxTP * maxChan * 4; //using 32 bit
        if (maxSizeInBytes / 4 >= 256000) {
            BufferTooLargeException e = new BufferTooLargeException("Buffer too large for card");
            throw e;
        } else {
            buffer = ContiguousBuffer.allocate(maxSizeInBytes);
        }
        dataType = int.class;
    }

    private static int voltageToInt(float voltage) throws VoltageRangeException
    {
        if(voltage < -1 || voltage > 1){
            VoltageRangeException e = new VoltageRangeException("float voltage is out of range");
            throw e;
        } else{
            return (int)voltage;
        }
    }

    public void appendValue(float voltage, int chan)
    {
        int value;
        //register beginning of last value on stack
        buffer.pushPosition();
        try {value = voltageToInt(voltage);} catch (VoltageRangeException ex) {value = 0;}
        int writevalue = (chan << c.id_off.intValue() | value);
        buffer.writeInt(writevalue);
    }

    public void appendEndofTP()
    {
        int value;
        buffer.popPosition();
        value = buffer.readInt();
        int newvalue = (1 << c.eof.intValue() | value);
        buffer.writeInt(newvalue);
    }

    public void appendEndofBuffer()
    {
        int value;
        buffer.popPosition();
        value = buffer.readInt();
        int newvalue = (1 << c.eog.intValue() | value);
        buffer.writeInt(newvalue);
    }

    //getters
    public int getAllChannels()
    {
        //returns ALL channels assigned in memory
    }

    public int getActiveChannels()
    {
        //returns channels in current timepoint
    }

    public int getNumTP()
    {
        //returns current total num of timepoints written
    }

    public int[][] getTPValues(int timepoint)
    {
        //returns array of ALL channels/value pairs at given timepoint
    }

    public int getLastValue()
    {
        //returns channel/value pair of most recent timepoint
    }

    public int[][][] getFullBuffer()
    {
        //returns channel/value/tp for every value in the buffer, in order
    }

    public ContiguousBuffer getBuffer()
    {
        //returns buffer memory
    }

    //setters
    public void setValueAtPosition(int offset, int timepoint, int channel, float voltage)
    {
        //replaces a channel/voltage pair at a given timepoint/offset
        // offset is 32-bit aligned.
    }

    public void setLastValue(int channel, float voltage)
    {
        //replaces the last value in entry
    }

    //release memory
    public void clear() {}


}
