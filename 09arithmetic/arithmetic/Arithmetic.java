package arithmetic;

import java.text.spi.CollatorProvider;
import java.util.Arrays;
import java.util.Scanner;

public class Arithmetic {
    private static final byte NONE = 0;
    private static final byte ADD = 1;
    private static final byte MULTIPLY = 2;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Scanner lineParser;
        String line;

        while (sc.hasNextLine()) {
            line = sc.nextLine().strip();
            lineParser = new Scanner(line);

            try {
                process(lineParser);
            } catch (NumberFormatException e) {
                System.out.println(line + " Invalid" + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println(line + " " + e.getMessage());
                e.printStackTrace();
            } finally {
                lineParser.close();
            }
        }
        sc.close();
    }

    public static void process(Scanner sc) throws Exception {
        Calculator calc;

        // check if mode is valid
        String mode = sc.next();
        if (mode.equals("N")) {
            calc = new NormalCalc();
        } else if (mode.equals("L")) {
            calc = new Left2Right();
        } else {
            throw new Exception("Invalid");
        }

        // get target
        int goal = sc.nextInt();

        // get numbers to use
        String[] numsIn = sc.nextLine().strip().split(" ");
        int[] nums = new int[numsIn.length];
        for (int i = 0; i < numsIn.length; i++) {
            nums[i] = Integer.parseInt(numsIn[i]);
        }

        search(calc, goal, nums);
    }

    public static void search(Calculator calc, int goal, int[] nums) throws Exception {
        // firs check if a solution is possible
        if (calc.max(nums) < goal || calc.min(nums) > goal) {
            throw new Exception("impossible");
        }

        // start a search
        byte[] ops = new byte[nums.length - 1];
        if (search(calc, goal, nums, ops)) {
            System.out.println(calc.mode() + goal + formatSolution(nums, ops));
        } else {
            throw new Exception("impossible");
        }
    }

    public static int indexOf(byte[] a, byte n) {
        int index = 0; // index of the first NONE value
        for (int i = 0; i < a.length; i++) {
            if (a[i] == n) {
                break;
            } else {
                index++;
            }
        }
        return (index == a.length) ? -1 : index; // return -1 if item not found
    }

    public static boolean search(Calculator calc, int goal, int[] nums, byte[] ops) {
        // ops list might not be full, so we can't calculate a value immediately
        int noneIndex = indexOf(ops, NONE);

        // check if we have a full ops list
        if (noneIndex == -1) {
            // is this a solution?
            int result = calc.solve(nums, ops);
            return result == goal;
        }

        // keep searching down

        // check if the goal can be reached if the remaining operationgs were all *

        int max = calc.solve(nums, maxOps);
        if (max < goal)
            return false;

        // check if the goal is too small to be reached with remaining ops as +
        int min = calc.solve(nums, minOps);
        if (min > goal)
            return false;

        // add a multiply to the ops list and search from there
        ops[noneIndex] = MULTIPLY;
        if (search(calc, goal, nums, ops)) {
            return true;
        }

        // add an addition to the ops list and search from there
        ops[noneIndex] = ADD;
        if (search(calc, goal, nums, ops)) {
            return true;
        }

        // a solution wasn't found, return the ops list to how it was and return
        ops[noneIndex] = NONE;
        return false;
    }

    public static String formatSolution(int[] nums, byte[] ops) {
        String s = "";
        s += nums[0];
        for (int i = 0; i < nums.length; i++)
            s += (ops[i] == ADD) ? "+" : "*" + nums[i];
        return s;
    }
}