package GS;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import constants.GSConstants;

import java.util.ArrayDeque;

public class GSSequencerTests {

    private GSBuffer bufferTest1;
    private GSBuffer bufferTest2;
    private GSBuffer bufferTest3;

    private GSSequencer sequencerTest;

    //initialize constant values without initializing board
    private GSConstants constants = new GSConstants();

    private ArrayDeque<GSBuffer> arrayData;

    /**
     * simple initialization test
     */
    @Test
    void GSSequencer_testInitialize() {
        try {
            sequencerTest = new GSSequencer(65536, 50000);
            bufferTest1 = new GSBuffer(1000, 32);
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    void GSSequencer_testInitRange() {
        assertThrows(InvalidBoardParams.class, () -> new GSSequencer(-1, 50000));
        assertThrows(InvalidBoardParams.class, () -> new GSSequencer(0, 50000));
        assertThrows(InvalidBoardParams.class, () -> new GSSequencer(500000, 50000));
        assertThrows(InvalidBoardParams.class, () -> new GSSequencer(65536, 0));
        assertThrows(InvalidBoardParams.class, () -> new GSSequencer(65536, 500001));
    }

    @Test
    void GSSequencer_testPreFillRange() {
        try {
            sequencerTest = new GSSequencer(65536, 50000);
        } catch (Exception ex) {
            fail(ex);
        }

        arrayData = new ArrayDeque<>();

        try {
            bufferTest1 = new GSBuffer(4096, 16);
            bufferTest2 = new GSBuffer(4096, 16);
        } catch (Exception ex) {
            fail(ex);
        }

        continuousSineFunction(bufferTest1);

        for (int i = 0; i < 10; i++) {
            arrayData.push(bufferTest1);
        }

        sequencerTest.play(arrayData);
        //several buffers with very few values, # that are written before clock starts
        // two buffers with too high values (such that only one is written, and leaves thresh low)
    }

//    @Test GSSequencer_testArrayDeque()
//    {
//        // null entries
//        // entries with variable amount of values written
//    }

    @Test
    void GSSequencer_resetOutputs(){
        try {
            sequencerTest = new GSSequencer(65536, 50000);
        } catch (Exception ex) {
            fail(ex);
        }

        arrayData = new ArrayDeque<>();

        try {
            bufferTest1 = new GSBuffer(5, 64);
            for (int i = 0; i < 64; i++) {
                bufferTest1.appendValue(0, i);
                bufferTest1.appendEndofTP();
            }
            bufferTest1.appendEndofFunction();
            arrayData.push(bufferTest1);
        } catch (Exception ex) {
            fail(ex);
        }

    }

    /**
     * create array of 10 GSbuffers, test threshold triggering
     */
    @Test
    void GSSequencer_testSimpleSequence() {
        try {
            sequencerTest = new GSSequencer(65536, 50000);
        } catch (Exception ex) {
            fail(ex);
        }

        arrayData = new ArrayDeque<>();

        try {
            bufferTest1 = new GSBuffer(4096, 16);
        } catch (Exception ex) {
            fail(ex);
        }


        // Bryant: I played a bit with your code, fun! I wrote some other functions: ramp, sinus...
        // Turns out that the sinus function looks really weird on the oscilloscope... I think it has
        // to do with the interpretation of the values...
        //continuousSineFunction(bufferTest1);
        continuousStepFunction(bufferTest1);
        //continuousRampFunction2(bufferTest1);

        for (int i = 0; i < 1000; i++) {
            arrayData.push(bufferTest1);
        }

        sequencerTest.play(arrayData);

    }

    /**
     * For simple function generation, for testing
     *
     * @param data memory allocated from GSBuffer
     */
    private void continuousStepFunction(GSBuffer data) {
        int numTP = 4096;
        for (int loop = 0; loop < numTP; loop++) {
            if (loop % 2 == 0) {
                try {
                    for (int i = 0; i < 16; i++) {
                        data.appendValue(-.01, i);
                    }
                    //System.out.println(data.getLastValue());
                    data.appendEndofTP();
                } catch (Exception ex) {
                    fail(ex);
                }
            } else {
                try {
                    for (int i = 0; i < 16; i++) {
                        data.appendValue(0, i);
                    }
                    //System.out.println(data.getLastValue());
                    data.appendEndofTP();
                } catch (Exception ex) {
                    fail(ex);
                }
            }
        } // end for loop

        try {
            data.appendEndofFunction();
        } catch (Exception ex) {
            fail(ex);
        }
    }

    /**
     * For simple function generation, for testing
     *
     * @param data memory allocated from GSBuffer
     */
    private void continuousSineFunction(GSBuffer data) {
        int numTP = 4096;
        try {
            data.appendValue(1, 0);
            data.appendValue(-1, 1);
            for (int loop = 0; loop < numTP; loop++) {
                //float value = (float) Math.sin(0.001*loop+0.0001*i);
                float value = (float) Math.sin(0.001*loop);
                data.appendValue(value, 2);
                data.appendEndofTP();
            } // end for loop
            data.appendEndofFunction();

        } catch (Exception ex) {
            fail(ex);
        }
    }

    /**
     * For simple function generation, for testing
     *
     * @param data memory allocated from GSBuffer
     */
    private void continuousRampFunction(GSBuffer data) {
        int numTP = 4096;
        try {
            for (int loop = 0; loop < numTP; loop++) {
                for (int i = 0; i < 16; i++) {

                    float value = (float) (((1.0/numTP)*loop+0.00001*i)%1);
                    data.appendValue(value, i);
                }
                data.appendEndofTP();
            } // end for loop
            data.appendEndofFunction();

        } catch (Exception ex) {
            fail(ex);
        }
    }


    private void continuousRampFunction2(GSBuffer data) {
        int numTP = 1000;
        try {
            for (int loop = 0; loop < numTP; loop++) {
                float value = (float) (((-1.0/numTP)*loop)%1);
                System.out.println(value);
                data.appendValue(value, 0);
                //System.out.println(data.getLastValue());
                data.appendEndofTP();
            } // end for loop
            data.appendEndofFunction();

        } catch (Exception ex) {
            fail(ex);
        }
    }

}
