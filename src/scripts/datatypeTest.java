package scripts;

public class datatypeTest {


    public static void main(String[] args) {

        float fvoltage = 5.3f;
        double dvoltage = 0.5;
        short svoltage = -32768;


        System.out.println("voltage f = "+fvoltage);
        System.out.println("voltage d = "+dvoltage);
        System.out.println("voltage if = "+(int)fvoltage);
        System.out.println("voltage id = "+(int)dvoltage);
        System.out.println("voltage short = "+svoltage);

    }

}
