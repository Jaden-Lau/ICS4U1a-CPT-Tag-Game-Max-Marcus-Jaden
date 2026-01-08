package TESTS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class esc extends JPanel implements KeyListener {

    boolean blnEsc = false;
    JButton instructions = new JButton();

    public esc() {
        setPreferredSize(new Dimension(1280, 720));
        setFocusable(true);
        addKeyListener(this);


        JFrame frame = new JFrame("ESC Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(this);
        frame.pack();
        frame.setVisible(true);

        
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (blnEsc) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.out.println("esc");
            blnEsc = true;
            repaint();
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        new esc();
    }
}
