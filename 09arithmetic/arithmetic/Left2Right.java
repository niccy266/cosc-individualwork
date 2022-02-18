package arithmetic;

import java.util.Arrays;

public class Left2Right implements Calculator {

    private static final byte NONE = 0;
    private static final byte ADD = 1;
    private static final byte MULTIPLY = 2;


    public int solve(int[] nums, byte[] ops) {
        return solve(nums, ops, ops.length);
    }

    public int solve(int[] nums, byte[] ops, int end) {
        // solve from left to right
        int i = 0;
        int r = nums[0];
        while (i < end) {
            if (ops[i] == ADD) {
                r += nums[++i];
            } else {
                r *= nums[++i];
            }
        }
        return r;
    }

    /* public int max(int[] nums) {
        return max(nums, 0);
    }

    public int max(int[] nums, byte[] ops, int start) {
        return solve(nums, ops, start) + max(nums, start);
    } */

    // too complicated
    /* public int max(int[] nums, int start) {
        int n;
        int l = 0;
        for (int i = start; i < nums.length; i++) {
            n = nums[i];
            if(n != 1) {
                l = (l < 1) ? n : l * n;
            } else {
                if(i == nums.length) 
                    return n;
                if(i < nums.length - 2) {
                    int r = max(nums, i + 2);
                    int r1 = r * (nums[i + 1] + 1) * l;
                    int r1 = r * (nums[i + 1]) * (l + 1);
                }
                int r = max(nums, i + 2);
                l = (l > r) ? (r * (nums[i + 1] + 1) * l : ((l != 0) ? l : 1) * r;
                System.out.println("from " + i + " to the end the max was " + l);
                return l;
            }
            System.out.println("running total: " + l);
        }
        System.out.println("max of " + Arrays.toString(nums) + " is: " + l);
        return l;
    } */

    public int min(int[] nums) {
        return min(nums, 0);
    }

    public int min(int[] nums, int start) {
        int n;
        int r = 0;
        for (int i = start; i < nums.length; i++) {
            n = nums[i];
            if(n != 1) { // ignore 1s
                r += n;
            } else if(r < 1) { // unless no numbers tracked yet
                r = n;
            }
        }
        // System.out.println("min of " + Arrays.toString(nums) + " is: " + r);
        return r;
    }

    public int min(int[] nums, byte[] ops, int start) {
        int n = nums[start];
        nums[start] = solve(nums, ops, start);
        int r = min(nums, start);
        nums[start] = n;
        // System.out.println("min of " + Arrays.toString(nums) + " " + Arrays.toString(ops) + " is: " + r);
        return r;
    }

    public String mode() {
        return "L";
    }
}