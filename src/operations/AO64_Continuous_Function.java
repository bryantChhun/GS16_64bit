package operations;

import bindings.AO64_64b_Driver_CLibrary;
import bindings.GS_NOTIFY_OBJECT;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.NativeLongByReference;
import constants.c;
import scripts.example;
import com.sun.jna.NativeLong;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinDef.DWORD;
import java.util.Scanner;


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
    public NativeLong dataval;
    public final Pointer data;

    public AO64_Continuous_Function(AO64_64b_Driver_CLibrary INSTANCE, example ex){

        System.out.println("\nContinuous function:");

        lINSTANCE= INSTANCE;
        lex = ex;
        numTimes = 4096; //Warning: allocated buffer allows 256k-samples, don't overflow.
        Event = new GS_NOTIFY_OBJECT();
        EventStatus = new DWORD();
        WAIT_ABANDONED = new DWORD(); WAIT_ABANDONED.setValue(0x00000080);
        WAIT_OBJECT_0 = new DWORD(); WAIT_OBJECT_0.setValue(0x00000000);
        WAIT_TIMEOUT = new DWORD(); WAIT_TIMEOUT.setValue(0x00000102);
        WAIT_FAILED = new DWORD(); WAIT_FAILED.setValue(0xFFFFFFFF);
        c.ulChannel = new NativeLong(); c.ulChannel.setValue(0x01);
        c.ulWords = new NativeLong(); c.ulWords.setValue(0x10000);
        c.BuffPtr = new NativeLongByReference();
        Scanner keyboard = new Scanner(System.in);
        String input;


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

        // buffer threshold
        NativeLong val = new NativeLong(); val.setValue(65536);
        lINSTANCE.AO64_66_Write_Local32(c.ulBdNum, c.ulError, c.BUFFER_THRSHLD, val);

        // Generate square data
        System.out.println("generating square, assigning pointers");
        data = generate_square();
        c.BuffPtr.setPointer(data.share(0));

        // creating handlers
        myHandle = Kernel32.INSTANCE.CreateEvent(null, false, false, null);
        if( myHandle == null){
            System.out.println("Insufficient Resources ...");
            try { System.in.read(); } catch (Exception except) { System.out.println(except); }
            System.exit(1);
        }

        // Store event handle
        // hEvent is a U64 object, to which we assign a pointer to the handle.
        Event.hEvent.setPointer(myHandle.getPointer());
        // enable local interrupt (not DMA)
        c.LOCAL = new NativeLong(); c.LOCAL.setValue(0);
        // monitor interrupt=4, Buffer threshold flag High-to-Low transition.
        NativeLong ulValue = new NativeLong(); ulValue.setValue(0x04);
        lINSTANCE.AO64_66_EnableInterrupt(c.ulBdNum, ulValue, c.LOCAL, c.ulError);
        lINSTANCE.AO64_66_Register_Interrupt_Notify(c.ulBdNum, Event, ulValue, c.LOCAL, c.ulError);

        System.out.println("Continuously Writing using interrupts now....");
        System.out.println("Checking data memory allocation = "+((Memory) data).size());

        c.ulChannel = new NativeLong(); c.ulChannel.setValue(0x01);
        c.ulWords = new NativeLong(); c.ulWords.setValue(0x10000);
        lINSTANCE.AO64_66_Open_DMA_Channel(c.ulBdNum, c.ulChannel, c.ulError);
        lINSTANCE.AO64_66_DMA_Transfer(c.ulBdNum, c.ulChannel, c.ulWords, c.BuffPtr, c.ulError);
        lINSTANCE.AO64_66_DMA_Transfer(c.ulBdNum, c.ulChannel, c.ulWords, c.BuffPtr, c.ulError);
        lINSTANCE.AO64_66_DMA_Transfer(c.ulBdNum, c.ulChannel, c.ulWords, c.BuffPtr, c.ulError);

        lex.AO64_Connect_Outputs();
        lINSTANCE.AO64_66_Enable_Clock(c.ulBdNum, c.ulError);

        while(true) {
            input = keyboard.nextLine();

            EventStatus.setValue(Kernel32.INSTANCE.WaitForSingleObject(myHandle, 3*1000));
            switch(EventStatus.intValue())
            {
                case 0://wait_object_0, object is signaled;
                    lINSTANCE.AO64_66_DMA_Transfer(c.ulBdNum, c.ulChannel, c.ulWords, c.BuffPtr, c.ulError);
                    lINSTANCE.AO64_66_DMA_Transfer(c.ulBdNum, c.ulChannel, c.ulWords, c.BuffPtr, c.ulError);
                    break;
                case 0x80://wait abandoned;
                    System.out.println("Error ... Wait abandoned");
                    break;
                case 0x102://wait timeout.  object stat is non signaled
                    System.out.println("Error ... Wait timeout");
                    break;
                case 0xFFFFFFFF:// wait failed.  Function failed.  call GetLastError for extended info.
                    System.out.println("Error ... Wait failed");
                    break;
//                default:
//                    System.out.println("Error ... Interrupt Timeout");
//                    break;
            }
            if("".equals(input))
                break;
        } //while (!("".equals(keyboard.nextLine())));

        System.out.println("Cancel Interrupt Notify");
        lINSTANCE.AO64_66_Cancel_Interrupt_Notify(c.ulBdNum, Event, c.ulError);
        System.out.println("Disable Interrupt");
        lINSTANCE.AO64_66_DisableInterrupt(c.ulBdNum, ulValue, c.LOCAL, c.ulError);
        System.out.println("Disable Clock");
        lINSTANCE.AO64_66_Disable_Clock(c.ulBdNum, c.ulError);
        System.out.println("Closing DMA channel");
        lINSTANCE.AO64_66_Close_DMA_Channel(c.ulBdNum, c.ulChannel, c.ulError);
        System.out.println("Closing Kernel Handle");
        Kernel32.INSTANCE.CloseHandle(myHandle);
    }


    public Memory generate_square(){

        Memory tempdata = new Memory(524288); // 65536 data points * 8 offsets/datapt = 524288 memory allocated
        NativeLong dataval = new NativeLong();
        // this loop should generate a 24.4 Hz square wave on 16 channels
        // 100000 / (65536/16) 100kHz sample rate, 65536 samples, 16 channels
        for(int loop=0; loop<numTimes; loop++)
        {
//             ex uses !(loop%2),
//             which I read as "if loop is divisible by 2, return 0 or False.  ! operator evaluates as True"
//             Therefore, "if loop is divisible by 2, execute"
            if(loop%2 == 0) {

                // assign all 16 channels
                for (int i = 0; i < 16; i++) {
                    dataval.setValue( ((i << c.id_off.intValue()) | 0x4000 ) );
                    tempdata.setNativeLong(16*loop*8 + i*8, dataval);
                }
                // eog tag is appended to last dataframe, i=15
                dataval.setValue( (15 << c.id_off.intValue()) | 0x4000 | (1 << c.eog.intValue()));
                tempdata.setNativeLong(16*loop*8 + 15*8, dataval);
            } else {
                for (int i = 0; i < 16; i++) {
                    dataval.setValue( (i << c.id_off.intValue()) | 0xC000 );
                    tempdata.setNativeLong(16*loop*8 + i*8, dataval);
                }
                // eog tag is appended to last dataframe, i=15
                dataval.setValue( (15 << c.id_off.intValue()) | 0xC000 | (1 << c.eog.intValue()));
                tempdata.setNativeLong(16*loop*8 + 15*8, dataval);
            }
        } // end for loop

//        for(int j=0; j<32; j++){
//            System.out.println("memory val = " + tempdata.getNativeLong(8*j).toString());
//        }

        return tempdata;

    } // end of generate_square


}
