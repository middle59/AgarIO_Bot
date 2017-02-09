package UI;

import Utilities.ScreenCap;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;


import javax.swing.*;

/**
 * This class checks the position every #DELAY milliseconds and
 * informs all registered MouseMotionListeners about position updates.
 */
public class MouseXY {
    /* the resolution of the mouse motion */
    private static final int DELAY = 10;

    //UI
    private JFrame frame;
    private Component component;
    private JLabel xPosLabel, yPosLabel, rValLabel, gValLabel, bValLabel;

    //AgarIO.AgarIOController
    private static boolean pollMousePos = true;
    private Timer timer;
    private Set<MouseMotionListener> mouseMotionListeners;

    protected MouseXY() {
        xPosLabel = new JLabel("X: ");
        yPosLabel = new JLabel("Y: ");
        rValLabel = new JLabel("R: ");
        gValLabel = new JLabel("G: ");
        bValLabel = new JLabel("B: ");


        frame = new JFrame("Mouse Coord Reader");
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        FlowLayout experimentLayout = new FlowLayout();
        frame.setLayout(experimentLayout);
        frame.add(xPosLabel);
        frame.add(yPosLabel);
        frame.add(rValLabel);
        frame.add(gValLabel);
        frame.add(bValLabel);
        frame.add(new JLabel("Press the 'End' key to disable/re-enable tracking."));

        frame.setSize(300, 100);
        frame.setVisible(true);


        this.component = frame;

        /* poll mouse coordinates at the given rate */
        timer = new Timer(DELAY, new ActionListener() {
            private Point lastPoint = MouseInfo.getPointerInfo().getLocation();

            /* called every DELAY milliseconds to fetch the
             * current mouse coordinates */
            public synchronized void actionPerformed(ActionEvent e) {
                Point point = MouseInfo.getPointerInfo().getLocation();

                if (!point.equals(lastPoint) && pollMousePos) {
                    fireMouseMotionEvent(point);
                }

                lastPoint = point;
            }
        });
        mouseMotionListeners = new HashSet<MouseMotionListener>();

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent ke) {
                synchronized (MouseXY.class) {
                    switch (ke.getID()) {
                        case KeyEvent.KEY_PRESSED:
                            if (ke.getKeyCode() == KeyEvent.VK_END) {
                                pollMousePos = !pollMousePos;
                            }
                            break;
                    }
                    return false;
                }
            }
        });

    }

    public void pollMousePos(boolean val) { pollMousePos = val;}

    public Component getComponent() {
        return component;
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    public void addMouseMotionListener(MouseMotionListener listener) {
        synchronized (mouseMotionListeners) {
            mouseMotionListeners.add(listener);
        }
    }

    public void removeMouseMotionListener(MouseMotionListener listener) {
        synchronized (mouseMotionListeners) {
            mouseMotionListeners.remove(listener);
        }
    }

    protected void fireMouseMotionEvent(Point point) {
        synchronized (mouseMotionListeners) {
            for (final MouseMotionListener listener : mouseMotionListeners) {
                final MouseEvent event =
                        new MouseEvent(component, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(),
                                0, point.x, point.y, 0, false);

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        listener.mouseMoved(event);
                    }
                });
            }
        }
    }

    public void setXYLabels(int x, int y)
    {
        xPosLabel.setText("X: "+x);
        yPosLabel.setText("Y:"+y);

        frame.repaint();
    }

    public void setRGBLabels(int r, int g, int b)
    {
        rValLabel.setText("R: "+r);
        gValLabel.setText("G:"+g);
        bValLabel.setText("B:"+b);

        frame.repaint();
    }

    public static boolean isWPressed() {
        synchronized (MouseXY.class) {
            return pollMousePos;
        }
    }

    /* Testing the ovserver */
    public static void main(String[] args) {

        MouseXY mo = new MouseXY();
        mo.addMouseMotionListener(new MouseMotionListener() {
            public void mouseMoved(MouseEvent e) {
                //System.out.println("mouse moved: " + e.getPoint());
                mo.setXYLabels(e.getPoint().x, e.getPoint().y);
                Color pointColor = ScreenCap.getPointRGB(e.getPoint());
                mo.setRGBLabels(pointColor.getRed(),pointColor.getGreen(),pointColor.getBlue());
            }

            public void mouseDragged(MouseEvent e) {
                //System.out.println("mouse dragged: " + e.getPoint());
            }
        });

        mo.start();
    }
}