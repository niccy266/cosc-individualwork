import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.sql.SQLInvalidAuthorizationSpecException;

import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.Scanner;

public class Quilt extends JPanel {

    private static int[][] squares;

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        var center = new Point(getWidth() / 2, getHeight() / 2);
        var max = Math.min(getWidth() / 2, getHeight() / 2) - 5;
        var diameter = max * 2;

        g.setColor(Color.WHITE);
        g.fillOval(center.x - radius, center.y - radius, diameter, diameter);
        g.setColor(Color.RED);
        g.fillOval(center.x - innerRadius, center.y - innerRadius, innerDiameter, innerDiameter);
        g.setColor(Color.WHITE);
        g.fillRect(center.x - barWidth / 2, center.y - barHeight / 2, barWidth, barHeight);
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

        // parsing clean data
        squares = new int[i][4]; // i is the index of the last square
        for (i = 0; i < squares.length; i++) {
            lineParser = new Scanner(lines[i]);
            for (int k = 0; k < 3; k++) {
                squares[i][k] = lineParser.nextInt();
            }
        }

        SwingUtilities.invokeLater(() -> {
            var panel = new Quilt();
            panel.setBackground(Color.GREEN.darker());
            var frame = new JFrame("A simple graphics program");
            // make window a resonable size
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setSize(screenSize.width / 2, screenSize.height / 2);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(panel, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }

    public static void addSquare() {

    }

    public static String[] doubleArraySize(String[] a) {
        String[] b = new String[a.length * 2];
        for (int i = 0; i < a.length; i++) {
            b[i] = a[i];
        }
        return b;
    }
}