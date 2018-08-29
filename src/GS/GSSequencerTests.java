package GS;

import GSExceptions.InvalidBoardParamsException;
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
            sequencerTest = new GSSequencer(65536, 40000);
            bufferTest1 = new GSBuffer(1000, 32);
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    void GSSequencer_testInitRange() {
        assertThrows(InvalidBoardParamsException.class, () -> new GSSequencer(-1, 50000));
        assertThrows(InvalidBoardParamsException.class, () -> new GSSequencer(0, 50000));
        assertThrows(InvalidBoardParamsException.class, () -> new GSSequencer(500000, 50000));
        assertThrows(InvalidBoardParamsException.class, () -> new GSSequencer(65536, 0));
        assertThrows(InvalidBoardParamsException.class, () -> new GSSequencer(65536, 500001));
    }

    @Test
    void GSSequencer_testPreFillRange_standardSize()
    {
        try {
            sequencerTest = new GSSequencer(65536, 40000);
            bufferTest1 = new GSBuffer(4096, 16);
        } catch (Exception ex) {
            fail(ex);
        }

        arrayData = new ArrayDeque<>();

        continuousStepFunction(bufferTest1, 4096);

        for (int i = 0; i < 10; i++) {
            arrayData.push(bufferTest1);
        }

        if(sequencerTest.play(arrayData, 1))
        {
            System.out.println("successful GSBuffer prefill test (10 buffers)");
        } else{
            fail("Fail to play 10 buffers");
        }
    }

    @Test
    void GSSequencer_testPreFillRange_smallSize()
    {
        try {
            sequencerTest = new GSSequencer(65536, 40000);
            bufferTest1 = new GSBuffer(1, 1);
            bufferTest1.appendValue(0.5, 0);
            bufferTest1.appendEndofTP();
            bufferTest1.appendEndofFunction();
        } catch (Exception ex) {
            fail(ex);
        }

        arrayData = new ArrayDeque<>();

        for (int i = 0; i < 10; i++) {
            arrayData.push(bufferTest1);
        }

        if(sequencerTest.play(arrayData, 1))
        {
            System.out.println("Successful write even with too few values to trigger thresh");
        } else{
            fail("unsuccessful catch of prefill buffer when very few values written");
        }
    }

    /**
     * because we set the GSBuffer max size to 50% the capacity of the DMA buffer,
     *  we must keep the num_threshold_values under 50% for proper operation
     */
    @Test
    void GSSequencer_testPreFillRange_largeSize()
    {
        try {
            sequencerTest = new GSSequencer(192000, 40000);
            bufferTest1 = new GSBuffer(4096, 16);
            bufferTest2 = new GSBuffer(7999, 16);
        } catch (Exception ex) {
            fail(ex);
        }

        continuousStepFunction(bufferTest1, 4096);
        continuousStepFunction(bufferTest2, 7999);

        arrayData = new ArrayDeque<>();

        for (int i = 0; i < 10; i++) {
            arrayData.push(bufferTest1);
            arrayData.push(bufferTest1);
            arrayData.push(bufferTest2);
        }

        if(sequencerTest.play(arrayData, 1))
        {
            System.out.println("Successful write even with overFlow prefill buffer event");
        } else{
            fail("failure to catch overflow prefill buffer.");
        }
    }

    @Test
    void GSSequencer_testPreFillRange_nullBuffers()
    {
        try {
            sequencerTest = new GSSequencer(65536, 40000);
            bufferTest1 = new GSBuffer(4096, 16);
            bufferTest2 = new GSBuffer(12289, 16);
        } catch (Exception ex) {
            fail(ex);
        }

        continuousStepFunction(bufferTest1, 4096);
        continuousStepFunction(bufferTest2, 12289);

        arrayData = new ArrayDeque<>();

        for (int i = 0; i < 10; i++) {
            arrayData.push(bufferTest1);
            arrayData.push(bufferTest2);
        }

        if(sequencerTest.play(arrayData, 1))
        {
            System.out.println("Successful write even with overFlow prefill buffer event");
        } else{
            fail("failure to catch overflow prefill buffer.");
        }
    }


    /**
     * reset all outputs to zero.
     */
    @Test
    void GSSequencer_resetOutputs(){
        try {
            sequencerTest = new GSSequencer(65536, 40000);
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
            sequencerTest = new GSSequencer(65536, 40000);
            bufferTest1 = new GSBuffer(4096, 16);
        } catch (Exception ex) {
            fail(ex);
        }

        arrayData = new ArrayDeque<>();

        // Bryant: I played a bit with your code, fun! I wrote some other functions: ramp, sinus...
        // Turns out that the sinus function looks really weird on the oscilloscope... I think it has
        // to do with the interpretation of the values...
        continuousSineFunction(bufferTest1, 4096);
        //continuousStepFunction(bufferTest1, 4096);
        //continuousRampFunction2(bufferTest1);

        for (int i = 0; i < 1000; i++) {
            arrayData.push(bufferTest1);
        }

        sequencerTest.play(arrayData, 0);

    }

    /**
     * For simple function generation, for testing
     *
     * @param data memory allocated from GSBuffer
     */
    private void continuousStepFunction(GSBuffer data, int timepoints) {
        int numTP = timepoints;
        try {
            for (int loop = 0; loop < numTP; loop++) {
                if (loop % 2 == 0) {
                    try {
                        for (int i = 0; i < 16; i++) {
                            data.appendValue(0, i);
                        }
                        data.appendEndofTP();
                    } catch (Exception ex) {
                        fail(ex);
                    }
                } else {
                    try {
                        for (int i = 0; i < 16; i++) {
                            data.appendValue(-1, i);
                        }
                        data.appendEndofTP();
                    } catch (Exception ex) {
                        fail(ex);
                    }
                }
            }
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
    private void continuousSineFunction(GSBuffer data, int timepoints) {
        int numTP = timepoints;
        try {
            for (int loop = 0; loop < numTP; loop++) {
                for (int i = 0; i<16; i++){
                    float value = (float) Math.sin(0.01*loop+0.01*i);
                    data.appendValue(value, i);
                }
                data.appendEndofTP();
            }
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
    private void continuousRampFunction(GSBuffer data, int timepoints) {
        int numTP = timepoints;
        try {
            for (int loop = 0; loop < numTP; loop++) {
                for (int i = 0; i < 16; i++) {
                    float value = (float) (((1.0/numTP)*loop+0.00001*i)%1);
                    data.appendValue(value, i);
                }
                data.appendEndofTP();
            }
            data.appendEndofFunction();
        } catch (Exception ex) {
            fail(ex);
        }
    }

}
