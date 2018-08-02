package operations;

import bindings.AO64_64b_Driver_CLibrary;
import bindings.GS_NOTIFY_OBJECT;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.NativeLongByReference;
import constants.c;
import scripts.example;
import com.sun.jna.NativeLong;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.KeyboardUtils;


/**
 * @author Bryant Chhun
 */

/**
 * this example puts a continuous square wave function using DMA
 */
public class AO64_Continuous_Function {

    private AO64_64b_Driver_CLibrary lINSTANCE;
    private GS_NOTIFY_OBJECT Event;
    private HANDLE myHandle;
    private DWORD EventStatus, WAIT_ABANDONED, WAIT_OBJECT_0, WAIT_TIMEOUT, WAIT_FAILED;
    private int numTimes;
    private example lex;
    private NativeLong dataval;

    public AO64_Continuous_Function(AO64_64b_Driver_CLibrary INSTANCE, example ex){

        System.out.println("\nContinuous function:");

        lINSTANCE= INSTANCE;
        lex = ex;
        numTimes = 1024; //Warning: allocated buffer allows 256k-samples, don't overflow.
        Event = new GS_NOTIFY_OBJECT();
        EventStatus = new DWORD();
        WAIT_ABANDONED = new DWORD(); WAIT_ABANDONED.setValue(0x00000080);
        WAIT_OBJECT_0 = new DWORD(); WAIT_OBJECT_0.setValue(0x00000000);
        WAIT_TIMEOUT = new DWORD(); WAIT_TIMEOUT.setValue(0x00000102);
        WAIT_FAILED = new DWORD(); WAIT_FAILED.setValue(0xFFFFFFFF);
        c.ulChannel = new NativeLong(); c.ulChannel.setValue(0x01);
        c.ulWords = new NativeLong(); c.ulWords.setValue(0x10000);
        dataval = new NativeLong();

        System.out.println("Intializing the board");
        lINSTANCE.AO64_66_Initialize(c.ulBdNum, c.ulError);
        System.out.println("Initialization Complete");

        System.out.println("Set Sample rate");
        lINSTANCE.AO64_66_Set_Sample_Rate(c.ulBdNum, 500000.0, c.ulError);

        System.out.println("Autocalibrating the board");
        if(lINSTANCE.AO64_66_Autocal(c.ulBdNum, c.ulError).intValue() != 1)
        {
            System.out.println("Autocal Failed");
            System.exit(1);
        } else {
            System.out.println("Autocal Passed");
        }
        System.out.println("Please Verify that all Channels are now at Zero Volts");
        try { System.in.read(); } catch (Exception except) { System.out.println(except); }


        // setting pointers
        //c.ulData = new NativeLong[131072];
        // in the example, BuffPtr = REFERENCE to ulData[0]
        //c.BuffPtr.setValue(c.ulData[0]);


        // buffer threshold
        NativeLong val = new NativeLong(); val.setValue(65536);
        lINSTANCE.AO64_66_Write_Local32(c.ulBdNum, c.ulError, c.BUFFER_THRSHLD, val);

        generate_square();

//        myHandle = Kernel32.INSTANCE.CreateEvent(null, false, false, null);
//        if( myHandle == null){
//            System.out.println("Insufficient Resources ...");
//            try { System.in.read(); } catch (Exception except) { System.out.println(except); }
//            System.exit(1);
//        }

        // Store event handle
        // will this assignment work?
//        Event.hEvent.setPointer(myHandle.getPointer());
//        NativeLong ulValue = new NativeLong(); ulValue.setValue(0x04);
//        c.LOCAL = new NativeLong(); c.LOCAL.setValue(0);
//        lINSTANCE.AO64_66_EnableInterrupt(c.ulBdNum, ulValue, c.LOCAL, c.ulError);
//        lINSTANCE.AO64_66_Register_Interrupt_Notify(c.ulBdNum, Event, ulValue, c.LOCAL, c.ulError);

        System.out.println("Continuously Writing using interrupts now....");
        NativeLong channel = new NativeLong(); channel.setValue(0x01);
        NativeLong words = new NativeLong(); words.setValue(0x10000);
        lINSTANCE.AO64_66_Open_DMA_Channel(c.ulBdNum, channel, c.ulError);
        lINSTANCE.AO64_66_DMA_Transfer(c.ulBdNum, channel, words, c.BuffPtr, c.ulError);
        lINSTANCE.AO64_66_DMA_Transfer(c.ulBdNum, channel, words, c.BuffPtr, c.ulError);
        lINSTANCE.AO64_66_DMA_Transfer(c.ulBdNum, channel, words, c.BuffPtr, c.ulError);

        lex.AO64_Connect_Outputs();
        lINSTANCE.AO64_66_Enable_Clock(c.ulBdNum, c.ulError);
        System.out.println("Verify that Channels are written with square function");
        try { System.in.read(); } catch (Exception except) { System.out.println(except); }

        System.out.println("Disabling clock");
        lINSTANCE.AO64_66_Disable_Clock(c.ulBdNum, c.ulError);
        System.out.println("Closing DMA channel");
        lINSTANCE.AO64_66_Close_DMA_Channel(c.ulBdNum, channel, c.ulError);

//        DMA_sequence();

//        lINSTANCE.AO64_66_Cancel_Interrupt_Notify(c.ulBdNum, Event, c.ulError);
//        lINSTANCE.AO64_66_DisableInterrupt(c.ulBdNum, ulValue, c.LOCAL, c.ulError);
//        lINSTANCE.AO64_66_Disable_Clock(c.ulBdNum, c.ulError);
//        lINSTANCE.AO64_66_Close_DMA_Channel(c.ulBdNum, c.ulChannel, c.ulError);

//        Kernel32.INSTANCE.CloseHandle(myHandle);
    }


