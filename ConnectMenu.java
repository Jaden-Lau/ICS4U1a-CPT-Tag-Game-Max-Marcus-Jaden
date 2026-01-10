import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class ConnectMenu extends JFrame {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;

    public ConnectMenu() {
        setTitle("BOOM TAG - CONNECT");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);
        setUndecorated(true);

        setContentPane(new ConnectPanel());
        setVisible(true);
    }

    // Connect Panel
    class ConnectPanel extends JPanel {

        JTextField usernameField = new JTextField("Enter Username");
        JTextField ipField = new JTextField("Enter IP Address");
        JComboBox<String> modeChooser =
                new JComboBox<>(new String[]{"Client", "Server"});

        // Player color selection
        Color[] playerColors = {
                Color.BLUE,
                Color.RED,
                Color.GREEN,
                Color.YELLOW
        };
        int currentColorIndex = 0;
        JPanel colorPreview = new JPanel();

        public ConnectPanel() {
            setBackground(Color.BLACK);
            setLayout(new BorderLayout());

            // Header
            JPanel header = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    drawStylizedHeader(g, "CONNECT", getWidth() / 2);
                }
            };
            header.setPreferredSize(new Dimension(WIDTH, 220));
            header.setOpaque(false);
            add(header, BorderLayout.NORTH);

            // Username, IP ADDress, Client/Server, Connect/Back
            JPanel center = new JPanel();
            center.setOpaque(false);
            center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
            center.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

            styleField(usernameField);
            styleField(ipField);
            styleCombo(modeChooser);

            usernameField.setMaximumSize(new Dimension(420, 45));
            ipField.setMaximumSize(new Dimension(420, 45));
            modeChooser.setMaximumSize(new Dimension(420, 45));

            center.add(usernameField);
            center.add(Box.createVerticalStrut(15));
            center.add(createColorSelector());
            center.add(Box.createVerticalStrut(20));
            center.add(modeChooser);
            center.add(Box.createVerticalStrut(10));
            center.add(ipField);
            center.add(Box.createVerticalStrut(20));

            JButton connectBtn = createMenuButton("CONNECT");
            JButton backBtn = createMenuButton("BACK");

            center.add(connectBtn);
            center.add(Box.createVerticalStrut(1));
            center.add(backBtn);

            JPanel wrapper = new JPanel(new GridBagLayout());
            wrapper.setOpaque(false);
            wrapper.add(center);

            add(wrapper, BorderLayout.CENTER);

            // If client or server is chosen:
            modeChooser.addActionListener(e -> {
                if (modeChooser.getSelectedItem().equals("Server")) {
                    ipField.setEnabled(false);
                    ipField.setText("Server mode - no IP needed");
                } else {
                    ipField.setEnabled(true);
                    ipField.setText("Enter IP Address");
                }
            });

            backBtn.addActionListener(e -> dispose());
        }

        // Select colour
        private JPanel createColorSelector() {
            JPanel container = new JPanel();
            container.setOpaque(false);
            container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

            JLabel label = new JLabel("SELECT PLAYER COLOR");
            label.setFont(new Font("Serif", Font.PLAIN, 20));
            label.setForeground(new Color(180, 180, 180));
            label.setAlignmentX(Component.CENTER_ALIGNMENT);

            JPanel row = new JPanel();
            row.setOpaque(false);

            JButton left = createArrowButton("<");
            JButton right = createArrowButton(">");

            colorPreview.setPreferredSize(new Dimension(80, 80));
            colorPreview.setBackground(playerColors[currentColorIndex]);
            colorPreview.setBorder(BorderFactory.createLineBorder(new Color(90, 90, 90)));

            left.addActionListener(e -> {
                currentColorIndex--;
                if (currentColorIndex < 0)
                    currentColorIndex = playerColors.length - 1;
                colorPreview.setBackground(playerColors[currentColorIndex]);
            });

            right.addActionListener(e -> {
                currentColorIndex++;
                if (currentColorIndex >= playerColors.length)
                    currentColorIndex = 0;
                colorPreview.setBackground(playerColors[currentColorIndex]);
            });

            row.add(left);
            row.add(Box.createHorizontalStrut(15));
            row.add(colorPreview);
            row.add(Box.createHorizontalStrut(15));
            row.add(right);

            container.add(label);
            container.add(Box.createVerticalStrut(10));
            container.add(row);

            return container;
        }

        // Component backgrounds
        private void styleField(JTextField field) {
            field.setFont(new Font("Serif", Font.PLAIN, 22));
            field.setForeground(Color.WHITE);
            field.setBackground(Color.BLACK);
            field.setCaretColor(Color.WHITE);
            field.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        }

        private void styleCombo(JComboBox<String> combo) {
            combo.setFont(new Font("Serif", Font.PLAIN, 22));
            combo.setForeground(Color.WHITE);
            combo.setBackground(Color.BLACK);
            combo.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
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
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setText("◈  " + text + "  ◈");
                    button.setForeground(Color.WHITE);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setText(text);
                    button.setForeground(new Color(200, 200, 200));
                }
            });

            return button;
        }

        private JButton createArrowButton(String text) {
            JButton btn = new JButton(text);
            btn.setFont(new Font("Serif", Font.PLAIN, 36));
            btn.setForeground(new Color(200, 200, 200));
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setForeground(Color.WHITE);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setForeground(new Color(200, 200, 200));
                }
            });

            return btn;
        }
    }

    // Draw header
    private void drawStylizedHeader(Graphics g, String title, int centerX) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setFont(new Font("Serif", Font.PLAIN, 80));
        g2d.setColor(Color.WHITE);
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = (WIDTH - fm.stringWidth(title)) / 2;
        g2d.drawString(title, titleX, 150);

        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(new Color(150, 150, 150));
        g2d.drawArc(centerX - 100, 70, 200, 40, 0, 180);

        int lineY = 180;
        g2d.drawLine(centerX - 250, lineY, centerX - 40, lineY);
        g2d.drawLine(centerX + 40, lineY, centerX + 250, lineY);
        g2d.drawArc(centerX - 15, lineY - 8, 30, 15, 0, -180);
    }

    // Main run
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ConnectMenu::new);
    }
}
