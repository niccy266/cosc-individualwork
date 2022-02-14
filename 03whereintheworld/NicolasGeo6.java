import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NicolasGeo6 {

    /*
     * Rules:
     * 1. Cannot use +/- to represent deirection on an axis
     * while also using cardinal letters
     * 2. If cardinal letters aren't used, latitude must come before longitude
     * 3. Label must be separated from coordinates by whitespace
     * 4. If dms (degrees, minutes, seconds) format is used, they must use
     * symbols with no whitespace after values: \u00B0 (degree symbol) ' "
     * or lowercase letters: d m s
     * 5. The two coordinates should be in the same format
     * 6. The coordinates must be in decimal or dms format
     * 7. The only cardinal letters allowed are N, S, W & E.
     * 6. If the two coordinates are on the same axis they are not valid.
     */

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        String line;
        while (scan.hasNextLine()) {
            line = scan.nextLine();
            if (parse(line)) {
            } else {
                System.out.println("Unable to process: " + line);
            }
        }
        scan.close();
    }

    public static boolean parse(String line) {

        Matcher m;
        String matchSign = "";

        if (line.matches("[0-9]+\\s?d(.*)")) {
            matchSign = "dms";
        }

        if (line.matches("[0-9]+\u00B0(.*)")) {
            if (matchSign != "")
                return false; // can't use dms and dg " ' symbols
            matchSign = "\u00B0\'\"";
        }

        System.out.println(matchSign);

        String intReg = "([0-9]+)";
        String floatReg = "([0-9]+(\\.[0-9]+)?)";
        String degReg = intReg + matchSign.charAt(0) + "\\s?";
        String minReg = intReg + matchSign.charAt(1) + "\\s?";
        String secReg = floatReg + matchSign.charAt(2) + "\\s?";

        String pattern = degReg + "(" + minReg + "(" + secReg + ")?)?" + "\\s?([NSWE])";
        pattern = pattern + ",?\\s?" + pattern + "\\s?";

        System.out.println(pattern);

        m = Pattern.compile(pattern).matcher(line);
        if (m.find()) {

            System.out.println("regex succeeded");
            System.out.println(m.group(0));

            return true;

        } else {
            System.out.println(m.pattern());
            System.out.println("regex failed");
            return false;
        }
    }
}