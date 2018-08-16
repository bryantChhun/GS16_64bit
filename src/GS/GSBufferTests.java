package GS;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import constants.GSConstants;


public class GSBufferTests {

    private GSBuffer buffertest;
    private GSConstants constants = new GSConstants();

    @Test
    public void testBufferMaxSize()
    {
        assertThrows(BufferTooLargeException.class, () -> new GSBuffer(constants, 4001, 64 ));

        assertThrows(BufferTooLargeException.class, () -> new GSBuffer(constants,3999, 64 ));
    }

    @Test
    public void testVoltageToIntConversion_negativeMax()
    {
        try {
            buffertest = new GSBuffer( constants, 2000, 64);
        } catch (BufferTooLargeException ex) {fail("voltage to int conversion: "+ex);}

        try {
            buffertest.appendValue(-1.0,0);
        } catch(ActiveChanException ex) {} catch (VoltageRangeException vex) {fail("voltage to int conversion: "+vex);}

        assertEquals(-32768, buffertest.getLastValue() );
    }

    @Test
    public void testVoltageToIntConversion_positiveMax()
    {
        try {
            buffertest = new GSBuffer( constants, 2000, 64);
        } catch (BufferTooLargeException ex) {System.out.println("voltage to int conversion: "+ex);}

        try {
            buffertest.appendValue(1.0,0);
        } catch(ActiveChanException ex) {} catch (VoltageRangeException vex) {}

        assertEquals(32767, buffertest.getLastValue() );
    }

    @Test
    public void testVoltageToIntConversion_zero()
    {
        try {
            buffertest = new GSBuffer( constants, 2000, 64);
        } catch (BufferTooLargeException ex) {System.out.println("voltage to int conversion: "+ex);}

        try {
            buffertest.appendValue(0,0);
        } catch(ActiveChanException ex) {} catch (VoltageRangeException vex) {}

        assertEquals(0, buffertest.getLastValue());
    }

    @Test
    public void testVoltageToIntConversion_limit_low()
    {
        try {
            buffertest = new GSBuffer( constants, 2000, 64);
        } catch (BufferTooLargeException ex) {System.out.println("voltage to int conversion: "+ex);}

        try {
            buffertest.appendValue(-10,0);
        } catch(ActiveChanException ACex) {
            //do nothing
        } catch(VoltageRangeException VRex){
            System.out.println("voltage limit low pass");
        }
    }

    @Test
    public void testVoltageToIntConversion_limit_high()
    {
        try {
            buffertest = new GSBuffer( constants, 2000, 64);
        } catch (BufferTooLargeException ex) {System.out.println("voltage to int conversion: "+ex);}

        try {
            buffertest.appendValue(10,0);
        } catch(ActiveChanException ACex) {
            //do nothing
        } catch(VoltageRangeException VRex){
            System.out.println("voltage limit high pass");
        }
    }

    @Test
    /**
     * EOGFlag: test handling of double eog write
     */
    public void testEOGFlag_2xWrite()
    {
        try {
            buffertest = new GSBuffer( constants, 2000, 64);
        } catch (BufferTooLargeException ex) {System.out.println("EOGflag test: "+ex);}

        try {
            buffertest.appendValue(0.5,0);
        } catch(ActiveChanException ACex) {
            //do nothing
        } catch(VoltageRangeException VRex){
            //do nothing
        }

        try {
            buffertest.appendEndofTP();
        } catch(FlagException ex) {}

        try {
            buffertest.appendEndofTP();
        } catch(FlagException ex) {System.out.println(ex);}
    }

    @Test
    /**
     * EOGflag: test it's in correct place
     */
    public void testEOGFlag_correctFlag()
    {
        try {
            buffertest = new GSBuffer( constants, 2000, 64);
        } catch (BufferTooLargeException ex) {System.out.println("EOGflag test: "+ex);}

        try {
            buffertest.appendValue(0.5,1);
        } catch(ActiveChanException ACex) {
            //do nothing
        } catch(VoltageRangeException VRex){
            //do nothing
        }

        try {
            buffertest.appendEndofTP();
        } catch(FlagException ex) {}

        assertEquals(1,buffertest.getLastValue() >> constants.eog.intValue());

    }


    // WRITE TESTS

    //test channel validity, (appendValue)
        // -- try writing channel that exists (pass on exception)
        // -- try writing channel beyond range (pass on exception)
        // -- try writing channels out of order (pass on exception)

    //test multiple writes - one channel time series (varying values)
        // loop values,
            // write one channel
        // fail on exception
        //

    //test multiple writes - multi channel same values

    //test multiple writes -- multi channel multiple values
}
