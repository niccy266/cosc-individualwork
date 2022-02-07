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
        q.add(new int[] { 0, x, y });
        int[] s;

        while (!q.isEmpty()) {
            s = q.remove();
            x = s[1];
            y = s[2];

            int r = s[0] / 2;
            g.setColor(new Color(s[1], s[2], s[3]));
            g.fillRect(x - r, y - r, r * 2, r * 2);

            // don't draw corners if end is reached
            if (s[0]++ >= squares.length) {
                continue;
            }
            // draw squares on the corners
            q.add(new int[] { s[0], x - r, y - r });
            q.add(new int[] { s[0], x + r, y - r });
            q.add(new int[] { s[0], x - r, y + r });
            q.add(new int[] { s[0], x + r, y + r });
        }
    }

    // void recurseSquares(Graphics g, Point center, int i) {
    // if (i >= squares.length) {
    // return;
    // }

    // int[] s = squares[i++];
    // int radius = s[0] / 2;
    // g.setColor(new Color(s[1], s[2], s[3]));
    // g.fillRect(center.x - radius, center.y - radius, radius * 2, radius * 2);

    // recurseSquares(g, center.translate(radius, radius)), i);
    // }

    public static void main(String[] args) {
        String line;
        String[] lines = new String[3];
        Scanner sc = new Scanner(System.in);
        Scanner lineParser;
        int i = 0;
        int l = 0;

        // cleaning data
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            lineParser = new Scanner(line);
            try {
                lineParser.nextInt();
                for (int k = 0; k < 3; k++) {
                    int c = lineParser.nextInt();
                    if (c < 0 || c > 255) {
                        throw new NumberFormatException();
                    }
                }
                if (i >= lines.length) {
                    doubleArraySize(lines);
                }
                lines[i++] = line;
                l++;
            } catch (NumberFormatException e) {
                System.err.println("bad input on line " + l++);
            }
            lineParser.close();
        }
        sc.close();

        int totalScale = 0;
        // parsing clean data
        squares = new int[i][4]; // i is the index of the last square
        for (i = 0; i < squares.length; i++) {
            lineParser = new Scanner(lines[i]);
            for (int k = 0; k < 3; k++) {
                squares[i][k] = lineParser.nextInt();
            }
            totalScale += squares[i][0];
        }

        // make window a resonable size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int w = screenSize.width / 2, h = screenSize.height / 2;
        int size = (w > h) ? w : h;

        int scale = size / totalScale;
        for (int[] square : squares) {
            square[0] *= scale;
        }

        SwingUtilities.invokeLater(() -> {
            var panel = new Quilt();
            panel.setBackground(Color.GREEN.darker());
            var frame = new JFrame("A simple graphics program");
            frame.setSize(size, size);
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