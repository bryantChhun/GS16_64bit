package GS;

import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.Collections;

import constants.GSConstants;
import coremem.buffers.ContiguousBuffer;

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
 *
 * Rules:
 * 1) can write exactly one value to exactly one channel per timepoint
 * 2) channels must be written in incremental order per timepoint
 * 3) eog = "end of group" = "end of timepoint"
 * 4) eog must be written before "end of function" flag is placed
 */
public class GSBuffer {

    private ContiguousBuffer buffer;

    private Set<Integer> chansWritten;
    private Set<Integer> activeChans;
    private HashMap<Integer, Integer> TPtoPosMap;
    private int tpsWritten;
    private int valsWritten;
    private int writevalue;
    private short svalue;

    private GSConstants c;

    /**
     *
     * @param maxTP number of timepoints addressed
     * @param maxChan: maximum number of channels that will be addressed
     */
    public GSBuffer(GSConstants pconstants, int maxTP, int maxChan) throws BufferTooLargeException {
        c = pconstants;
        tpsWritten = 0;
        valsWritten = 0;
        int maxSizeInBytes = maxTP * maxChan * 4; //using 32 bit
        // Buffer has capacity of 256k VALUES
        if ((maxSizeInBytes / 4) >= 256000) {
            throw new BufferTooLargeException("Requested buffer too large.  Reduce tps or num chans");
        } else if ((maxSizeInBytes / 4) >= 192000 && (maxSizeInBytes / 4) < 256000) {
            throw new BufferTooLargeException("Warning: Requested buffer > 3/4 max capacity");
        } else {
            buffer = ContiguousBuffer.allocate(maxSizeInBytes);
        }
        buffer.pushPosition();
        chansWritten = new HashSet<Integer>();
        activeChans = new HashSet<Integer>();
        activeChans.add(-1);
        TPtoPosMap = new HashMap<Integer, Integer>();
        TPtoPosMap.put(0,0);
    }

    /**
     * Method to convert voltage into 16bit value for DAC card.
     * Scale limits are based on 2's complement
     * @param voltage float ranging from -1 to 1
     * @return scaled voltage recast as short.
     * @throws VoltageRangeException
     */
    public static int voltageToInt(float voltage) throws VoltageRangeException
    {
        short scaledVoltage;
        if(voltage < -1 || voltage > 1){
            throw new VoltageRangeException("float voltage is out of range");
        } else if (voltage<0)
        {
            scaledVoltage = (short)(voltage*32768);
        } else if (voltage>0)
        {
            scaledVoltage = (short)(voltage*32767);
        } else
        {
            scaledVoltage = (short)voltage;
        }
        return scaledVoltage;
    }

    /**
     * Add a value/chan to the end of the memory.  MUST add only to end.  MUST increment channel.
     * @param voltage float from -1 to 1
     * @param chan integer
     * @throws ActiveChanException
     */
    public void appendValue(double voltage, int chan) throws ActiveChanException, VoltageRangeException
    {
        if( (Collections.max(activeChans) > chan) ) {
            ActiveChanException e = new ActiveChanException(
                    "Higher channel exists.  Must write in increasing channel order");
            throw e;
        } else if(activeChans.contains(chan))
        {
            ActiveChanException e = new ActiveChanException(
                    "Channel already active for current timepoint.  Replace or change Channel.");
            throw e;
        } else if(chan < 0 || chan >=64){
            ActiveChanException e = new ActiveChanException(
                    "Channel must be between 0 and 64"
            );
            throw e;
        } else
        {
            activeChans.add(chan);
            chansWritten.add(chan);
        }

        //short value;
        try {svalue = (short)voltageToInt((float)voltage);} catch (VoltageRangeException ex) {throw ex;}
        System.out.println("svalue = "+svalue);
        writevalue = (chan << c.id_off.intValue() | svalue);
        System.out.println("writevalue = "+writevalue);
        buffer.writeInt(writevalue);
        System.out.println("wrote to buffer");
        // push endpoint to stack
        buffer.pushPosition();
        System.out.println("pushed position");
        valsWritten += 1;
        System.out.println("finished append value");
    }

