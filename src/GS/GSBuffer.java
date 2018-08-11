package GS;

import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Collections;
import coremem.buffers.ContiguousBuffer;
import constants.c;

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
 */
public class GSBuffer {

    private ContiguousBuffer buffer;

    private Set<Integer> chansWritten;
    private Set<Integer> activeChans;
    private HashMap<Integer, Integer> TPtoPosMap;
    private int tpsWritten;
    private int valsWritten;

    /**
     *
     * @param maxTP number of timepoints addressed
     * @param maxChan: maximum number of channels that will be addressed
     */
    GSBuffer(int maxTP, int maxChan) throws BufferTooLargeException {
        tpsWritten = 0;
        valsWritten = 0;
        int maxSizeInBytes = maxTP * maxChan * 4; //using 32 bit
        if (maxSizeInBytes / 4 >= 256000) {
            throw new BufferTooLargeException("Buffer too large for card");
        } else {
            buffer = ContiguousBuffer.allocate(maxSizeInBytes);
        }

        chansWritten = new HashSet<Integer>();
        activeChans = new HashSet<Integer>();
        TPtoPosMap = new HashMap<Integer, Integer>();
        TPtoPosMap.put(0,0);
    }

    private static int voltageToInt(float voltage) throws VoltageRangeException
    {
        if(voltage < -1 || voltage > 1){
            throw new VoltageRangeException("float voltage is out of range");
        } else{
            return (int)voltage;
        }
    }

    /**
     * Add a value/chan to the end of the memory.  MUST add only to end.  MUST increment channel.
     * @param voltage
     * @param chan
     * @throws ActiveChanException
     */
    public void appendValue(float voltage, int chan) throws ActiveChanException
    {
        if(Collections.max(activeChans) > chan) {
            ActiveChanException e = new ActiveChanException(
                    "Higher channel exists.  Must write in increasing channel order");
            throw e;
        } else if(activeChans.contains(chan))
        {
            ActiveChanException e = new ActiveChanException(
                    "Channel already active for current timepoint.  Replace or change Channel.");
            throw e;
        } else
        {
            activeChans.add(chan);
            chansWritten.add(chan);
        }

        int value;
        // register beginning memory position of this value on the stack
        // this enables the stack to represent value positions in the buffer
        buffer.pushPosition();
        try {value = voltageToInt(voltage);} catch (VoltageRangeException ex) {value = 0;}
        int writevalue = (chan << c.id_off.intValue() | value);
        buffer.writeInt(writevalue);
        valsWritten += 1;
    }

    public void appendEndofTP()
    {
        // we want to move to beginning of last value without removing from stack
        buffer.popPosition();
        buffer.pushPosition();
        int value = buffer.readInt();
        int newvalue = (1 << c.eof.intValue() | value);
        // this writeint replaces the old new value, so we do not increment valsWritten
        buffer.writeInt(newvalue);
        tpsWritten += 1;
        // marks end of TP.  Next TP registered in hashmap
        TPtoPosMap.put(tpsWritten, 4*valsWritten);
        activeChans.clear();
    }

    public void appendEndofBuffer()
    {
        buffer.popPosition();
        buffer.pushPosition();
        int value = buffer.readInt();
        int newvalue = (1 << c.eog.intValue() | value);
        // this writeint replaces the old new value, so we do not increment valsWritten
        buffer.writeInt(newvalue);
    }

    //getters
    public TreeSet<Integer> getAllChannels()
    {
        return new TreeSet<>(chansWritten);
    }

    public TreeSet<Integer> getActiveChannels()
    {
        return new TreeSet<>(activeChans);
    }

    public int getNumTP()
    {
        return tpsWritten;
    }

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
     * returns
     * @return the last channel/value pair
     */
    public int getLastValue()
    {
        buffer.pushPosition();
        TreeMap<Integer, Integer> sorted = new TreeMap<>(TPtoPosMap);
        buffer.setPosition(sorted.last())
        //returns channel/value pair of most recent timepoint
    }

    //release memory
    public void clearALL()
    {
        buffer.rewind();
        buffer.clearStack();
        chansWritten = new HashSet<Integer>();
        activeChans = new HashSet<Integer>();
        TPtoPosMap = new HashMap<Integer, Integer>();
    }


}
