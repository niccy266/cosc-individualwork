import java.util.*;

public class NicolasArithmeticShort {
    private static final int NONE = 0, ADD = 1, MULTIPLY = 2;
    private static char calc;
    private static int goal;
    private static int[] nums, ops;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String line;
        
        while (sc.hasNextLine()) {
            line = sc.nextLine().strip();
            Scanner lineParser = new Scanner(line);
            try {
                process(lineParser);
            } catch (NumberFormatException e) {
                System.out.println(line + " Invalid");
            } catch (NoSuchElementException e) {
                System.out.println(line + " Invalid");
            } catch (Exception e) {
                System.out.println(line + " " + e.getMessage());
            } 
            lineParser.close();
        } 
        sc.close();
    }

    public static void process(Scanner sc) throws Exception {
        // check if mode is valid
        String mode = sc.next();
        if (mode.equals("N") || mode.equals("L") ) calc = mode.charAt(0);
        else throw new Exception("Invalid");
        // get goal and numbers to use
        goal = sc.nextInt();
        String[] numsIn = sc.nextLine().strip().split(" ");
        nums = new int[numsIn.length];
        for (int i = 0; i < numsIn.length; i++) {
            nums[i] = Integer.parseInt(numsIn[i]);
            if(nums[i] < 1) 
                throw new Exception("Invalid");
        }

        // check if goal is too small to reach
        if (min() > goal) throw new Exception("impossible");
        // start a new search
        ops = new int[nums.length - 1];
        if (search(0)) {
            String out = calc + " " + goal + " = " + nums[0];
            for (int i = 0; i < ops.length; i++) {
                out += ((ops[i] == ADD) ? " + " : " * ") + nums[i + 1];
            }
            System.out.println(out);
        }
        else throw new Exception("impossible");
    }

    public static boolean search(int noneIndex) {
        int result;
        if(calc == 'N') { // solve using * before +
            int r = 0, i = 0, n = nums[0];
            while (i < noneIndex) {
                if (ops[i] == ADD) {
                    r += n;
                    n = nums[++i];
                } else n *= nums[++i];
            }
            result = r + n;
        } else { // solve left to right
            int i = 0, r = nums[0];
            while (i < noneIndex) {
                if (ops[i] == ADD) r += nums[++i];
                else r *= nums[++i];
            } 
            result = r;
        }
        // if we have a full ops list, did we find a solution?
        if(noneIndex == ops.length) return result == goal;
        // check if goal is still reachable
        if(result > goal) return false;
        // try searching with new operations at the end
        ops[noneIndex] = MULTIPLY;
        if (search(noneIndex + 1)) return true;
        ops[noneIndex] = ADD;
        if (search(noneIndex + 1)) return true;
        ops[noneIndex] = NONE; // no solution found, restore ops list
        return false;
    }

    public static int min() {
        int n, r = 0;
        for (int i = 0; i < nums.length; i++) {
            n = nums[i];
            if(n != 1) r = (r == 1) ? n : r + n; // add number unless one is 1
            else if(r == 0) r = n; // ignore 1s unless no numbers tracked
        }
        return r;
    }
}