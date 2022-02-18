package arithmetic;

import java.util.Arrays;

public class NormalCalc implements Calculator {
    private static final byte NONE = 0;
    private static final byte ADD = 1;
    private static final byte MULTIPLY = 2;

    public int solve(int[] nums, byte[] ops) {
        return solve(nums, ops, ops.length);
    }

    public int solve(int[] nums, byte[] ops, int end) {
        int r = 0;
        int i = 0;
        int n = nums[0];
        while (i < end) {
            if (ops[i] == ADD) {
                r += n;
                n = nums[++i];
            } else {
                n *= nums[++i];
            }
            //System.out.println("running total: " + n);
        }
        //if(ops[ops.length - 1] == ADD) r += n;
        r += n;
        //System.out.println("solve: " + Arrays.toString(nums) + " end: " + end + " " + Arrays.toString(ops) + " " + r);
        return r;
    }

    // working but i couldn't get l2r to work
/*     public int max(int[] nums) {
        return max(nums, 0);
    }

    public int max(int[] nums, byte[] ops, int start) {
        return solve(nums, ops, start) + max(nums, start);
    }

    public int max(int[] nums, int start) {
        int n;
        int r = 0;
        int l = 0;
        for (int i = start; i < nums.length; i++) {
            n = nums[i];
            if(n != 1) {
                if(r < 1) r = 1;
                r *= n;
            } else {
                l += r + n;
                if(++i < nums.length)
                    r = nums[i];
            }
        }
        l += r;
        // System.out.println("max of " + Arrays.toString(nums) + " is: " + (l));
        return l;
    } */

    public int min(int[] nums) {
        return min(nums, 0);
    }

    public int min(int[] nums, int start) {
        int n, r = 0;
        for (int i = 0; i < nums.length; i++) {
            n = nums[i];
            if(n != 1) r = (r == 1) ? n : r + n;
            else if(r == 0) r = n; // ignore 1s unless no numbers tracked
        }
        //System.out.println("min of " + Arrays.toString(nums) + " is: " + r);
        return r;
    }

    public int min(int[] nums, byte[] ops, int start) {
        int r = solve(nums, ops, start) + min(nums, start);
        System.out.println("min of " + Arrays.toString(nums) + " " + Arrays.toString(ops) + " is: " + r);
        return r;
    }

    public String mode() {
        return "N";
    }
}