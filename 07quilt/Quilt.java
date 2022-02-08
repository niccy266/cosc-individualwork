import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.Scanner;
import java.util.Queue;
import java.util.LinkedList;

public class Quilt extends JPanel {

    private static int[][] squares;

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Queue<int[]> q = new LinkedList<int[]>();

        int x = getWidth() / 2, y = getHeight() / 2;
        int[] s = { 0, x, y };
        q.add(s);
        int[] item;
        int r;

        while (!q.isEmpty()) {
            item = q.remove();
            x = item[1];
            y = item[2];

            s = squares[item[0]];

            r = s[0] / 2;
            g.setColor(new Color(s[1], s[2], s[3]));
            g.fillRect(x - r, y - r, r * 2, r * 2);

            // don't draw corners if end is reached
            if (++item[0] >= squares.length) {
                continue;
            } else {
                // draw squares on the corners
                q.add(new int[] { item[0], x - r, y - r });
                q.add(new int[] { item[0], x + r, y - r });
                q.add(new int[] { item[0], x - r, y + r });
                q.add(new int[] { item[0], x + r, y + r });
            }
        }
    }

    public static void main(String[] args) {
        String line;
        String[] lines = new String[3];
        Scanner sc = new Scanner(System.in);
        Scanner lineParser;
        int i = 0;
        int l = 0;

        // cleaning data
        while (sc.hasNextLine()) {
            // System.out.println("cleaning input");
            line = sc.nextLine();
            lineParser = new Scanner(line);
            try {
                lineParser.nextFloat();
                for (int k = 1; k < 3; k++) {
                    int c = lineParser.nextInt();
                    if (c < 0 || c > 255) {
                        throw new NumberFormatException();
                    }
                }
                if (i >= lines.length) {
                    lines = doubleArraySize(lines);
                }
                lines[i++] = line;
                l++;
            } catch (Exception e) {
                System.err.println("bad input on line " + l++ + e.getMessage());
            }
            lineParser.close();
        }
        sc.close();

        // System.out.println("parsing input");
        float totalScale = 0;
        // parsing clean data
        squares = new int[i][4]; // i is the index of the last square + 1
        float[] scales = new float[i];
        for (i = 0; i < squares.length; i++) {

            // System.out.println(lines[i]);
            lineParser = new Scanner(lines[i]);

            scales[i] = lineParser.nextFloat();
            totalScale += scales[i];

            for (int k = 1; k < 4; k++) {
                squares[i][k] = lineParser.nextInt();
            }

            // System.out.println(Arrays.toString(squares[i]));
        }

        // make window a resonable size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int w = screenSize.width, h = screenSize.height;
        int size = (w < h) ? w * 2 / 3 : h * 2 / 3;

        // System.out.println("scaling squares");
        float scale = size / totalScale;
        for (i = 0; i < squares.length; i++) {
            squares[i][0] = (int) (scales[i] * scale);
        }

        SwingUtilities.invokeLater(() -> {
            var panel = new Quilt();
            var frame = new JFrame("A simple graphics program");
            frame.setSize(size + 30, size + 50); // idk why but these numbers work
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(panel, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }

    public static String[] doubleArraySize(String[] a) {
        String[] b = new String[a.length * 2];
        for (int i = 0; i < a.length; i++) {
            b[i] = a[i];
        }
        return b;
    }
}