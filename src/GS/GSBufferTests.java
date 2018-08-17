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
    private GSConstants constants = new GSConstants();

    @Test
    /**
     * test GS buffer constructor for too much data and > 3/4 full.
     */
    public void GSBuffer_BufferMaxSize()
    {
        assertThrows(BufferTooLargeException.class, () -> new GSBuffer(constants, 4001, 64 ));

        assertThrows(BufferTooLargeException.class, () -> new GSBuffer(constants,3999, 64 ));

        assertThrows(BufferTooLargeException.class, () -> new GSBuffer(constants,2000, 500 ));
    }

    @Test
    /**
     * test that voltage converter retains range of short
     */
    public void GSBuffer_VoltageToIntConversion_negativeMax()
    {
        try {
            buffertest = new GSBuffer( constants, 2000, 64);
        } catch (Exception ex) {fail(ex);}

        try {
            buffertest.appendValue(-1.0,1);
        } catch (Exception ex) {fail(ex);}

        assertEquals(-32768, buffertest.getLastValue() );
    }

    @Test
    /**
     * test that voltage converter retains range of short
     */
    public void GSBuffer_VoltageToIntConversion_positiveMax()
    {
        try {
            buffertest = new GSBuffer( constants, 2000, 64);
        } catch (Exception ex) {fail(ex);}

        try {
            buffertest.appendValue(1.0,1);
        } catch (Exception ex) {fail(ex);}

        assertEquals(32767, buffertest.getLastValue() );
    }

    @Test
    /**
     * test that zero voltage is handled
     */
    public void GSBuffer_VoltageToIntConversion_zero()
    {
        try {
            buffertest = new GSBuffer( constants, 2000, 64);
        } catch (Exception ex) {fail(ex);}

        try {
            buffertest.appendValue(0,1);
        } catch (Exception ex) {fail(ex);}

        assertEquals(0, buffertest.getLastValue());
    }

    @Test
    /**
     * test negative bounds exception
     */
    public void GSBuffer_VoltageToIntConversion_limit_low()
    {
        try {
            buffertest = new GSBuffer( constants, 2000, 64);
        } catch (Exception ex) {fail(ex);}

        try {
            buffertest.appendValue(-10,1);
        } catch(ActiveChanException ACex) {
            fail(ACex);
        } catch(VoltageRangeException VRex){
            System.out.println("voltage limit low pass");
        }
    }

    @Test
    /**
     * test positive bounds exception
     */
    public void GSBuffer_VoltageToIntConversion_limit_high()
    {
        try {
            buffertest = new GSBuffer( constants, 2000, 64);
        } catch (Exception ex) {fail(ex);}

        try {
            buffertest.appendValue(10,1);
        } catch(ActiveChanException ACex) {
            fail(ACex);
        } catch(VoltageRangeException VRex){
            System.out.println("voltage limit high pass");
        }
    }

    @Test
    /**
     * EOGFlag: test handling of double TP flag write
     * // is this necessary?  double write does nothing anyway...
     */
    public void GSBuffer_EOG_2xWrite()
    {
        try {
            buffertest = new GSBuffer( constants, 2000, 64);
        } catch (Exception ex) {fail(ex);}

        try {
            buffertest.appendValue(0.5,1);
        } catch (Exception ex) {fail(ex);}

        try {
            buffertest.appendEndofTP();
        } catch(FlagException ex) {}

        try {
            buffertest.appendEndofTP();
        } catch(FlagException ex) {System.out.println("EOG 2x write pass");}
    }

    @Test
    /**
     * EOG flag: test correct placement of TP flag
     */
    public void GSBuffer_EOG_correctFlag()
    {
        try {
            buffertest = new GSBuffer( constants, 2000, 64);
        } catch (Exception ex) {fail(ex);}

        try {
            buffertest.appendValue(1,1);
        } catch(Exception ex) {fail(ex);}

        try {
            buffertest.appendEndofTP();
        } catch(FlagException ex) {fail(ex);}

        assertEquals(1, buffertest.getLastBlock() >> constants.eog.intValue());
    }

    @Test
    /**
     * EOF flag: test correct placement of end of buffer flag
     * 4 byte int can't hold a number with this tag
     *  it will return 2's complement.
     */
    public void GSBuffer_EOF_correctFlag()
    {
        try {
            buffertest = new GSBuffer( constants, 2000, 64);
        } catch (Exception ex) {fail(ex);}

        try {
            buffertest.appendValue(1,1);
        } catch(Exception ex) {fail(ex);}

        try {
            buffertest.appendEndofTP();
            buffertest.appendEndofFunction();
        } catch(FlagException ex) {fail(ex);}

        assertEquals(1, -1*(buffertest.getLastBlock() >> constants.eof.intValue()) );
    }

    @Test
    /**
     * attempt to write same active channel twice
     */
    public void GSBuffer_WriteChannel_2x()
    {
        try {
            buffertest = new GSBuffer( constants, 2000, 64);
        } catch (Exception ex) {fail(ex);}

        try {
            buffertest.appendValue(1,1);
        } catch(Exception ex) {fail(ex);}

        try {
            buffertest.appendValue(0,1);
        } catch(Exception ex) {System.out.println("same active channel pass");}

    }

    @Test
    /**
     * attempt to write beyond channel range
     */
    public void GSBuffer_WriteChannel_range()
    {
        try {
            buffertest = new GSBuffer( constants, 2000, 64);
        } catch (Exception ex) {fail(ex);}

        try {
            buffertest.appendValue(1,65);
        } catch(Exception ex) {System.out.println("active channel positive range pass");}

        try {
            buffertest.appendValue(1,-10);
        } catch(Exception ex) {System.out.println("active channel negative range pass");}

    }

    @Test
    /**
     * attempt to write not in increasing order
     */
    public void GSBuffer_WriteChannel_order()
    {
        try {
            buffertest = new GSBuffer( constants, 2000, 64);
        } catch (Exception ex) {fail(ex);}

        try {
            buffertest.appendValue(1,1);
        } catch(Exception ex) {fail(ex);}

        try {
            buffertest.appendValue(1,3);
        } catch(Exception ex) {fail(ex);}

        try {
            buffertest.appendValue(1,2);
        } catch(Exception ex) {System.out.println("active channel out of order pass");}

    }


    @Test
    /**
     * Retrieve active channels for current timepoint
     */
    public void GSBuffer_getActiveChannels()
    {
        try {
            buffertest = new GSBuffer( constants, 2000, 64);
        } catch (Exception ex) {fail(ex);}

        try {
            buffertest.appendValue(1,1);
        } catch(Exception ex) {fail(ex);}

        try {
            buffertest.appendValue(1,2);
        } catch(Exception ex) {fail(ex);}

        try {
            buffertest.appendValue(1,3);
        } catch(Exception ex) {fail(ex);}

        TreeSet<Integer> returnset = new TreeSet<>(buffertest.getActiveChannels());

        assertEquals(3, returnset.size());

    }

    @Test
    /**
     *
     */
    public void GSBuffer_MultipleTimePoints()
    {
        try {
            buffertest = new GSBuffer( constants, 2000, 5);
        } catch (Exception ex) {fail(ex);}

        for(int tp=0; tp<1000; tp++)
        {
            for(double chan=0; chan < 5; chan++)
            {
                try{
                    buffertest.appendValue((chan+1)/5, (int)chan);
                } catch(Exception ex) {fail(ex);}
            }
            try{buffertest.appendEndofTP();} catch(Exception ex) {fail(ex);}
        }
        //try{buffertest.appendEndofFunction();} catch(Exception ex) {fail(ex);}

        HashMap<Integer, Short> tpMap = buffertest.getTPValues(100);

        assertEquals((short)(0.2*32767), (short)tpMap.get(0));
        assertEquals((short)(0.4*32767), (short)tpMap.get(1));
        assertEquals((short)(0.6*32767), (short)tpMap.get(2));
        assertEquals((short)(0.8*32767), (short)tpMap.get(3));
        assertEquals((short)(1.0*32767), (short)tpMap.get(4));

    }


    //test multiple writes - one channel time series (varying values)
        // loop values,
            // write one channel
        // fail on exception
        //

    //test multiple writes - multi channel same values

    //test multiple writes -- multi channel multiple values
}
