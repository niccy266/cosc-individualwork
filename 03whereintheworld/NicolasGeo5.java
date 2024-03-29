import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileWriter;
import java.io.IOException;

public class NicolasGeo5 {

    public static final char dg = '\u00B0';
    public static final int NONE = -1;
    public static final int LAT = 0;
    public static final int LON = 1;
    public static final int POS = 1;
    public static final int NEG = -1;
    public static float coord;
    public static boolean signed;
    public static boolean cardinal;
    public static int axis;
    public static int sign;
    public static float[] coords;
    public static String label;
    public static StringBuilder out; // output to write to file

    /*
     * Rules:
     * 1. Cannot use +/- to represent deirection on an axis
     * while also using cardinal letters
     * 2. If cardinal letters aren't used, latitude must come before longitude
     * 3. Label must be separated from coordinates by whitespace
     * 4. If dms (degrees, minutes, seconds) format is used, they must use
     * symbols with no whitespace before symbols: \u00B0 (degree symbol) ' "
     * or lowercase letters: d m s
     * 5. The two coordinates should be in the same format
     * 6. The coordinates must be in decimal or dms format
     * 7. The only cardinal letters allowed are N, S, W & E.
     * 6. If the two coordinates are on the same axis they are not valid.
     */
    public static void main(String[] args) {
        // start file
        out = new StringBuilder(
                "{\n" +
                        "\t\"type\": \"FeatureCollection\",\n" +
                        "\t\"features\": [");

        // read data in
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

        // close off the file properly
        int lastComma = out.lastIndexOf(","); // check if anything was stored
        if (lastComma != 30)
            out.delete(lastComma, lastComma + 2);
        out.append("]\n}");

        // write to file
        FileWriter geo;
        try {
            geo = new FileWriter("Geo.JSON", false);
            geo.write(out.toString());
            geo.close();
        } catch (IOException e) {
            System.out.println("couldn't write to file");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static boolean parse(String line) {
        if (line.charAt(0) == '#')
            return false; // found a comment
        if (!startsWithNumber(line))
            return false; // not in either format

        if (validDegreesFormat(line))
            return tryParseDegree(line);
        else if (validDecimalFormat(line))
            return tryParseDecimal(line);
        else
            return false; // unrecognized format
    }

    /**
     * Tries to parse a line of input as coordinates in decimal format.
     * Tries to match input with decimal format coordinates, eg, 80, 170.
     * Returns false if the the requirements for the format ever aren't met.
     * 
     * @param line the line of input to parse in decimal format
     * @return true if the line was parsed successfully, otherwise false
     */
    public static boolean tryParseDecimal(String line) {
        signed = false;
        cardinal = false;
        coord = NONE;
        axis = NONE;
        sign = POS;
        coords = new float[] { NONE, NONE };
        label = "";

        String lonReg = "([0-9]+(\\.[0-9]+)?)[" + dg + "d]?";
        String pattern = "([+-])?" + lonReg + "(\\s?([NSWE]))?";
        pattern = pattern + "[,\\s]\\s*" + pattern + "(,?\\s+(.+))?$";
        Matcher m = Pattern.compile(pattern).matcher(line);

        if (m.find()) {
            if (parseDecimal(m, 0) && parseDecimal(m, 5)) {
                label = m.group(12);
                return true;
            }

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
        // checking if a cardinal letter was given
        String card = m.group(i + 5);
        if (card != null) {
            cardinal = true;
            char c = card.charAt(0);
            axis = latOrLon(c);
            sign = cardToSign(c);
        } else {
            if (cardinal) {
                return false; // missing a cardinal letter
            }
        }

        if (cardinal && signed) {
            return false; // used both signs and cardinal directions
        }
        if (cardinal && axis == NONE) {
            return false; // format uses cardinal letters but this one is missing
        }
        coord = Float.parseFloat(m.group(i + 2));
        if (!cardinal) {
            if (coords[LAT] == NONE) {
                axis = LAT;
            } else {
                axis = LON;
            }
        }

        // fail if two values given for same axis
        if (coords[axis] != NONE)
            return false;
        // make sure data is in bounds
        if (axis == LAT && coord > 90)
            return false;
        if (axis == LON && coord > 180)
            return false;

        coords[axis] = sign * coord;
        axis = NONE;
        sign = POS;
        return true;
    }

    /**
     * Tries to parse a line of input as coordinates in degree format.
     * 
     * @param line the line of input to parse in decimal format
     */
    public static boolean tryParseDegree(String line) {
        coord = NONE;
        axis = NONE;
        sign = POS;
        coords = new float[] { NONE, NONE };
        label = "";
        String[] matchSign = new String[3];

        if (line.matches("(.*)[0-9]+\\s?d(.*)")) {
            matchSign = new String[] { "\\s?d", "\\s?m", "\\s?s" };
        }
        if (line.matches(".*[0-9]+" + dg + ".*")) {
            if (matchSign[0] != null) {
                return false; // can't use both dms and dg " ' symbols
            }
            matchSign = new String[] { "" + dg, "'", "\"" };
        }

        String minReg = "([0-5]?[0-9]|60)";
        String secReg = "(([0-5]?[0-9](\\.[0-9]+)?)|60|60.0+)";
        String degReg = "([0-9]+)" + matchSign[0] + "\\s?";
        minReg += matchSign[1] + "\\s?";
        secReg += matchSign[2] + "\\s?";

        String pattern = degReg + "(" + minReg + "(" + secReg + ")?)?" + "(\\s?([NSWE]))?";
        pattern = pattern + "[,\\s]\\s*" + pattern + "(,?\\s+(.+))?$";
        // pattern = "(" + pattern + "|" + "([0-9]+(\\.([0-9]+))?)" + ")";
        Matcher m = Pattern.compile(pattern).matcher(line);

        if (m.find()) {
            // for (int i = 0; i <= m.groupCount(); i++) {
            // System.out.println(i + " " + m.group(i));
            // }

            if (parseDegree(m, 0) && parseDegree(m, 9)) {
                label = m.group(20);
                return true;
            }
        }
        // System.out.println("regex failed");
        return false;
    }

    public static boolean parseDegree(Matcher m, int i) {
        // checking if a cardinal letter was given
        String card = m.group(i + 9);
        if (card != null) {
            cardinal = true;
            char c = card.charAt(0);
            axis = latOrLon(c);
            sign = cardToSign(c);
        } else {
            if (cardinal) {
                return false; // missing a cardinal letter
            }
        }

        if (coords[axis] != NONE)
            return false; // two values given for same axis

        String deg_s = m.group(i + 1);
        String min_s = m.group(i + 3);
        String sec_s = m.group(i + 5);
        int deg = 0;
        int min = 0;
        float sec = 0;

        deg = Integer.parseInt(deg_s);
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

        coord = degToDecimal(deg, min, sec);
        if (axis == LAT && coord > 90)
            return false;
        if (axis == LON && coord > 180)
            return false;

        coords[axis] = coord * sign;
        return true;
    }

    public static boolean startsWithNumber(String c) {
        return c.matches("[+-]?[0-9](.*)");
    }

    public static boolean validDegreesFormat(String c) {
        String pattern = "([0-9]+)((\\s?d)|\u00B0)" + "\\s?(([0-5]?[0-9]|60)(\\sm|\')" +
                "(\\s?([0-9][0-9]*)(\\.([0-9]*))?(\\ss|\"))?)?[NSEW]?,?(.)*";
        return c.matches(pattern);
    }

    public static boolean validDecimalFormat(String c) {
        String num = "[+-]?[0-9]+(\\.([0-9])+)?" + dg + "?(\\s?[NSEW])?";
        String sep = ",?\\s?";
        String end = "(\\s(.)*)?";
        return c.matches(num + sep + num + end);
    }

    public static int cardToSign(char c) {
        return c == 'S' || c == 'W' ? -1 : 1;
    }

    public static int latOrLon(char c) {
        return c == 'N' || c == 'S' ? LAT : LON;
    }

    public static float degToDecimal(int deg, int min, float sec) {
        float c = 0;
        c += deg;
        c += min / 60.0;
        c += sec / 3600;
        return c;
    }

    public static void store(float[] coords, String label) {
        System.out.print(String.format("%.6f", coords[LAT]) + ", ");
        System.out.println(String.format("%.6f", coords[LAT]));
        out.append("{\n" +
                "\t\t\"type\": \"Feature\",\n" +
                "\t\t\"geometry\": {\n" +
                "\t\t\t\"type\": \"Point\",\n" +
                "\t\t\t\"coordinates\": " + Arrays.toString(coords) + "\n" +
                "\t\t},\n" +
                "\t\t\"properties\": {");
        if (label != null)
            out.append("\n\t\t\t\"label\": \"" + label.strip() + "\"\n\t\t");
        out.append("}\n\t}, ");
        return;
    }
}