    /**
     * adds an "end of group" flag to the most recent value written
     */
    public void appendEndofTP() throws FlagException
    {
        // move to beginning of last value
        buffer.popPosition();
        buffer.popPosition();
        buffer.pushPosition();

        int value = buffer.readInt();
        if (value >> c.eog.intValue() == 1){
            FlagException e = new FlagException("end of timepoint flag already exists!");
            throw e;
        }
        int newvalue = (1 << c.eog.intValue() | value);

        // do not use 'appendValue'
        buffer.writeInt(newvalue);
        buffer.pushPosition();

        // marks end of TP.  Register next TP in hashmap
        tpsWritten += 1;
        TPtoPosMap.put(tpsWritten, 4*valsWritten);
        activeChans.clear();
    }

    /**
     * adds an "end of function" flag to the most recent value written
     */
    public void appendEndofFunction() throws FlagException
    {
        // move to beginning of last value
        buffer.popPosition();
        buffer.popPosition();
        buffer.pushPosition();

        int value = buffer.readInt();
        if (value >> c.eog.intValue() != 1) {
            FlagException e = new FlagException("must tag end of TP before end of buffer");
            throw e;
        } else if (value >> c.eog.intValue() == 1) {
            FlagException e = new FlagException("end of function flag already exists!");
            throw e;
        }
        int newvalue = (1 << c.eog.intValue() | value);

        // do not use 'appendValue'
        buffer.writeInt(newvalue);
        buffer.pushPosition();
    }

    public short getLastValue()
    {
        buffer.popPosition();
        buffer.popPosition();
        buffer.pushPosition();
        int value = buffer.readInt();
        buffer.pushPosition();
        return (short)value;
    }

    /**
     * Get all channels written in this memory block
     * @return set of channels
     */
    public TreeSet<Integer> getAllChannels()
    {
        return new TreeSet<>(chansWritten);
    }

    /**
     * Get all channels written in this time point
     * @return set of channels
     */
    public TreeSet<Integer> getActiveChannels()
    {
        return new TreeSet<>(activeChans);
    }

    /**
     * get total timepoints written
     * @return
     */
    public int getNumTP()
    {
        return tpsWritten;
    }

    /**
     * get total number of values written
     * @return
     */
    public int getValsWritten()
    {
        return valsWritten;
    }


    /**
     * returns hashmap of ALL channels/value pairs at given timepoint
     * @param timepoint
     * @return
     */
    public HashMap<Integer, Short> getTPValues(int timepoint)
    {
        /**
         * push to set endpoint on stack
         *  retrieve TPtoPosMap(tp) and (tp+1) values
         *  difference between returned values/4 represents # values written
         *  go to tp memory position
         *  loop for number of values and return all channel/value pairs (readint)
         * pop to return to endpoint on stack
         */

        HashMap<Integer, Short> ChanValPair= new HashMap<>();
        int channel, value;
        buffer.pushPosition();
        int tpOffset1 = TPtoPosMap.get(timepoint);
        int tpOffset2 = TPtoPosMap.get(timepoint+1);
        int numVals = (tpOffset2-tpOffset1)/4;
        buffer.setPosition(tpOffset1);
        for(int i=0; i<numVals; i++)
        {
            value = buffer.readInt();
            channel = (value >> c.id_off.intValue());
            value &= (channel << c.id_off.intValue());
            ChanValPair.put(channel, (short)value);
        }
        buffer.popPosition();
        return ChanValPair;
    }

    /**
     * reset stack to beginning and clear it
     * reset all channel trackers
     * overwrite memory with zeros
     */
    public void clearALL()
    {
        buffer.rewind();
        buffer.clearStack();
        chansWritten = new HashSet<Integer>();
        valsWritten = 0;
        tpsWritten = 0;
        activeChans = new HashSet<Integer>();
        TPtoPosMap = new HashMap<Integer, Integer>();
        buffer.fillBytes((byte)0);
    }


}