    private void generate_square(){

        //final Pointer ex8p = new Memory(numTimes * Native.getNativeSize(NativeLong.class)+16);
        Pointer ex8p = new Memory(65536);

        // this loop should generate a 24.4 Hz square wave on 16 channels
        // 100000 / (65536/16) 100kHz sample rate, 65536 samples, 16 channels
        for(int loop=0; loop<numTimes; loop++)
        {
            if(loop%64 == 0){
                System.out.printf("loop val = %s\n", loop);
            }
//             ex uses !(loop%2),
//             which I read as "if loop is divisible by 2, return 0 or False.  ! operator evaluates as True"
//             Therefore, "if loop is divisible by 2, execute"
            if(loop%2 == 0) {

                // assign all 16 channels
                for (int i = 0; i < 16; i++) {
                    dataval.setValue( (i << c.id_off.intValue()) | 0x4000 );
                    // should offset = loop*Native.getNativeSize(NativeLong.class) + i ???
                    ex8p.setNativeLong(loop * Native.getNativeSize(NativeLong.class) + i, dataval);
                }
                // eog tag is appended to last dataframe, i=15
                dataval.setValue( (15 << c.id_off.intValue()) | 0x4000 | (1 << c.eog.intValue()));
                ex8p.setNativeLong(loop * Native.getNativeSize(NativeLong.class) + 15, dataval);
            } else {
                for (int i = 0; i < 16; i++) {
                    dataval.setValue( (i << c.id_off.intValue()) | 0xC000 );
                    ex8p.setNativeLong(loop * Native.getNativeSize(NativeLong.class) + i, dataval);
                }
                // eog tag is appended to last dataframe, i=15
                dataval.setValue( (15 << c.id_off.intValue()) | 0xC000 | (1 << c.eog.intValue()));
                ex8p.setNativeLong(loop * Native.getNativeSize(NativeLong.class) + 15, dataval);
            }
        } // end for loop

        System.out.println("ex8p class = " + ex8p.getClass().toString());
        System.out.println("ex8p size = " + ((Memory) ex8p).size());
        System.out.println("ex8p val 0 = " + ex8p.getNativeLong(0).longValue());
        System.out.println("ex8p val 3 = " + ex8p.getNativeLong(3).longValue());
        System.out.println("ex8p val 7 = " + ex8p.getNativeLong(7).longValue());
        System.out.println("ex8p val 11 = " + ex8p.getNativeLong(11).longValue());
        System.out.println("ex8p val 15 = " + ex8p.getNativeLong(15).longValue());
        System.out.println("ex8p val 16 = " + ex8p.getNativeLong(16).longValue());
        System.out.println("ex8p val 19 = " + ex8p.getNativeLong(19).longValue());
        System.out.println("ex8p val 23 = " + ex8p.getNativeLong(23).longValue());
        System.out.println("ex8p val 27 = " + ex8p.getNativeLong(27).longValue());
        System.out.println("ex8p val 31 = " + ex8p.getNativeLong(31).longValue());
        //c.BuffPtr.setPointer(ex8p);

    } // end of generate_square

    private void DMA_sequence() {

        lINSTANCE.AO64_66_Open_DMA_Channel(c.ulBdNum, c.ulChannel, c.ulError);
        lINSTANCE.AO64_66_DMA_Transfer(c.ulBdNum, c.ulChannel, c.ulWords, c.BuffPtr, c.ulError);
        lINSTANCE.AO64_66_DMA_Transfer(c.ulBdNum, c.ulChannel, c.ulWords, c.BuffPtr, c.ulError);
        lINSTANCE.AO64_66_DMA_Transfer(c.ulBdNum, c.ulChannel, c.ulWords, c.BuffPtr, c.ulError);

        //kbflush reads as "while there is a keyboard key in buffer, getch (get it)

        lex.AO64_Connect_Outputs();
        lINSTANCE.AO64_66_Enable_Clock(c.ulBdNum, c.ulError);

//        do {
//
//            // need enum here?  Event Status needs to be a DWORD enum  or switch case won't work.
//            // example uses switch/case, which must be compile-time evaluable.  Enums might not work here.
//            EventStatus.setValue(Kernel32.INSTANCE.WaitForSingleObject(myHandle, 3*1000));
//
//            switch(EventStatus.intValue())
//            {
//                case 0://wait_object_0, object is signaled;
//                case 0x80://wait abandoned;
//                case 0x102://wait timeout.  object stat is non signaled
//                case 0xFFFFFFFF:// wait failed.  Function failed.  call GetLastError for extended info.
//
//            }
//
//        } while();
        // no java equiv of kbhit (keyboard hit) so will just use System.in.read()

    }


}
