package whereintheworld;

import java.util.*;

public class NicoGeoInput {

    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);
        String line;

        while (scan.hasNextLine()) {
            line = "";
            Scanner s = new Scanner(scan.nextLine());
            try {
                System.out.println();
            } catch (EmptyStackException e) {
                System.out.println("Error: too few operands");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            s.close();
        }

        scan.close();
    }
}