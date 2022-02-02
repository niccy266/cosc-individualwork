package whereintheworld;

import java.util.*;
//import org.apache.commons.lang3.ArrayUtils;

public class NicoGeoInput {

    public static final String dg = "\u00B0";
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

    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);
        String place;
        String[] tokens;
        int[] coords;
        String label;

        int count = 0;
        while (scan.hasNextLine()) {
            count++;
            place = scan.nextLine();
            tokens = place.split("[, ]");

            if (tokens.length < 2) {
                reject(count, "Not enough arguments.");
            } else {
                coords = parse(tokens, count);
                if (coords[1] == 999) {
                    continue;
                } else {
                    label = place.substring(coords[3]);
                    store(coords[0], coords[1], label);
                }
            }
        }

        scan.close();
    }

    public static boolean startsWithNumber(String c) {
        return c.matches("[+-]?[0-9](.*)");
    }

    public static void store(int lat, int lon, String label) {
        return;
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

    public static int[] parse(String[] t, int count) {
        // check if first string contains a number as all formats require a number first
        if (!startsWithNumber(t[0])) {
            reject(count, "First token isn't a number.");
            return new int[] { 999 };
        }

        // int[] coordFormat = new int[14];
        int[] coord1 = parseNext(t, count, 0, "");
        int[] coord2 = parseNext(t, count, 0, "");

        return coord1;
    }

    public static int[] parseNext(String[] t, int count, int i, String coord) {
        // get formatting in first token
        boolean[] format1 = getFormat(t[i++]);

        // check if formatting breaks a rule
        if (format1[SIGNED] && format1[HASCARDINAL]) {
            reject(count, "Line contains number with sign and cardinal letter.");
            return new int[] { 999 };
        }
        // check if second token has a number
        if (startsWithNumber(t[2])) {

            boolean[] format2 = getFormat(t[i]);

            if (format2[SIGNED] && format2[HASCARDINAL]) {
                reject(count, "Line contains number with sign and cardinal letter.");
                return new int[] { 999 };
            }

            // check is we are dealing with DMS format
            if (format1[HASDEGREES]) {
                // checks if second token is second number of DMS format
                if (!format2[HASDEGREES] && format2[HASMINUTES] && !format1[HASMINUTES]) {
                    int coord1Len = findLengthDegree(t);
                    int coord2Len = findLengthDegree(t, coord1Len);
                    String lat = rejoin(t, coord1Len);
                    String lon = rejoin(t, coord1Len, coord2Len);
                    int[] coords = parseDegrees(lat, lon);
                    return new int[] { coords[0], coords[1], coord1Len + coord2Len };
                // else second token is the start of the second coord
                } else if (format2[HASDEGREES]) {
                    int coord1Len = 1;
                    int coord2Len = findLengthDegree(t, 1);
                    String lat = rejoin(t, coord1Len);
                    String lon = rejoin(t, coord1Len, coord2Len);
                    int[] coords = parseDegrees(lat, lon);
                    return new int[] { coords[0], coords[1], coord1Len + coord2Len };
                } else {
                    reject(count, "Failed while parsing as DMS, improper format.");
                    return new int[] { 999 };
                }
            } else if (format2[HASDEGREES]) {
                reject(count, "Mixed formats.");
                return new int[] { 999 };
            }

            // if we have gotten two numbers that are not in dms format, we can parse
            if (!format1[HASCARDINAL] == !format2[HASCARDINAL]) {
                if (format1[HASCARDINAL]) {
                    t[i]
                    int[] coords = parseSimple(t[0], t[1]);
                    if(checkValid(coords)) {
                        return new int[] { coords[0], coords[1], 2 };
                    } else {
                        reject(count, "Value out of bounds.");
                        return new int[] { 999 };
                    }
                }
                int[] coords = parseSimple(t[0], t[1]);
                if(checkValid(coords)) {
                    return new int[] { coords[0], coords[1], 2 };
                } else {
                    reject(count, "Value out of bounds.");
                    return new int[] { 999 };
                }
            } else {
                reject(count, "Cardinal letter used in one coordinate, but not the other.");
                return new int[] { 999 };
            }

            byte compatibility = checkCompat(format1, format2);
            switch (compatibility) {
                case (0):
                    break;
                case (1):
                    break;
                case (2):
                    break;
                case (3):
                    break;
                case (4):
                    break;
                case (5):
                    break;
                case (6):
                    break;
                case (7):
                    break;
            }

        // check if second token is a direction
        } else if (t[1].matches("[NSEW]")) {
            //coordFormat[2] = DIRECTION;
            if (format1[0]) {
                reject(count, "Line contains number with sign and cardinal letter.");
                return new int[] { 999 };
            } else {
                parseNext(t, count, i, coord + t[i]);
            }
        // check if it is a label for dms format
        } else if (t[1].matches("d")) {
            int coord1Len = findLengthDegree(t);
            int coord2Len = findLengthDegree(t, coord1Len);

        } else {
            reject(count, "Encountered unrecognized token after first coordinate '" + t[1] + "'.");
            return new int[] { 999 };
        }
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

    public static byte checkCompat(boolean[] f1, boolean[] f2) {
        boolean 

        if(f1[0]) { // if first token is signed, second token must not have cardinality
            if(!f2[0]) {
                if(f2[1]) {
                    return 0; // return code for line contains numbers with signs and cardinal letters
                }
                //otherwise these are definitely two coords because they both have signs
            } else {

            }

        }

        if(f1[3] && f1[3]) { // if first token uses degrees, check if second number is also degrees
            if(f2[4]) {
                return 1; // code for unique compatible degree coords
            }
        }

        if(f1[3] && !f1[4]) { // if first token uses degrees, check if second number is the minutes
            if(f2[4]) {

            }
        }
        return 0;
    }

    public static boolean signed(String c) {
        return c.substring(0, 1).matches("[+-]");
    }

    public static boolean validDegrees(String c) {
        return c.matches("([0-9]*)\u00B0(\s?([0-9]*)\'(\s?([0-9]*)(.([0-9]*))?)?)?[NSEW]?,?");
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
}

/*
 * try {
 * System.out.println();
 * } catch (EmptyStackException e) {
 * System.out.println("Error: too few operands");
 * } catch (Exception e) {
 * System.out.println(e.getMessage());
 * }
 */