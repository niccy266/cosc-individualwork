import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Quilt extends JPanel {
    private static final long serialVersionUID = 7148504528835036003L;

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        var center = new Point(getWidth() / 2, getHeight() / 2);
        var max = Math.min(getWidth() / 2, getHeight() / 2) - 5;
        var diameter = radius * 2;

        g.setColor(Color.WHITE);
        g.fillOval(center.x - radius, center.y - radius, diameter, diameter);
        g.setColor(Color.RED);
        g.fillOval(center.x - innerRadius, center.y - innerRadius, innerDiameter, innerDiameter);
        g.setColor(Color.WHITE);
        g.fillRect(center.x - barWidth / 2, center.y - barHeight / 2, barWidth, barHeight);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            var panel = new DoNotEnterSign();
            panel.setBackground(Color.GREEN.darker());
            var frame = new JFrame("A simple graphics program");
            frame.setSize(400, 300);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(panel, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }
}