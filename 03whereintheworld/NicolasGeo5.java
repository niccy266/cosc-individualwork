import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NicolasGeo5 {

    public static final char dg = '\u00B0';
    public static final int NONE = -1;
    public static final int LAT = 0;
    public static final int LON = 1;
    public static final int POS = 1;
    public static final int NEG = -1;
    public static final int LETTERS = 0;
    public static final int SYMBOLS = 1;
    public static final int DECIMAL = 1;
    public static final int DEGREE = -1;
    public static float coord = NONE;
    public static boolean signed = false;
    public static boolean cardinal = false;
    public static int axis = NONE;
    public static int sign = POS;
    public static float[] coords;
    public static String label;
    public static String t; // current token being processed

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
                store(coords, label);
            } else {
                System.out.println("Unable to process: " + line);
            }
        }
        scan.close();
    }

    public static boolean parse(String line) {
        System.out.println("\n" + line);
        if (line.charAt(0) == '#') {
            return false; // found a comment
        }
        if (!startsWithNumber(line)) {
            return false; // not in either format
        }

        if (validDegreesFormat(line)) {
            System.out.println("Parsing as degrees");
            return tryParseDegree(line);
        } else if (validDecimalFormat(line)) {
            System.out.println("Parsing as decimal");
            return tryParseDecimal(line);
        } else {
            return false; // unrecognized format
        }
    }

    // public static String coordToStr(float n) {
    // String num = Integer.toString(Math.round(n*1000000));
    // int decPoint = num.length() - 6
    // return num.substring(0, decPoint) + "." + num.substring(decPoint);
    // }

    /**
     * Tries to parse a line of input as coordinates in decimal format.
     * Tries to match input with decimal format coordinates, eg, 80, 170.
     * Returns if the the requirements for the format ever aren't met.
     * If the data is parsed succesfully, the coordinates and label
     * are passed to the output file using store()
     * 
     * @param line  the line of input to parse in decimal format
     * @param count number of input line in system.in for reporting bad data
     */
    public static boolean tryParseDecimal(String line) {
        signed = false;
        cardinal = false;
        coord = NONE;
        axis = NONE;
        sign = POS;
        coords = new float[] { NONE, NONE };
        label = "";

        // will only accept a number between 1 and 90
        // String latReg = "(([0-8]?[0-9](\\.[0-9]+)?)|90|90.0+)";
        String lonReg = "([0-9]+(\\.[0-9]+)?)[" + dg + "d]?";

        String pattern = "([+-])?" + lonReg + "(\\s?([NSWE]))?";
        pattern = pattern + ",?\\s?" + pattern + ",?\\s+(.*$)";
        System.out.println(pattern);
        Matcher m = Pattern.compile(pattern).matcher(line);

        if (m.find()) {
            for (int i = 0; i < m.groupCount(); i++) {
                System.out.println(i + " " + m.group(i));
            }

            int i = 0;
            if (parseDecimal(m, i) && parseDecimal(m, i + 5)) {
                label = line.replaceAll(pattern, "");
                return true;
            }

        } else {
            System.out.println("regex failed");
        }

        return false;
    }

    public static boolean parseDecimal(Matcher m, int i) {
        // checking if input had a + or - sign
        String s = m.group(i + 1);
        if (s != null && s.charAt(0) == '-') {
            signed = true;
            sign = NEG;
        }

        // checking is a cardinal letter was given
        String card = m.group(i + 5);
        if (card != null) {
            cardinal = true;
            char c = card.charAt(0);
            axis = latOrLon(c);
            sign = cardToSign(c);
        }

        if (cardinal && signed)
            return false; // used both signs and cardinal directions
        if (cardinal && axis == NONE)
            return false; // format uses cardinal letters but this one is missing

        coord = Float.parseFloat(m.group(i + 2));
        if (!cardinal) {
            if (coords[LAT] == NONE) {
                axis = LAT;
            } else {
                axis = LON;
            }
        }

        if (coords[axis] != NONE)
            return false; // two values given for same axis

        if (axis == LAT && coord > 90) {
            return false;
        } else if (axis == LON && coord > 180) {
            return false;
        }

        coords[axis] = sign * coord;
        return true;
    }

    // public static String coordToStr(float n) {
    // String num = Integer.toString(Math.round(n*1000000));
    // int decPoint = num.length() - 6
    // return num.substring(0, decPoint) + "." + num.substring(decPoint);
    // }

    /**
     * Tries to parse a line of input as coordinates in decimal format.
     * Tries to match input with decimal format coordinates, eg, 80, 170.
     * Returns if the the requirements for the format ever aren't met.
     * If the data is parsed succesfully, the coordinates and label
     * are passed to the output file using store()
     * 
     * @param line the line of input to parse in decimal format
     */
    public static boolean tryParseDegree(String line) {
        coord = NONE;
        axis = NONE;
        sign = POS;
        coords = new float[] { NONE, NONE };
        label = "";

        Matcher m;
        String matchSign = "";

        if (line.matches("[0-9]+\\s?d(.*)")) {
            matchSign = "dms";
        }

        if (line.matches("[0-9]+\\s?\u00B0(.*)")) {
            if (matchSign != "")
                return false; // can't use dms and dg " ' symbols
            matchSign = "\u00B0\'\"";
        }

        String intReg = "([0-5]?[0-9]|60)";
        String floatReg = "(([0-5]?[0-9](\\.[0-9]+)?)|60|60.0+)";
        String degReg = intReg + matchSign.charAt(0) + "\\s?";
        String minReg = intReg + matchSign.charAt(1) + "\\s?";
        String secReg = floatReg + matchSign.charAt(2) + "\\s?";

        String pattern = degReg + "(" + minReg + "(" + secReg + ")?)?" + "\\s?([NSWE])";
        pattern = pattern + ",?\\s?" + pattern + "\\s((.*$))?";
        m = Pattern.compile(pattern).matcher(line);

        if (m.find()) {

            int i = 0;
            parseDegree(m, i);
            parseDegree(m, i + 7);

            label = m.group(16);
            // line.replaceAll(pattern, "");
            return true;

        } else {
            System.out.println("regex failed");
            return false;
        }

    }

    public static boolean parseDegree(Matcher m, int i) {
        String deg_s = m.group(i + 1);
        String min_s = m.group(i + 3);
        String sec_s = m.group(i + 5);

        int deg = 0;
        int min = 0;
        float sec = 0;

        deg = Integer.parseInt(deg_s);
        if (deg > 60)
            return false;

        if (min_s != null) {
            min = Integer.parseInt(min_s);
            if (min > 60)
                return false;
            if (sec_s != null) {
                sec = Float.parseFloat(sec_s);
                if (sec > 60)
                    return false;
            }
        }
        char c = m.group(i + 7).charAt(0);

        sign = cardToSign(c);
        axis = latOrLon(c);
        coord = degToDecimal(deg, min, sec);
        if (coords[axis] != NONE)
            return false; // two values given for same axis
        coords[axis] = coord * sign;
        return true;
    }

    public static void store(float[] coords, String label) {
        System.out.println("[ " + Float.toString(coords[LAT]) +
                ", " + Float.toString(coords[LON]) + " ]" + label);
        return;
    }

    public static boolean startsWithNumber(String c) {
        return c.matches("[+-]?[0-9](.*)");
    }

    public static String removeComma(String c) {
        return c.charAt(c.length() - 1) == ',' ? c.substring(0, c.length() - 2) : c;
    }

    public static boolean isCardinal(char c) {
        return Character.toString(c).matches("[NSEW]");
    }

    public static boolean isSigned(char c) {
        return Character.toString(c).substring(0, 1).matches("[+-]");
    }

    public static boolean isNum(String n) {
        try {
            Float.parseFloat(n);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDirection(String n) {
        return n.length() == 1 && isCardinal(n.charAt(0));
    }

    public static int cardToSign(char c) {
        switch (c) {
            case 'S':
            case 'W':
                return -1;
            default:
                return 1;
        }
    }

    public static boolean validDegreesFormat(String c) {
        String pattern = "([0-9])*[d\u00B0]" + "\\s?([0-5]?[0-9]|60)[m\']" +
                "(\\s?([0-9][0-9]*)(\\.([0-9]*))?[s\"])?[NSEW]?,?(.)*";
        return c.matches(pattern);
    }

    public static boolean validDecimalFormat(String c) {
        String num = "[+-]?([0-9])*(\\.([0-9])+)?" + dg + "?(\\s?[NSEW])?";
        String sep = ",?\\s?";
        String end = "(\\s(.)*)?";
        return c.matches(num + sep + num + end);
    }

    public static int latOrLon(char c) {
        return c == 'N' || c == 'S' ? LAT : LON;
    }

    public static boolean areCoordsValid(float lat, float lon) {
        float absLat = (lat < 0) ? -lat : lat;
        float absLon = (lon < 0) ? -lon : lon;
        return absLat <= 90 && absLon <= 180;
    }

    public static float degToDecimal(int deg, int min, float sec) {
        float c = 0;
        c += deg;
        c += min / 60.0;
        c += sec / 3600;
        return c;
    }
}