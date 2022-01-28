package whereintheworld;

import java.lang.Character.Subset;
import java.util.*;
//import org.apache.commons.lang3.ArrayUtils;

public class NicoGeoInput2 {

    public static final String dg = "\u00B0";
    public static final int NONE = -1;
    public static final int DECIMAL = 0;
    public static final int DIRECTION = 1;
    public static final int DEGREE = 2;
    public static final int DEGREE_d = 3;
    public static final int MINUTE = 4;
    public static final int MINUTE_m = 5;
    public static final int SECOND = 6;
    public static final int SECOND_s = 7;
    public static final int FULLDEGREE = 8;
    public static final int SIGNED = 0;
    public static final int HASCARDINAL = 1;
    public static final int HASDEGREES = 2;
    public static final int HASMINUTES = 3;
    public static final int LAT = 0;
    public static final int LON = 1;

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

        int count = 0;
        while (scan.hasNextLine()) {
            parse(scan.nextLine(), count++);
        }

        scan.close();
    }

    public static boolean startsWithNumber(String c) {
        return c.matches("[+-]?[0-9](.*)");
    }

    public static boolean isCardinal(char c) {
        return Character.toString(c).matches("(.*)[NSEW](.*)");
    }

    public static boolean isSigned(char c) {
        return Character.toString(c).substring(0, 1).matches("[+-]");
    }

    /**
     * Checks if the token passed is a number and if it is signed
     * 
     * @param c coordinate to check format of
     * @return 1 if numbers are signed, 0 if they are not
     */
    public static boolean[] getFormat(String c) {
        boolean hasSign = c.substring(0, 1).matches("[+-]");
        boolean hasCardinality = c.matches("(.*)[NSEW](.*)");
        boolean hasDegrees = c.matches("(.*)[d\u00B0](.*)");
        boolean hasMinutes = c.matches("(.*)[m\'](.*)");

        return new boolean[] { hasSign, hasCardinality, hasDegrees, hasMinutes };
    }

    public static void parse(String line, int count) {
        if (line.charAt(0) == '#') {
            return; // found a comment
        }
        if (!startsWithNumber(line)) {
            return; // not in either format // broke rule 6
        }

        if (validDegreesFormat(line)) {
            tryParseDegree(line, count);
            return;
        } else if (validDecimalFormat(line)) {
            tryParseDecimal(line, count);
            return;
        } else {
            return; // unrecognized format // broke rule 6
        }
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

    public static String removeComma(String c) {
        return c.charAt(c.length()) == ',' ? c.substring(0, c.length() - 1) : c;
    }

    public static boolean validDegreesFormat(String c) {
        return c.matches("[+-]?([0-9]*)[d\u00B0](\s?([0-5]?[0-9]|60)[m\']" +
                "(\s?([0-9][0-9]*)(.([0-9]*))?[s\"])?)?[NSEW]?,?(.)*");
    }

    public static boolean validDecimalFormat(String c) {
        return c.matches("[+-]?([0-9]*)[\s?[NSEW]]?,?\s?" +
                "[+-]?([0-9]*)[\s?[NSEW]]?[\s(.)*]?");
    }

    public static int latOrLon(char c) {
        return c == 'N' || c == 'S' ? LAT : LON;
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
    public static void tryParseDecimal(String line, int count) {
        Scanner sc = new Scanner(line);

        // int i = 0;
        String t;
        boolean signed = false;
        boolean cardinal = false;

        int axis = NONE;
        float coord = NONE;

        float[] coords = { NONE, NONE };

        while (sc.hasNext()) {
            int sign = 1;

            t = sc.next();
            removeComma(t);

            // check if token starts with a + or - sign
            char s = t.charAt(0);
            if (isSigned(s)) {
                if (cardinal)
                    return; // used +/- as well as cardinal letters
                signed = true;

                if (s == '-') {
                    sign = -1;
                }
                t = t.substring(1); // remove sign so the number can be parsed
            }

            // check if number ends with a cardinal direction
            char c = t.charAt(t.length());
            if (isCardinal(c)) {
                if (signed)
                    return; // used +/- as well as cardinal letters
                cardinal = true;

                // if a direction is already staged
                if (axis != NONE) {
                    return; // two directions given before a value
                }

                // stage the direction
                sign = cardToCoord(c);
                axis = latOrLon(c);

                // checks if the staged axis already has a value
                if (coords[axis] != NONE) {
                    return; // given two values for the same axis
                }

                // if a value is ready to be assigned to an axis
                if (coord != NONE) {
                    // store the staged value and direction
                    coords[axis] = sign * coord;
                    // break from loop if we have a coordinate for both axes
                    if (coords[1 - axis] != NONE) {
                        break;
                    }

                    // clear stage for next coordinate and direction
                    coord = NONE;
                    axis = NONE;
                    sign = 1;
                }

                // remove the cardinal letter
                t = t.substring(0, t.length());
            }

            // if the imput matches the decimal format, it should be
            // stripped to just the number by this point
            if (isNum(t)) {
                // if another value is already staged but not saved
                if (coord != NONE) {
                    coords[LAT] = coord;
                    coords[LON] = Float.parseFloat(t);
                    // now we have both coords so exit the loop
                    break;
                }

                // otherwise stage the next value
                coord = sign * Float.parseFloat(t);

                if (cardinal) {
                    // if there was a cardinal letter adjacent to the number
                    if (axis != NONE) {
                        coords[axis] = sign * coord;
                        if (coords[1 - axis] != NONE) {
                            // break from loop if we have a coordinate for both axes
                            break;
                        }

                        // ready for next coordinate
                        coord = NONE;
                        axis = NONE;
                        sign = 1;
                    } // else no axis given now but next token could be a cardinal letter
                }
            }
        }

        store(coords, sc.nextLine());
        return;
    }

    public static int findLengthDegree(String[] t) {

        return 1;
    }

    public static String stripToNum(String coord) {
        return coord.replace("[^[.][0-9]]", "");
    }

    public static int[] parseSimple(String lat, String lon) {
        int lat_i = parseSimple(lat);
        int lon_i = parseSimple(lon);
        return new int[] { lat_i, lon_i };
    }

    public static int parseSimple(String c) {
        // storing coordinates at 10^6 times their value so they can be stored as ints
        int n = (int) (Float.parseFloat(stripToNum(c)) * 1000000);

        if (c.substring(c.length() - 1).matches("[SW]")) {
            n *= -1;
        } else if (c.charAt(0) == '-') {
            n *= -1;
        }
        return n;
    }

    public static boolean parseDegrees(String c) {
        return c.matches("[+-]?[0-9](.*)");
    }

    public static byte validDegreesLatitude(String c) {

        String[] dmsNums = stripDMS(c); // extract numbers

        if (dmsNums.length > 3) {
            return 4; // error code for too many numbers
        }

        try {
            int absLatDeg = Integer.parseInt(dmsNums[0]);
            int absLatMin = 0;
            int absLatSec = 0;
            if (dmsNums.length >= 2) {
                absLatMin = Integer.parseInt(dmsNums[1]);
                if (dmsNums.length == 3) {
                    absLatMin = Integer.parseInt(dmsNums[1]);
                }
            }
            if (absLat > 90) {
                return 3; // error code for latitude out of bounds
            }
        } catch (NumberFormatException e) {
            return 2; // error code for latitude isn't a number
        }
        for (int i = 0; i < dmsNums.length; i++) {

        }

        // try to parse absolute latitude

        return 0; // return 0 for valid latitude
    }

    public static String[] stripDMS(String c) {
        // this code reduces token to the absolute value of latitude
        if (c.substring(0, 1).matches("[+-]")) {
            if (c.substring(c.length() - 1).matches("[NS]")) {
                return 1; // error code for using signed coordinate and a cardinal letter
            }
            c = c.substring(1); // remove sign so number is absolute value
        } else {
            if (c.substring(c.length() - 1).matches("[NS]")) {
                c = c.substring(0, c.length()); // remove the cardinal letter so only positive number is left
            }
        }

        return c.split("[\u00B0\'\"]");
    }

    public static byte validDecimalLatitude(String c) {
        // this code reduces token to the absolute value of latitude
        if (c.substring(0, 1).matches("[+-]")) {
            if (c.substring(c.length() - 1).matches("[NS]")) {
                return 1; // error code for using signed coordinate and a cardinal letter
            }
            c = c.substring(1); // remove sign so number is absolute value
        } else {
            if (c.substring(c.length() - 1).matches("[NS]")) {
                c = c.substring(0, c.length()); // remove the cardinal letter so only positive number is left
            }
        }

        // try to parse absolute latitude
        try {
            int absLat = Integer.parseInt(c);
            if (absLat > 90) {
                return 3; // error code for latitude out of bounds
            }
        } catch (NumberFormatException e) {
            return 2; // error code for latitude isn't a number
        }

        return 0; // return 0 for valid latitude
    }

    public static byte validDecimalLongitude(String c) {
        // this code reduces token to the absolute value of longitude
        if (c.substring(0, 1).matches("[+-]")) {
            if (c.substring(c.length() - 1).matches("[EW]")) {
                return 1; // error code for using signed coordinate and a cardinal letter
            }
            c = c.substring(1); // remove sign so number is absolute value
        } else {
            if (c.substring(c.length() - 1).matches("[EW]")) {
                c = c.substring(0, c.length()); // remove the cardinal letter so only positive number is left
            }
        }

        // try to parse absolute longitude
        try {
            int absLat = Integer.parseInt(c);
            if (absLat > 180) {
                return 3; // error code for longitude out of bounds
            }
        } catch (NumberFormatException e) {
            return 2; // error code for longitude isn't a number
        }

        return 0; // return 0 for valid longitude
    }

    public static void reject(int l, String m) {
        System.err.println("rejected line " + l + ": " + m);
    }

    public static void store(int[] coords, String label) {
        System.out.print("[ " + Float.toString(coords[LAT]) +
                ", " + Float.toString(coords[LON]) + " ]" + label);
        return;
    }

}