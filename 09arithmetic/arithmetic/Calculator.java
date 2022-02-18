package arithmetic;

public interface Calculator {

    public int solve(int[] nums, byte[] ops);

    public int solve(int[] nums, byte[] ops, int end);

    // public int max(int[] nums, byte[] ops, int start);

    public int min(int[] nums, byte[] ops, int start);

    // public int max(int[] nums);

    public int min(int[] nums);

    public String mode();
}
