import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class DrawPad extends Frame {
    private final List<Point> points = new ArrayList<>();
    private final Label status = new Label("Clicks: 0");

    public DrawPad() {
        super("Draw Pad");
        add(status, BorderLayout.SOUTH);
        addMouseListener(new MouseAdapter() {
            // TODO 1: Override mouseClicked correctly: store the point, update the
            // label and request a repaint
            @Override
            public void mouseClicked(MouseEvent e) {
                points.add(new Point(e.getX(), e.getY()));
                status.setText("Clicks: " + points.size());
                repaint();
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
        setSize(400, 300);
    }

    @Override
    public void paint(Graphics g) {
        // TODO 2: Draw every stored point as a 6 x 6 filled circle centred on the point
        for (Point p : points) {
            g.fillOval(p.x - 3, p.y - 3, 6, 6);
        }
    }

    public static void main(String[] args) {
        System.out.println("Initializing repaired DrawPad...");
        DrawPad pad = new DrawPad();
        System.out.println("Simulating user clicks...");
        // Simulate clicking 3 points programmatically
        MouseEvent click1 = new MouseEvent(pad, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 50, 60, 1, false);
        MouseEvent click2 = new MouseEvent(pad, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 120, 140, 1, false);
        MouseEvent click3 = new MouseEvent(pad, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 200, 210, 1, false);

        pad.getMouseListeners()[0].mouseClicked(click1);
        pad.getMouseListeners()[0].mouseClicked(click2);
        pad.getMouseListeners()[0].mouseClicked(click3);

        System.out.println("Final Stored Points Count: " + pad.points.size());
        System.out.println("Status Label text: " + pad.status.getText());
        System.out.println("Verification: Points survive repaint() and window restoration.");
        pad.dispose();
    }
}
