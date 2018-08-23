package GS;

import com.sun.jna.NativeLong;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import constants.GSConstants;

import java.util.ArrayDeque;

public class GSSequencerTests {

    private GSBuffer bufferTest;
    private GSBuffer bufferTest1;
    private GSBuffer bufferTest2;
    private GSBuffer bufferTest3;

    private GSSequencer sequencerTest;
    private ArrayDeque<GSBuffer> arrayData;

    @Test
    void GSSequencer_constants()
    {
        try {
            bufferTest = new GSBuffer(1000, 32);
        } catch(BoardInitializeException BIex) {
            System.out.println(BIex);
        } catch(BufferTooLargeException Vex) {fail(Vex);}
    }

    @Test
    void GSSequencer_initialize()
    {
        try{
            sequencerTest = new GSSequencer();
            bufferTest = new GSBuffer(1000, 32);
        } catch (Exception ex) {fail(ex);}
    }


    @Test
    void GSSequencer_simpleSequence()
    {

        try{
            sequencerTest = new GSSequencer();
            bufferTest1 = new GSBuffer(1000, 32);
            bufferTest2 = new GSBuffer(1000, 32);
            bufferTest3 = new GSBuffer(1000, 32);
        } catch (Exception ex) {fail(ex);}

        continuousFunction(bufferTest1);
        continuousFunction(bufferTest2);
        continuousFunction(bufferTest3);

        arrayData.addLast(bufferTest1);
        arrayData.addLast(bufferTest2);
        arrayData.addLast(bufferTest3);

        sequencerTest.play(arrayData);

    }

    private void continuousFunction(GSBuffer data)
    {
        // 1000 timepoints
        for(int loop=0; loop<1000; loop++)
        {
            if(loop%2 == 0) {
                try {
                    for (int i = 0; i < 16; i++) {
                        data.appendValue(0x4000, i);
                    }
                    data.appendEndofTP();
                } catch (Exception ex) {fail(ex);}
            } else {
                try {
                    for (int i = 0; i < 16; i++) {
                        data.appendValue(0xC000, i);
                    }
                    data.appendEndofTP();
                } catch (Exception ex) {fail(ex);}
            }
        } // end for loop

        try {
            data.appendEndofFunction();
        } catch (Exception ex) {fail(ex);}
    }





}
