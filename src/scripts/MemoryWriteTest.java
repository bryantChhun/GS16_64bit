package scripts;

import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.NativeLongByReference;

/**
 * @author Bryant Chhun
 */
public class MemoryWriteTest {
    /***
     * Creates Memory block in heap.  Assign data values in NativeLong format.
     * Then try to pass entire memory block to NativeLongByReference bufftest
     * @param args //not used
     */

    public static void main(String[] args) {


        Pointer ex8p = new Memory(76); // 10 data points for now
        NativeLong dataval = new NativeLong();
        //dataval.setValue(0xFF);
        //ex8p.setNativeLong(0, dataval);

        for(int i=0; i< 10; i++){
            if(i%2 == 0){
                dataval.setValue(0xFFFFFF);
                ex8p.setNativeLong( 8*i, dataval);  // allocates 32 bits for every data point
            } else {
                dataval.setValue(( 1<< 24) | 0xFFFF );
                ex8p.setNativeLong(8*i, dataval);   // allocates 32 bits for every data point
            }
        }

        System.out.println("ex8p class = " + ex8p.getClass().toString());
        System.out.println("ex8p size = " + ((Memory) ex8p).size());
        System.out.println("ex8p val 0 = " + ex8p.getNativeLong(0).longValue());
        System.out.println("ex8p val 1 = " + ex8p.getNativeLong(8).longValue());
        System.out.println("ex8p val 2 = " + ex8p.getNativeLong(16).longValue());
        System.out.println("ex8p val 3 = " + ex8p.getNativeLong(24).longValue());
        System.out.println("ex8p val 4 = " + ex8p.getNativeLong(32).longValue());
        System.out.println("ex8p val 5 = " + ex8p.getNativeLong(40).longValue());
        System.out.println("ex8p val 6 = " + ex8p.getNativeLong(48).longValue());
        System.out.println("ex8p val 7 = " + ex8p.getNativeLong(56).longValue());
        System.out.println("ex8p val 8 = " + ex8p.getNativeLong(64).longValue());
        System.out.println("ex8p val 9 = " + ex8p.getNativeLong(72).longValue());

        NativeLongByReference bufftest = new NativeLongByReference();
        bufftest.setPointer(ex8p);


        System.out.print("done with main");
    }
}
