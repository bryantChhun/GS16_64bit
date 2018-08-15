package GS;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GSBufferTests {

    GSBuffer buffertest;

    @Test
    public void testBufferMaxSize()
    {
        assertThrows(BufferTooLargeException.class, () -> new GSBuffer(4001, 64 ));

        assertThrows(BufferTooLargeException.class, () -> new GSBuffer(3999, 64 ));

    }

    @Test
    public void testVoltageToIntConversion_negative()
    {
        try {
            buffertest = new GSBuffer(2000, 64);
        } catch (BufferTooLargeException ex) {System.out.println("voltage to int conversion buffer creation fail");}

        try {
            buffertest.appendValue(-1,0);
        } catch(ActiveChanException ex) {}

        assertEquals(buffertest.getLastValue(), -1);
    }

    //test volt to int positive
        // assertEquals
    //test volt to int zero
        // assertEquals

    //test append end of tp
        // write eog twice, (pass on exception)
        // assertEquals(bitshift value is 1)

    //test append end of function
        // try writing when eog doesn't exist, (pass on exception)
        // try writing when eof flag already exists, (pass on exception)
        // assertEquals(bitshift value is 1)

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
