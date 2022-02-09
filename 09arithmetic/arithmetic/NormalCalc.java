package arithmetic;

public static class NormalCalc implements Calculator {
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
        // multiply before adding
        int[] toAdd = new int[nums.length];
        int i = start;
        int a = 0;
        int r = nums[start];
        while (i <= end) {
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

    public int max(int[] goal) {
        return 1;
    }

    public int min(int[] nums) {
        int r = 0;
        for (int n : nums)
            r += n;
        return r;
    }

    public String mode() {
        return "N";
    }
}