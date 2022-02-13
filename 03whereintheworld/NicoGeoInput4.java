import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import org.apache.commons.lang3.ArrayUtils;

import javax.print.DocFlavor.STRING;

public class NicoGeoInput4 {

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
        int count = 0;
        while (scan.hasNextLine()) {
            line = scan.nextLine();
            if (parse(line, count++)) {
                store(coords, label);
            } else {
                System.out.println("Unable to process: " + line);
            }
        }
        scan.close();
    }

    public static boolean parse(String line, int count) {
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

    public static int checkSign() {
        char s = t.charAt(0);
        if (isSigned(s)) {
            signed = true;

            if (s == '-') {
                sign = NEG;
            }

            if (t.length() > 1)
                t = t.substring(1); // remove sign so the number can be parsed
            else
                t = "";
        }
        return sign;
    }

    public static boolean checkCardinal() {
        char c = t.strip().charAt(t.length() - 1);
        System.out.println(c);
        if (isCardinal(c)) {
            // System.out.println("cardinal");
            cardinal = true;

            // if a direction is already staged
            if (axis != NONE) {
                return false; // two directions given before a value
            }

            // stage the direction
            sign = cardToCoord(c);
            axis = latOrLon(c);
            // checks if the staged axis already has a value
            if (coords[axis] != NONE) {
                return false; // given two values for the same axis
            }

            // if a value is ready to be assigned to an axis
            if (coord != NONE) {
                // store the staged value and direction
                coords[axis] = sign * coord;

                // clear stage for next coordinate and direction
                coord = NONE;
                axis = NONE;
                sign = POS;
            }

            // remove the cardinal letter
            t = t.substring(0, t.length() - 1);
        }
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
     * @param line  the line of input to parse in decimal format
     * @param count number of input line in system.in for reporting bad data
     */
    public static boolean tryParseDecimal(String line) {
        Scanner sc = new Scanner(line);
        sc.useDelimiter("[\\s," + dg + "]+");
        signed = false;
        cardinal = false;
        coord = NONE;
        axis = NONE;
        sign = POS;
        coords = new float[] { NONE, NONE };
        label = "";

        while (sc.hasNext()) {
            t = sc.next();
            System.out.println(t);
            // t = removeComma(t.strip());

            sign = checkSign();
            if (!checkCardinal()) {
                sc.close();
                return false;
            }

            if (signed && cardinal) {
                sc.close();
                return false;
            }
            System.out.println(t);

            if (coords[LAT] != NONE && coords[LON] != NONE) {
                // break from loop if we have a coordinate for both axes
                break;
            }

            if (isNum(t)) {
                // if another value is already staged but not saved
                if (coord != NONE) {
                    coords[LAT] = coord;
                    coords[LON] = Float.parseFloat(t);
                    break;
                }

                // otherwise stage the next value
                coord = Float.parseFloat(t);

                if (axis != NONE) {
                    System.out.println(sign + " " + axis + " " + coord);
                    coords[axis] = sign * coord;
                    // check if both coords are here
                    if (coords[1 - axis] != NONE) {
                        break;
                    }

                    // ready for next coordinate
                    coord = NONE;
                    axis = NONE;
                    sign = POS;

                } // else no axis given now but next token could be a cardinal letter
            }
        }

        if (sc.hasNextLine())
            label = sc.nextLine();

        sc.close();
        return true;
    }

    public static boolean parseDecimal() {

        return false;
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
        signed = false;
        cardinal = false;
        coord = NONE;
        int deg = NONE;
        int min = NONE;
        float sec = NONE;
        char[] dmsLetters = { 'd', 'm', 's' };
        char[] dmsSymbols = { dg, '\'', '\"' };
        axis = NONE;
        sign = POS;
        coords = new float[] { NONE, NONE };
        label = "";

        Matcher m;
        String matchSign = "";

        if (line.matches("[0-9]+d(.*)")) {
            matchSign = "([dms])";
        }
        if (line.matches("[0-9]+\u00B0(.*)")) {
            if (matchSign != "")
                return false;
            matchSign = "([\u00B0\'\"])";
        }

        m = Pattern.compile("([0-9]+)\\s*(" + matchSign + ")\\s?([NSEW]?)").matcher(t);

        while (m.find()) {
            float n = Float.parseFloat(m.group(1));
            char unit = m.group(2).charAt(0);

            t = m.group(2);
            if (!checkCardinal()) {
                return false;
            }

            switch (unit) {
                case dg:
                case 'd':
                    if (deg != NONE) {
                        coord = degToDecimal(deg, min, sec);
                    }
                    if ((int) n == n) {
                        deg = (int) n;
                        continue;
                    } else {
                        return false; // float given for degrees
                    }
                case '\'':
                case 'm':
                    if (deg == NONE)
                        return false; // recieved mins before degrees
                    if (min != NONE) {
                        coord = degToDecimal(deg, min, sec);
                    }
                    if ((int) n == n) {
                        min = (int) n;
                        continue;
                    } else {
                        return false; // float given for minutes
                    }

                case '\"':
                case 's':
                    if (min == NONE)
                        return false; // recieved secs before mins
                    if (sec != NONE) {
                        coord = degToDecimal(deg, min, sec);
                    }
                    sec = n;
            }

            if (coords[LAT] != NONE && coords[LON] != NONE) {
                // break from loop if we have a coordinate for both axes
                break;
            }

            if (startsWithNumber(t)) {
                if (isNum(t)) {
                    if (val == NONE) {
                        val = Float.parseFloat(t);
                        continue;
                    } else {
                        return false;
                    }
                }

                while (m.find()) {
                    String thing = m.group();
                    char unit = thing.charAt(thing.length() - 1);
                    thing = thing.substring(0, thing.length() - 2);

                }

                String[] units = new String[3];

                String[] vals = t.split("[dms\u00B0\"\']");
                if (vals.length > 3) {
                    return false; // too many numbers for dms format
                }
                if (vals.length == 1) {
                    String t;
                }
                if (t.matches("(.*)[dms\u00B0\"\'](.*)"))
                    ;

                // if another value is already staged but not saved
                if (coord != NONE) {
                    coords[LAT] = coord;
                    coords[LON] = Float.parseFloat(t);
                    break;
                }

                // otherwise stage the next value
                coord = sign * Float.parseFloat(t);

                if (cardinal) {
                    // if there was a cardinal letter adjacent to the number
                    if (axis != NONE) {
                        coords[axis] = sign * coord;

                        // ready for next coordinate
                        coord = NONE;
                        axis = NONE;
                        sign = POS;
                    } // else no axis given now but next token could be a cardinal letter
                }
            }
        }

        label = line.replaceAll("[+-]?" + "([0-9])*[d\u00B0]" +
                "(\s?([0-5]?[0-9]|60)[m\']" +
                "(\\s?([0-9][0-9]*)(.([0-9]*))?[s\"])?)?[NSEW]?,?", "");

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

    public static int cardToCoord(char c) {
        switch (c) {
            case 'S':
            case 'W':
                return -1;
            default:
                return 1;
        }
    }

    public static boolean validDegreesFormat(String c) {
        return c.matches("[+-]?" + "([0-9])*[d\u00B0]" + "(\s?([0-5]?[0-9]|60)[m\']" +
                "(\\s?([0-9][0-9]*)(.([0-9]*))?[s\"])?)?[NSEW]?,?(.)*");
    }

    public static boolean validDecimalFormat(String c) {
        String num = "[+-]?([0-9])*(.([0-9])+)?" + dg + "?(\\s?[NSEW])?";
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
        if (min != NONE)
            c += min / 60.0;
        if (sec != NONE)
            c += sec / 3600;
        return c;
    }
}