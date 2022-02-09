package arithmetic;

public static class Left2Right implements Calculator {
    public int solve(int[] nums, byte[] ops) {
        return solve(0, nums, ops, ops.length);
    }

    // public int solve(int start, int[] nums, byte[] ops) {
    // return solve(start, nums, ops, ops.length);
    // }

    // public int solve(int[] nums, byte[] ops, int end) {
    // return solve(nums, ops, end);
    // }

    public int solve(int start, int[] nums, byte[] ops, int end) {
        // solve from left to right
        int r = nums[start];
        for (int i = start + 1; i <= end; i++) {
            if (ops[i - 1] == ADD) {
                r += nums[i];
            } else {
                r *= nums[i];
            }
        }
        return r;
    }

    public int min(int[] nums) {
        byte[] ops = new byte[nums.length - 1];
        return min(nums, ops, 0);
    }

    public int min(int[] nums, byte[] ops, int start) {
        // optimize the remaining operations
        for (int i = start; i < ops.length; i++)
            ops[i] = (nums[i] == 1) ? MULTIPLY : ADD;
        int min = solve(nums, ops);
        // restore the ops list
        for (int i = start; i < ops.length; i++)
            ops[i] = NONE;
        return min;
    }

    public int max(int[] nums) {
        byte[] ops = new byte[nums.length - 1];
        return max(nums, ops, 0);
    }

    public int max(int[] nums, byte[] ops, int start) {
        // optimize the remaining operations
        for (int i = start; i < ops.length; i++)
            ops[i] = (nums[i] == 1) ? ADD : MULTIPLY;
        int min = solve(nums, ops);
        // restore the ops list
        for (int i = start; i < ops.length; i++)
            ops[i] = NONE;
        return min;
    }

    public String mode() {
        return "L";
    }
}