import java.util.Scanner;

public class NicolasArithmetic {
    private static final byte NONE = 0;
    private static final byte ADD = 1;
    private static final byte MULTIPLY = 2;
    private static Calculator calc;
    private static int goal;
    private static int[] nums;
    private static byte[] ops;

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
                System.out.println(line + " Invalid");
            } catch (Exception e) {
                System.out.println(line + " " + e.getMessage());
            } finally {
                lineParser.close();
            }
        }
        sc.close();
    }

    public static void process(Scanner sc) throws Exception {
        // check if mode is valid
        String mode = sc.next();
        if (mode.equals("N")) {
            calc = new NormalCalc();
        } else if (mode.equals("L")) {
            calc = new Left2Right();
        } else {
            throw new Exception("Invalid");
        }

        // get goal and numbers to use
        goal = sc.nextInt();
        String[] numsIn = sc.nextLine().strip().split(" ");
        nums = new int[numsIn.length];
        for (int i = 0; i < numsIn.length; i++) {
            nums[i] = Integer.parseInt(numsIn[i]);
            if(nums[i] < 1) 
                throw new Exception("Invalid");
        }

        if (calc.min(nums) > goal) throw new Exception("impossible");

        // start a new search
        ops = new byte[nums.length - 1];
        if (search(0))
            System.out.println(calc.mode() + " " + 
            goal + " = " + formatSolution(nums, ops));
        else throw new Exception("impossible");
    }

    public static boolean search(int noneIndex) {
        // get current total
        int runningTotal = calc.solve(nums, ops, noneIndex);
        // check if we have a full ops list
        if (noneIndex == ops.length) return runningTotal == goal;
        // check if the goal is too small to be reached with current ops
        if (runningTotal > goal) return false;
        // add a multiply to the ops list and search from there
        ops[noneIndex] = MULTIPLY;
        if (search(noneIndex + 1)) return true;
        // add an addition to the ops list and search from there
        ops[noneIndex] = ADD;
        if (search(noneIndex + 1)) return true;
        // a solution wasn't found, return the ops list to how it was and return
        ops[noneIndex] = NONE;
        return false;
    }

    public static String formatSolution(int[] nums, byte[] ops) {
        String s = "" + nums[0];
        for (int i = 0; i < ops.length; i++) {
            s += (ops[i] == ADD) ? " + " : " * ";
            s += nums[i + 1];
        } 
        return s;
    }

    public static abstract class Calculator {
        public abstract int solve(int[] nums, byte[] ops, int end);
        public abstract String mode();

        public int min(int[] nums) {
            int n, r = 0;
            for (int i = 0; i < nums.length; i++) {
                n = nums[i];
                // ignore 1s unless no numbers tracked yet
                if(n == 1 && r < 1) r = n;
                else r += n;
            }
            return r;
        }
    }

    public static class NormalCalc extends Calculator {
        public int solve(int[] nums, byte[] ops, int end) {
            int r = 0, i = 0, n = nums[0];
            while (i < end) {
                if (ops[i] == ADD) {
                    r += n;
                    n = nums[++i];
                } else n *= nums[++i];
            }
            return r + n;
        }
    
        public String mode() { return "N"; }
    }

    public static class Left2Right extends Calculator {
        public int solve(int[] nums, byte[] ops, int end) {
            int i = 0, r = nums[0];
            while (i < end) {
                if (ops[i] == ADD) r += nums[++i];
                else r *= nums[++i];
            }
            return r;
        }
    
        public String mode() { return "L"; }
    }
}