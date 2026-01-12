package TESTS;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class esc extends JPanel implements KeyListener,ActionListener{

    boolean blnEsc = false;
    JButton exitButton = new JButton("Exit");
    JButton resumeButton = new JButton("Resume");
    JButton instructions = new JButton("Instructions");

    public esc() {
        setPreferredSize(new Dimension(1280, 720));
        setLayout(null);
        setFocusable(true);
        addKeyListener(this);

        resumeButton.setBounds(540, 300, 200, 50);
        resumeButton.addActionListener(this);
        
        exitButton.setBounds(540, 370, 200, 50);
        exitButton.addActionListener(this);

        add(resumeButton);
        add(exitButton);

        hideButtons();

        JFrame frame = new JFrame("ESC Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(this);
        frame.pack();
        frame.setVisible(true);

        
    }
//
    public void hideButtons(){
        resumeButton.setVisible(false);
        exitButton.setVisible(false);
    }

    public void showButtons(){
        resumeButton.setVisible(true);
        exitButton.setVisible(true);
    }
//
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (blnEsc) {
            g.setColor(new Color(0, 0, 0, 128));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.drawString("PAUSED", 610, 250);
        } else {
            
            g.setColor(Color.BLACK);
            g.drawString("GAME RUNNING", 20, 20);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if(blnEsc == false){
                //System.out.println("esc");
                blnEsc = true;
                showButtons();
                repaint();
            }else if(blnEsc == true){
                blnEsc = false;
                hideButtons();
                repaint();
            }
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == resumeButton){
            blnEsc = false;
            hideButtons();
            repaint();
        }else if(e.getSource() == exitButton){
            System.exit(0);
        }
    }
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        new esc();
    }

    
}
