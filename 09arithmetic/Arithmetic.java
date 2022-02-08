import java.util.Scanner;
import java.util.Stack;

public class Arithmetic {
    private static final boolean ADD = false;
    private static final boolean MULTIPLY = true;

    public static void main(String[] args) {
        int goal;
        String[] numsIn;
        int[] nums;
        Scanner sc = new Scanner(System.in);
        Scanner lineParser;
        String line;
        String mode;

        while (sc.hasNextLine()) {
            line = sc.nextLine();
            lineParser = new Scanner(sc.nextLine());

            try {
                mode = lineParser.next();
                if (!(mode == "N" || mode == "L")) {
                    throw new Exception("Invalid");
                }

                goal = lineParser.nextInt();

                numsIn = lineParser.nextLine().split("\s");
                nums = new int[numsIn.length];
                for (int i = 0; i < numsIn.length; i++) {
                    nums[i] = Integer.parseInt(numsIn[i]);
                }

                if (goal > max(nums) || goal < min(nums)) {
                    throw new Exception("impossible");
                }

                if (mode == "N") {
                    search(new Normal_Calc(), goal, nums);
                } else if (mode == "L") {
                    search(new Left2Right(), goal, nums);
                } else {
                    throw new Exception("Invalid");
                }

            } catch (NumberFormatException e) {
                System.out.println(line.strip() + " Invalid");
            } catch (Exception e) {
                System.out.println(line.strip() + " " + e.getMessage());
            }
            lineParser.close();
        }
        sc.close();
    }

    public static int max(int[] nums) {
        int r = 1;
        for (int n : nums) {
            r *= n;
        }
        return r;
    }

    public static int min(int[] nums) {
        int r = 0;
        for (int n : nums) {
            r += n;
        }
        return r;
    }

    public interface Calculator {
        public int solve(int[] nums, Boolean[] ops);
    }

    public static class Left2Right implements Calculator {
        public int solve(int[] nums, Boolean[] ops) {
            // solve from left to right
            int r = nums[0];
            for (int i = 1; i <= ops.length; i++) {
                if (ops[i - 1] == ADD) {
                    r += nums[i];
                } else {
                    r *= nums[i];
                }
            }
            return r;
        }
    }

    public static class Normal_Calc implements Calculator {
        public int solve(int[] nums, Boolean[] ops) {
            // multiply before adding
            int[] toAdd = new int[nums.length];
            int i = 0;
            int a = 0;
            int r = nums[0];
            while (i <= ops.length) {
                if (ops[i] == ADD) {
                    toAdd[a++] = r;
                    r = nums[++i];
                } else {
                    r *= nums[++i];
                }
            }
            toAdd[a++] = r;

            r = toAdd[0];
            for (i = 1; i < a; i++) {
                r += toAdd[i];
            }
            return r;
        }
    }

    public static void search(Calculator calc, int goal, int[] nums) {
        Stack<Boolean> ops = new Stack<Boolean>();
        search(calc, goal, nums, ops);
    }

    public static boolean search(Calculator calc, int goal, int[] nums, Stack<Boolean> ops) {
        int r = calc.solve(nums, (Boolean[]) ops.toArray());
        if (r == goal) {
            return true;
        } else if (r > goal) {

        }

        if (ops.size() < nums.length - 1) {
            ops.push(MULTIPLY);
            if (search(calc, goal, nums, ops)) {
                return true;
            }
            ops.pop();
            ops.push(ADD);
            if (search(calc, goal, nums, ops)) {
                return true;
            }
        }
        return false;
    }

    public static boolean[] add1(boolean[] binNum) {
        return addAtIndex(binNum, binNum.length);
    }

    public static boolean[] addAtIndex(boolean[] binNum, int i) {
        if (i < 0) {
            binNum = new boolean[binNum.length];
            return binNum;
        }
        if (binNum[i]) {
            binNum[i] = false;
            return addAtIndex(binNum, --i);
        } else {
            binNum[i] = true;
            return binNum;
        }
    }
}