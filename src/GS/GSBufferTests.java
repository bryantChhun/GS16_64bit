package GS;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import constants.GSConstants;

import java.util.HashMap;
import java.util.TreeSet;


public class GSBufferTests {

    private GSBuffer buffertest;

    //initialize constant values without initializing board
    private GSConstants constants = new GSConstants();

    /**
     * test GS buffer constructor for too much data and > 3/4 full.
     */
    @Test
    void GSBuffer_BufferMaxSize()
    {
        assertThrows(BufferTooLargeException.class, () -> new GSBuffer(4001, 64 ));

        assertThrows(BufferTooLargeException.class, () -> new GSBuffer(3999, 64 ));

        assertThrows(BufferTooLargeException.class, () -> new GSBuffer(2000, 500 ));
    }

    /**
     * test that voltage converter retains range of short
     */
    @Test
    void GSBuffer_VoltageToIntConversion_negativeMax()
    {
        try {
            buffertest = new GSBuffer( 2000, 64);
            buffertest.appendValue(-1.0,1);
        } catch (Exception ex) {fail(ex);}

        assertEquals(-32768, buffertest.getLastValue() );
    }

    /**
     * test that voltage converter retains range of short
     */
    @Test
    void GSBuffer_VoltageToIntConversion_positiveMax()
    {
        try {
            buffertest = new GSBuffer( 2000, 64);
            buffertest.appendValue(1.0,1);
        } catch (Exception ex) {fail(ex);}

        assertEquals(32767, buffertest.getLastValue() );
    }

    /**
     * test that zero voltage is handled
     */
    @Test
    void GSBuffer_VoltageToIntConversion_zero()
    {
        try {
            buffertest = new GSBuffer( 2000, 64);
            buffertest.appendValue(0,1);
        } catch (Exception ex) {fail(ex);}

        assertEquals(0, buffertest.getLastValue());
    }

    /**
     * test negative bounds exception
     */
    @Test
    void GSBuffer_VoltageToIntConversion_limit_low()
    {
        try {
            buffertest = new GSBuffer( 2000, 64);
        } catch (Exception ex) {fail(ex);}

        try {
            buffertest.appendValue(-10,1);
        } catch(ActiveChanException ACex) {
            fail(ACex);
        } catch(VoltageRangeException VRex){
            System.out.println("voltage limit low pass");
        }
    }

    /**
     * test positive bounds exception
     */
    @Test
    void GSBuffer_VoltageToIntConversion_limit_high()
    {
        try {
            buffertest = new GSBuffer( 2000, 64);
        } catch (Exception ex) {fail(ex);}

        try {
            buffertest.appendValue(10,1);
        } catch(ActiveChanException ACex) {
            fail(ACex);
        } catch(VoltageRangeException VRex){
            System.out.println("voltage limit high pass");
        }
    }

    /**
     * test positive write values
     */
    @Test
    void GSBuffer_VoltageToIntConversion_PostiveValues()
    {
        try {
            buffertest = new GSBuffer( 2000, 64);
        } catch (Exception ex) {fail(ex);}

        try {
            buffertest.appendValue(0.1,1);
            assertEquals(3276, buffertest.getLastValue());
            buffertest.appendValue(0.2, 2);
            assertEquals(6553, buffertest.getLastValue());
            buffertest.appendValue(0.3, 3);
            assertEquals(9830, buffertest.getLastValue());
            buffertest.appendValue(0.4, 4);
            assertEquals(13106, buffertest.getLastValue());
            buffertest.appendValue(0.5, 5);
            assertEquals(16383, buffertest.getLastValue());
        } catch(Exception ex) {fail(ex);}

    }

    /**
     * test several negative write values
     */
    @Test
    void GSBuffer_VoltageToIntConversion_NegativeValues()
    {
        try {
            buffertest = new GSBuffer( 2000, 64);
        } catch (Exception ex) {fail(ex);}

        try {
            buffertest.appendValue(-0.1,1);
            assertEquals(-3276, buffertest.getLastValue());
            buffertest.appendValue(-0.2, 2);
            assertEquals(-6553, buffertest.getLastValue());
            buffertest.appendValue(-0.3, 3);
            assertEquals(-9830, buffertest.getLastValue());
            buffertest.appendValue(-0.4, 4);
            assertEquals(-13107, buffertest.getLastValue());
            buffertest.appendValue(-0.5, 5);
            assertEquals(-16384, buffertest.getLastValue());
        } catch(Exception ex) {fail(ex);}

    }

    /**
     * EOGFlag: test handling of double TP flag write
     * // is this necessary?  double write does nothing anyway...
     */
    @Test
    void GSBuffer_EOG_2xWrite()
    {
        try {
            buffertest = new GSBuffer( 2000, 64);
            buffertest.appendValue(0.5,1);
            buffertest.appendEndofTP();
        } catch (Exception ex) {fail(ex);}

        try {
            buffertest.appendEndofTP();
        } catch(FlagException ex) {System.out.println("EOG 2x write pass");}
    }


    /**
     * EOG flag: test correct placement of TP flag
     * check both positive and negative values with EOG and EOF flags
     */
    @Test
    void GSBuffer_EOG_correctFlag()
    {
        try {
            buffertest = new GSBuffer( 2000, 64);
            buffertest.appendValue(1,1);
            buffertest.appendEndofTP();
            assertEquals(1, buffertest.getLastBlock() >>> constants.eog.intValue());
            buffertest.clearALL();

            buffertest = new GSBuffer( 2000, 64);
            buffertest.appendValue(-1,1);
            buffertest.appendEndofTP();
            assertEquals(1, buffertest.getLastBlock() >>> constants.eog.intValue());
            buffertest.clearALL();

            buffertest = new GSBuffer( 2000, 64);
            buffertest.appendValue(1,1);
            buffertest.appendEndofTP();
            buffertest.appendEndofFunction();
            //unsigned right shift necessary because EOF is most significant bit.
            assertEquals(3, buffertest.getLastBlock() >>> constants.eog.intValue());
            buffertest.clearALL();

            buffertest = new GSBuffer( 2000, 64);
            buffertest.appendValue(-1,1);
            buffertest.appendEndofTP();
            buffertest.appendEndofFunction();
            //unsigned right shift necessary because EOF is most significant bit.
            assertEquals(3, buffertest.getLastBlock() >>> constants.eog.intValue());
            buffertest.clearALL();

        } catch (Exception ex) {fail(ex);}

    }

