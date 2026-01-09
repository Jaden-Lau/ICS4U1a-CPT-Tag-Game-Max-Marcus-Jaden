import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class realmainmenu extends JFrame {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainContainer = new JPanel(cardLayout);

    public realmainmenu() {
        setTitle("BOOM TAG");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);
        setUndecorated(true);

        // Add the different screens to the container
        mainContainer.add(new MenuPanel(), "MENU");
        mainContainer.add(new InstructionsPanel(), "INSTRUCTIONS");
        mainContainer.add(new CreditsPanel(), "CREDITS");

        setContentPane(mainContainer);
        setVisible(true);
    }

    // Main Menu Page
    class MenuPanel extends JPanel {
        public MenuPanel() {
            setBackground(Color.BLACK);
            setLayout(new GridBagLayout());

            JPanel buttonContainer = new JPanel();
            buttonContainer.setOpaque(false);
            buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));

            String[] options = {"START GAME", "INSTRUCTIONS", "CREDITS", "QUIT GAME"};
            for (String text : options) {
                buttonContainer.add(createMenuButton(text));
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
                    if (text.equals("QUIT GAME")) {
                        System.exit(0);
                    } else if (text.equals("INSTRUCTIONS")) {
                        cardLayout.show(mainContainer, "INSTRUCTIONS");
                    } else if (text.equals("CREDITS")) {
                        cardLayout.show(mainContainer, "CREDITS"); 
                    }
                }
            });
            return button;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawStylizedHeader(g, "BOOM TAG", getWidth() / 2);
        }
    }

    // Instructions
    class InstructionsPanel extends JPanel {
        public InstructionsPanel() {
            setBackground(Color.BLACK);
            setLayout(new BorderLayout());

            // Create the Text Area
            JTextArea textArea = new JTextArea();
            textArea.setText(
                "HOW TO PLAY\n\n" +
                "This is a real-time multiplayer TAG game. One player is 'IT'. " +
                "If you are IT, your goal is to tag another player. If you are not IT, " +
                "your goal is to avoid being tagged.\n\n" +

                "CONTROLS:\n\n" +
                "W - Jump\n" +
                "A - Move Left\n" +
                "D - Move Right\n\n" +

                "CONNECTION MODES:\n\n" +
                "SERVER:\n" +
                "Select Server mode if you are hosting the game. The server starts the game " +
                "and waits for other players to connect.\n\n" +

                "CLIENT:\n" +
                "Select Client mode if you are joining a game. Enter the IP address of the " +
                "server and press CONNECT.\n\n" +

                "NETWORKING (SSM):\n\n" +
                "This game uses SuperSocketMaster (SSM) to send messages between players. " +
                "SSM is responsible for transmitting player movement, tag events, and chat " +
                "messages in real time. Each message follows a specific format so the game " +
                "can distinguish between gameplay data and chat data."
            );
            
            textArea.setFont(new Font("Serif", Font.PLAIN, 22));
            textArea.setForeground(new Color(200, 200, 200));
            textArea.setBackground(Color.BLACK);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setMargin(new Insets(20, 20, 20, 20));

            // Wrap it in a Scroll Pane
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 300));
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50))); 
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            
            // Minimalist Scrollbar
            scrollPane.getVerticalScrollBar().setBackground(Color.BLACK);
            scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));

            // Center Scroll
            JPanel centerContainer = new JPanel(new GridBagLayout());
            centerContainer.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(150, 0, 0, 0);
            centerContainer.add(scrollPane, gbc);

            add(centerContainer, BorderLayout.CENTER);

            // Back Button
            JButton backBtn = new JButton("BACK");
            backBtn.setFont(new Font("Serif", Font.PLAIN, 24));
            backBtn.setForeground(Color.GRAY);
            backBtn.setContentAreaFilled(false);
            backBtn.setBorderPainted(false);
            backBtn.setFocusPainted(false);
            backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            backBtn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { backBtn.setForeground(Color.WHITE); }
                public void mouseExited(MouseEvent e) { backBtn.setForeground(Color.GRAY); }
                public void mousePressed(MouseEvent e) { cardLayout.show(mainContainer, "MENU"); }
            });

            JPanel bottomPanel = new JPanel();
            bottomPanel.setOpaque(false);
            bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
            bottomPanel.add(backBtn);
            add(bottomPanel, BorderLayout.SOUTH);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawStylizedHeader(g, "INSTRUCTIONS", getWidth() / 2);
        }
    }

    // Credits
    class CreditsPanel extends JPanel {
        public CreditsPanel() {
            setBackground(Color.BLACK);
            setLayout(new BorderLayout());

            // Create the Credits Text Area
            JTextArea textArea = new JTextArea();
            textArea.setText(
                "DEVELOPED BY\n" +
                "Jaden, Marcus, Max / BOOM TAG\n\n" +
                "SPECIAL THANKS FROM DEVELOPERS OF BOOM TAG\n"
            );
            
            textArea.setFont(new Font("Serif", Font.PLAIN, 22));
            textArea.setForeground(new Color(200, 200, 200));
            textArea.setBackground(Color.BLACK);
            textArea.setEditable(false);
            textArea.setFocusable(false);
            // Center the text within the text area itself
            textArea.setMargin(new Insets(20, 20, 20, 20));

            DefaultLabelHttp(textArea); 

            // Wrap it in a Scroll Pane
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 350));
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(40, 40, 40)));
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)); // Hide scrollbar for clean look

            // Center the ScrollPane
            JPanel centerContainer = new JPanel(new GridBagLayout());
            centerContainer.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(150, 0, 0, 0); 
            centerContainer.add(scrollPane, gbc);

            add(centerContainer, BorderLayout.CENTER);

            // Back Button
            JButton backBtn = new JButton("BACK");
            backBtn.setFont(new Font("Serif", Font.PLAIN, 24));
            backBtn.setForeground(Color.GRAY);
            backBtn.setContentAreaFilled(false);
            backBtn.setBorderPainted(false);
            backBtn.setFocusPainted(false);
            backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            backBtn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { backBtn.setForeground(Color.WHITE); }
                public void mouseExited(MouseEvent e) { backBtn.setForeground(Color.GRAY); }
                public void mousePressed(MouseEvent e) { cardLayout.show(mainContainer, "MENU"); }
            });

            JPanel bottomPanel = new JPanel();
            bottomPanel.setOpaque(false);
            bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
            bottomPanel.add(backBtn);
            add(bottomPanel, BorderLayout.SOUTH);
        }

        // Center text in the JTextArea
        private void DefaultLabelHttp(JTextArea area) {
            area.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
           
            area.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawStylizedHeader(g, "CREDITS", getWidth() / 2);
        }
    }

    // Drawing Method
    private void drawStylizedHeader(Graphics g, String title, int centerX) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Title
        g2d.setFont(new Font("Serif", Font.PLAIN, 80));
        g2d.setColor(Color.WHITE);
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = (WIDTH - fm.stringWidth(title)) / 2;
        g2d.drawString(title, titleX, 150);

        // Lines
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(new Color(150, 150, 150));
        g2d.drawArc(centerX - 100, 70, 200, 40, 0, 180); // Top arc
        
        int lineY = 180;
        g2d.drawLine(centerX - 250, lineY, centerX - 40, lineY); // Left line
        g2d.drawLine(centerX + 40, lineY, centerX + 250, lineY); // Right line
        g2d.drawArc(centerX - 15, lineY - 8, 30, 15, 0, -180); // Bottom tiny arc
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(realmainmenu::new);
    }
}