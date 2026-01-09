import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class realmainmenu extends JFrame {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;

    public realmainmenu() {
        setTitle("Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null); 
        setUndecorated(true); 

        setContentPane(new MenuPanel());
        setVisible(true);
    }

    class MenuPanel extends JPanel {

        public MenuPanel() {
            setBackground(Color.BLACK);
            setLayout(new GridBagLayout());
            
            JPanel buttonContainer = new JPanel();
            buttonContainer.setOpaque(false);
            buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));

            // Menu Options
            String[] options = {"START GAME", "INSTRUCTIONS", "CREDITS", "QUIT GAME"};
            for (String text : options) {
                buttonContainer.add(createMenuButton(text));
                // Space between buttons
                buttonContainer.add(Box.createVerticalStrut(20));
            }

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.insets = new Insets(150, 0, 0, 0); 
            add(buttonContainer, gbc);
        }

        private JButton createMenuButton(String text) {
            JButton button = new JButton(text);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setFont(new Font("Serif", Font.PLAIN, 28));
            button.setForeground(new Color(200, 200, 200));
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    button.setText("◈  " + text + "  ◈");
                    button.setForeground(Color.WHITE);
                }
                public void mouseExited(MouseEvent e) {
                    button.setText(text);
                    button.setForeground(new Color(200, 200, 200));
                }
                public void mousePressed(MouseEvent e) {
                    if (text.equals("QUIT GAME")){
                        System.exit(0);
                    }
                }
            });

            return button;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int centerX = getWidth() / 2;

            // Title
            g2d.setFont(new Font("Serif", Font.PLAIN, 100));
            g2d.setColor(Color.WHITE);
            String title = "BOOM TAG";
            FontMetrics fm = g2d.getFontMetrics();
            int titleX = (getWidth() - fm.stringWidth(title)) / 2;
            g2d.drawString(title, titleX, 250);

            // Lines
            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(new Color(200, 200, 200));
            
            g2d.drawArc(centerX - 150, 140, 300, 60, 0, 180);
            
            int lineY = 280;
            g2d.drawLine(centerX - 350, lineY, centerX - 40, lineY);
            g2d.drawLine(centerX + 40, lineY, centerX + 350, lineY);
            g2d.drawArc(centerX - 20, lineY - 10, 40, 20, 0, -180);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(realmainmenu::new);
    }
}