    /**
     * EOF flag: test correct placement of end of buffer flag
     * 4 byte int can't hold a positive number with this tag
     *  it will ALWAYS return 2's complement
     */
    @Test
    void GSBuffer_EOF_correctFlag()
    {
        try {
            buffertest = new GSBuffer( 2000, 64);
            buffertest.appendValue(1,1);
            buffertest.appendEndofTP();
            buffertest.appendEndofFunction();
        } catch (Exception ex) {fail(ex);}

        assertEquals(1, (buffertest.getLastBlock() >>> constants.eof.intValue()) );

        try {
            buffertest = new GSBuffer( 2000, 64);
            buffertest.appendValue(-1,1);
            buffertest.appendEndofTP();
            buffertest.appendEndofFunction();
        } catch (Exception ex) {fail(ex);}

        assertEquals(1, (buffertest.getLastBlock() >>> constants.eof.intValue()) );

    }

    /**
     * attempt to write same active channel twice
     */
    @Test
    void GSBuffer_WriteChannel_2x()
    {
        try {
            buffertest = new GSBuffer( 2000, 64);
            buffertest.appendValue(1,1);
        } catch (Exception ex) {fail(ex);}

        try {
            buffertest.appendValue(0,1);
        } catch(Exception ex) {System.out.println("same active channel pass");}

    }

    /**
     * attempt to write beyond channel range
     */
    @Test
    void GSBuffer_WriteChannel_range()
    {
        try {
            buffertest = new GSBuffer( 2000, 64);
        } catch (Exception ex) {fail(ex);}

        try {
            buffertest.appendValue(1,65);
        } catch(Exception ex) {System.out.println("active channel positive range pass");}

        try {
            buffertest.appendValue(1,-10);
        } catch(Exception ex) {System.out.println("active channel negative range pass");}

    }

    /**
     * attempt to write not in increasing order
     */
    @Test
    void GSBuffer_WriteChannel_order()
    {
        try {
            buffertest = new GSBuffer( 2000, 64);
            buffertest.appendValue(1,1);
            buffertest.appendValue(1,3);
        } catch (Exception ex) {fail(ex);}

        try {
            buffertest.appendValue(1,2);
        } catch(Exception ex) {System.out.println("active channel out of order pass");}

    }

    /**
     * Retrieve active channels for current timepoint
     */
    @Test
    void GSBuffer_getActiveChannels()
    {
        try {
            buffertest = new GSBuffer( 2000, 64);
            buffertest.appendValue(1,1);
            buffertest.appendValue(1,2);
            buffertest.appendValue(1,3);
        } catch (Exception ex) {fail(ex);}

        TreeSet<Integer> returnset = new TreeSet<>(buffertest.getActiveChannels());

        assertEquals(3, returnset.size());

    }

    /**
     * loop to write several channels to several time points.
     * check the written values using "getTPValues"
     */
    @Test
    void GSBuffer_MultipleTimePoints()
    {
        try {
            buffertest = new GSBuffer( 2000, 5);
        } catch (Exception ex) {fail(ex);}

        for(int tp=0; tp<1000; tp++)
        {
            for(int chan=0; chan < 5; chan++)
            {
                if( tp%2 == 0) {
                    try {
                        double voltage = 0.1 * chan;
                        buffertest.appendValue(voltage, chan);
                    } catch (Exception ex) {fail(ex);}
                } else {
                    try {
                        double voltage = -0.1*chan;
                        buffertest.appendValue(voltage, chan);
                    } catch (Exception ex) {fail(ex);}
                }
            }
            try{buffertest.appendEndofTP();} catch(Exception ex) {fail(ex);}
        }
        try{buffertest.appendEndofFunction();} catch(Exception ex) {fail(ex);}

        // check negatives, EOG and EOF flags
        HashMap<Integer, Short> tpMap = buffertest.getTPValues(999);
        assertEquals((short)(-0.1*0*(Math.pow(2,15))), (short)tpMap.get(0));
        assertEquals((short)(-0.1*1*(Math.pow(2,15))), (short)tpMap.get(1));
        assertEquals((short)(-0.1*2*(Math.pow(2,15))), (short)tpMap.get(2));
        assertEquals((short)(-0.1*3*(Math.pow(2,15))), (short)tpMap.get(3));
        assertEquals((short)(-0.1*4*(Math.pow(2,15))), (short)tpMap.get(4));

        // check positives, EOG flag
        HashMap<Integer, Short> tpMap2 = buffertest.getTPValues(0);
        assertEquals((short)(0.1*0*(Math.pow(2,15)-1)), (short)tpMap2.get(0));
        assertEquals((short)(0.1*1*(Math.pow(2,15)-1)), (short)tpMap2.get(1));
        assertEquals((short)(0.1*2*(Math.pow(2,15)-1)), (short)tpMap2.get(2));
        assertEquals((short)(0.1*3*(Math.pow(2,15)-1)), (short)tpMap2.get(3));
        assertEquals((short)(0.1*4*(Math.pow(2,15)-1)), (short)tpMap2.get(4));

    }




}
