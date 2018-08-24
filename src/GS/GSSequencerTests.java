package GS;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import constants.GSConstants;

import java.util.ArrayDeque;

public class GSSequencerTests {

    private GSBuffer bufferTest1;

    private GSSequencer sequencerTest;

    //initialize constant values without initializing board
    private GSConstants constants = new GSConstants();

    private ArrayDeque<GSBuffer> arrayData;

    /**
     * simple initialization test
     */
    @Test
    void GSSequencer_testInitialize()
    {
        try{
            sequencerTest = new GSSequencer(65536, 50000);
            bufferTest1 = new GSBuffer(1000, 32);
        } catch (Exception ex) {fail(ex);}
    }

    @Test
    void GSSequencer_testInitRange()
    {
        assertThrows(InvalidBoardParams.class, () -> new GSSequencer(-1, 50000 ));
        assertThrows(InvalidBoardParams.class, () -> new GSSequencer(0, 50000 ));
        assertThrows(InvalidBoardParams.class, () -> new GSSequencer(500000, 50000 ));
        assertThrows(InvalidBoardParams.class, () -> new GSSequencer(65536, 0 ));
        assertThrows(InvalidBoardParams.class, () -> new GSSequencer(65536, 500001 ));
    }

    @Test
    void GSSequencer_testPreFillRange()
    {
        try {
            sequencerTest = new GSSequencer(65536, 50000);
        } catch (Exception ex) {fail(ex);}
        arrayData = new ArrayDeque<>();

        try{
            bufferTest1 = new GSBuffer(4096, 16);
        } catch (Exception ex) {fail(ex);}

        continuousFunction(bufferTest1);

        for (int i = 0; i<10; i++)
        {
            arrayData.push(bufferTest1);
        }

        sequencerTest.play(arrayData);
        //several buffers with very few values, # that are written before clock starts
        // two buffers with too high values (such that only one is written, and leaves thresh low)
    }

    @Test GSSequencer_testArrayDeque()
    {
        // null entries
        // entries with variable amount of values written
    }

    /**
     * create array of 10 GSbuffers, test threshold triggering
     */
    @Test
    void GSSequencer_testSimpleSequence()
    {
        try {
            sequencerTest = new GSSequencer(65536, 50000);
        } catch (Exception ex) {fail(ex);}
        arrayData = new ArrayDeque<>();

        try{
            bufferTest1 = new GSBuffer(4096, 16);
        } catch (Exception ex) {fail(ex);}

        continuousFunction(bufferTest1);

        for (int i = 0; i<10; i++)
        {
            arrayData.push(bufferTest1);
        }

        sequencerTest.play(arrayData);

    }

    /**
     * For simple function generation, for testing
     * @param data memory allocated from GSBuffer
     */
    private void continuousFunction(GSBuffer data)
    {
        int numTP = 4096;
        for(int loop=0; loop<numTP; loop++)
        {
            if(loop%2 == 0) {
                try {
                    for (int i = 0; i < 16; i++) {
                        data.appendValue(0.25, i);
                    }
                    data.appendEndofTP();
                } catch (Exception ex) {fail(ex);}
            } else {
                try {
                    for (int i = 0; i < 16; i++) {
                        data.appendValue(0.75, i);
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
