package scripts;


public class datatypeTest {


    public static void main(String[] args) {

        float fvoltage = 5.3f;
        double dvoltage = 0.5;
        short svoltage = -32767;
        short svoltage_high = 32767;

//        int[] binary = tobinary(svoltage_high);

//        System.out.println("voltage f = "+fvoltage);
//        System.out.println("voltage d = "+dvoltage);
//        System.out.println("voltage if = "+(int)fvoltage);
//        System.out.println("voltage id = "+(int)dvoltage);
//        System.out.println("voltage short = "+svoltage);

//        for(int i = 15; i>=0; i--){
//            System.out.print(binary[i]);
//        }

        String text = tobinaryString(svoltage);
        String text2 = tobinaryString(svoltage_high);
        System.out.println(text.substring(text.length()-16));
        System.out.println(text2);


    }

    public static int[] tobinary(int num) {
        int[] binary = new int[40];
        int index = 0;
        while(num > 0 ){
            binary[index++] = num%2;
            num = num/2;
        }
        return binary;
    }

    public static String tobinaryString(int num) {
        return Integer.toBinaryString(num);
    }

}
