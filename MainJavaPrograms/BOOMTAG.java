package MainJavaPrograms;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.*;

/*
 * Program Name: BOOM TAG
 * Course: ICS4U
 * Authors: Jaden, Marcus, Max
 * Date: January 20, 2026
 * Description:
 * A real-time multiplayer tag game built in Java using Swing for graphics
 * and SuperSocketMaster for networking. Players connect over a network,
 * select a map, and compete in a fast-paced tag-style game.
 */

public class BOOMTAG extends JFrame implements ActionListener {

    // Screen Properties
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainContainer = new JPanel(cardLayout);

    // Game Properties
    SuperSocketMaster ssm = null;
    HashMap<String, Player> players = new HashMap<>();
    String myUsername;
    Timer gameTimer;
    Player localPlayer;
    boolean[] keys = new boolean[256];
    int vy = 0;
    String winnerName = "";

    int bombTimer; 
    Timer bombCountdown = new Timer(1000, this);
    int graceTimer;
    Timer graceCountdown = new Timer(1000,this);
    boolean gameActive = false;
    boolean gracePeriod = false;
    boolean gameOver = false;
    int roundsPlayed = 0;
    int tagImmune;
    boolean immune = false;
    Timer freezeTimer = null;
    Timer immunityTimer = null;
    
    // Track used spawn points
    java.util.List<Integer> usedSpawnPoints = new java.util.ArrayList<>();

    // Connect Panel
    JPanel connectPanel = new JPanel();
    JTextField username = new JTextField("Enter Username");
    JLabel SPCtextPanel1 = new JLabel("Select Player Color");
    JButton rightColorButton = createArrowButton(">");
    JButton leftColorButton = createArrowButton("<");
    JButton connectBackBtn = createMenuButton("BACK");
    JButton connectBtn = createMenuButton("CONNECT");
    
    JComboBox<String> modeChooser = new JComboBox<>(new String[]{"Client", "Server"});
    JTextField IPAdressField = new JTextField("Enter IP Address");
    JLabel waitingLabel = new JLabel("WAITING FOR PLAYERS...");
    JPanel colorPreview = new JPanel();
    
    Color[] playerColors = {Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW};
    int currentColorIndex = 0;
    Font mainFont = new Font("Arial", Font.BOLD, 24);

    // Map Select
    JPanel mapSelectPanel = new JPanel();
    JButton map1Btn = new JButton("Map 1");
    JButton map2Btn = new JButton("Map 2");

    private static final int MAX_PLAYERS = 4;
    private int currentPlayers = 1;
    private JLabel lobbyCountLabel;
    
    // Game
    GamePanel gamePanel = new GamePanel();
    String[][] mapData = new String[16][16];
    BufferedImage groundBtm, groundTop, skyBtm, skyTop;

    // Game Over
    JPanel endPanel = new endPanel();

    // Chat
    JLayeredPane layeredPane = new JLayeredPane();
    JPanel chatPanel = new JPanel(new BorderLayout());
    JTextArea chatTextArea = new JTextArea("Press shift to close chat, enter to open.\n");
    JTextField chatTextField = new JTextField("Type message here...");

    // Constructor
    public BOOMTAG() {
        setTitle("BOOM TAG");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);
        setUndecorated(true);

        loadAssets();
        gameTimer = new Timer(1000/60, this);
        gameTimer.start();

        // Setup Panels
        setupConnectPanel();
        setupMapSelectPanel();
        setupGameLayer();

        mainContainer.add(new MenuPanel(), "MENU");
        mainContainer.add(new InstructionsPanel(), "INSTRUCTIONS");
        mainContainer.add(new CreditsPanel(), "CREDITS");
        mainContainer.add(connectPanel, "CONNECT");
        mainContainer.add(mapSelectPanel, "MAP_SELECT");
        mainContainer.add(layeredPane, "GAME");

        setContentPane(mainContainer);
        
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) { keys[e.getKeyCode()] = true; }
            @Override
            public void keyReleased(KeyEvent e) { keys[e.getKeyCode()] = false; }
        });
        this.setFocusable(true);
        
        setVisible(true);
    }

    // Methods
    private void loadAssets() {
        try {
            groundBtm = ImageIO.read(new File("Map Tiles/groundBottom1.png"));
            groundTop = ImageIO.read(new File("Map Tiles/groundTop1.png"));
            skyBtm = ImageIO.read(new File("Map Tiles/skyBottom1.png"));
            skyTop = ImageIO.read(new File("Map Tiles/skyTop1.png"));
        } catch (IOException e) {
            System.out.println("Warning: Map Tiles not found. Game will run without textures.");
        }
    }

    private void setupConnectPanel() {
        
        connectPanel.setBackground(Color.BLACK);
        connectPanel.setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawStylizedHeaderC(g, "CONNECT", getWidth() / 2);
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

        styleField(username);
        styleField(IPAdressField);
        styleCombo(modeChooser);

        username.setMaximumSize(new Dimension(420, 45));
        IPAdressField.setMaximumSize(new Dimension(420, 45));
        modeChooser.setMaximumSize(new Dimension(420, 45));

        center.add(username);
        center.add(Box.createVerticalStrut(15));
        center.add(createColorSelector());
        center.add(Box.createVerticalStrut(20));
        center.add(modeChooser);
        center.add(Box.createVerticalStrut(10));
        center.add(IPAdressField);
        center.add(Box.createVerticalStrut(20));

        center.add(connectBtn);
        center.add(Box.createVerticalStrut(1));
        center.add(connectBackBtn);
        connectPanel.add(header, BorderLayout.NORTH);
        connectBtn.addActionListener(this);
        connectBackBtn.addActionListener(this);
        
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(center);

        connectPanel.add(wrapper, BorderLayout.CENTER);
        waitingLabel.setForeground(Color.WHITE);
        waitingLabel.setFont(new Font("Arial", Font.BOLD, 36));
        waitingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        waitingLabel.setVisible(false);

        connectPanel.add(waitingLabel, BorderLayout.SOUTH);
        // If client or server is chosen:
        modeChooser.addActionListener(e -> {
            if (modeChooser.getSelectedItem().equals("Server")) {
                IPAdressField.setEnabled(false);
                IPAdressField.setText("Server mode - no IP needed");
            } else {
                IPAdressField.setEnabled(true);
                IPAdressField.setText("Enter IP Address");
            }
        });   
    }

    // Select colour
    private JPanel createColorSelector() {
        JPanel container = new JPanel();
        container.setOpaque(false);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        
        SPCtextPanel1.setFont(new Font("Serif", Font.PLAIN, 20));
        SPCtextPanel1.setForeground(new Color(180, 180, 180));
        SPCtextPanel1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel row = new JPanel();
        row.setOpaque(false);

        colorPreview.setPreferredSize(new Dimension(80, 80));
        colorPreview.setBackground(playerColors[currentColorIndex]);
        colorPreview.setBorder(BorderFactory.createLineBorder(new Color(90, 90, 90)));

        leftColorButton.addActionListener(e -> {
            currentColorIndex--;
            if (currentColorIndex < 0)
                currentColorIndex = playerColors.length - 1;
            colorPreview.setBackground(playerColors[currentColorIndex]);
        });

        rightColorButton.addActionListener(e -> {
            currentColorIndex++;
            if (currentColorIndex >= playerColors.length)
                currentColorIndex = 0;
            colorPreview.setBackground(playerColors[currentColorIndex]);
        });

        row.add(leftColorButton);
        row.add(Box.createHorizontalStrut(15));
        row.add(colorPreview);
        row.add(Box.createHorizontalStrut(15));
        row.add(rightColorButton);

        container.add(SPCtextPanel1);
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

    // Draw header
    private void drawStylizedHeaderC(Graphics g, String title, int centerX) {
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

        waitingLabel.setForeground(Color.WHITE);
        waitingLabel.setFont(new Font("Arial", Font.BOLD, 36));
        waitingLabel.setBounds(450, 500, 650, 60);
        waitingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        waitingLabel.setVisible(false);
        

    }

    private void setupMapSelectPanel() {
        mapSelectPanel.setLayout(null);
        mapSelectPanel.setBackground(Color.BLACK);
        
        JLabel mapLabel = new JLabel("SELECT MAP", SwingConstants.CENTER);
        mapLabel.setForeground(Color.WHITE);
        mapLabel.setFont(new Font("Serif", Font.BOLD, 48));
        mapLabel.setBounds(0, 50, WIDTH, 100);
        mapSelectPanel.add(mapLabel);

        lobbyCountLabel = new JLabel("", SwingConstants.CENTER);
        lobbyCountLabel.setForeground(new Color(180, 180, 180));
        lobbyCountLabel.setFont(new Font("Arial", Font.BOLD, 26));
        lobbyCountLabel.setBounds(0, 140, WIDTH, 40);

        updateLobbyLabel();
        mapSelectPanel.add(lobbyCountLabel);

        map1Btn.setBounds(300, 250, 300, 300);
        map1Btn.setFont(mainFont);
        map1Btn.addActionListener(this);
        mapSelectPanel.add(map1Btn);

        map2Btn.setBounds(680, 250, 300, 300);
        map2Btn.setFont(mainFont);
        map2Btn.addActionListener(this);
        mapSelectPanel.add(map2Btn);
    }
    private void updateLobbyLabel() {
        lobbyCountLabel.setText(currentPlayers + " / " + MAX_PLAYERS);
    }

    private void setupGameLayer() {
        // Game Panel
        gamePanel.setBounds(0, 0, WIDTH, HEIGHT);
        gamePanel.setOpaque(true);
        gamePanel.setBackground(new Color(135, 206, 235)); // Sky blue default

        // Chat Panel
        chatPanel.setBounds(0, HEIGHT - 150, 350, 150);
        chatPanel.setBackground(new Color(0, 0, 0, 180));
        
        chatTextArea.setEditable(false);
        chatTextArea.setLineWrap(true);
        chatTextArea.setForeground(Color.WHITE);
        chatTextArea.setBackground(new Color(0,0,0,0));
        chatTextArea.setOpaque(false);

        chatTextField.addActionListener(this);
        
        JScrollPane scroll = new JScrollPane(chatTextArea);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        chatPanel.add(scroll, BorderLayout.CENTER);
        chatPanel.add(chatTextField, BorderLayout.SOUTH);

        layeredPane.setLayout(null);
        layeredPane.add(gamePanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(chatPanel, JLayeredPane.PALETTE_LAYER);
    }

    // Main Menu
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
            button.setPreferredSize(new Dimension(360, 50));
            button.setMinimumSize(button.getPreferredSize());
            button.setMaximumSize(button.getPreferredSize());
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
                    } else if (text.equals("START GAME")) {
                        cardLayout.show(mainContainer, "CONNECT");
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
                "This project was created as the final culminating assignment \nfor St. Augustine's ICS4U1 Computer Science Course. \nWe hope you enjoy it!\n\n\n" + 
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

    // Game Logic
    @Override
    public void actionPerformed(ActionEvent evt) {
        
        // Game loop
        if (evt.getSource() == gameTimer) {
            handleMovement();
            checkCollisions();
            gamePanel.repaint();
        } 
        
        // Bomb Logic (Server)
        else if(evt.getSource() == bombCountdown && modeChooser.getSelectedItem().equals("Server")){
                bombTimer--;
                ssm.sendText("TIME:" + bombTimer); 
            if (bombTimer <= 0) {
                bombCountdown.stop();
                graceCountdown.stop();
                graceTimer = 5;
                gracePeriod = true;
                graceCountdown.start();
                for (Player p : players.values()) {
                    if (p.isIt) {
                        ssm.sendText("EXPLODE:" + p.name);
                        p.isIt = false;
                        p.isAlive = false;
                        break;
                    }
                }
                pickRandomIt();
            }
        }else if(evt.getSource() == graceCountdown && modeChooser.getSelectedItem().equals("Server")){
            graceTimer--;
            ssm.sendText("GRACETIME:" + graceTimer);
            if (graceTimer < 0){
                graceTimer = 5;
                graceCountdown.stop();
                gracePeriod = false;
                bombTimer = 15; 
                bombCountdown.start();    //THIS STARTS BOMB TIMER
                pickRandomIt();
            }
        }


        // Network
        if (ssm != null && evt.getSource() == ssm) {
            String msg = ssm.readText();
            handleNetworkMessage(msg);
        }

        // UI Actions
        if (evt.getSource() == connectBackBtn) {
            cardLayout.show(mainContainer, "MENU");
        } 
        else if (evt.getSource() == rightColorButton) {
            currentColorIndex = (currentColorIndex + 1) % playerColors.length;
            colorPreview.setBackground(playerColors[currentColorIndex]);
        } 
        else if (evt.getSource() == leftColorButton) {
            currentColorIndex--;
            if (currentColorIndex < 0) currentColorIndex = playerColors.length - 1;
            colorPreview.setBackground(playerColors[currentColorIndex]);
        }
        else if (evt.getSource() == modeChooser) {
            if (modeChooser.getSelectedItem().equals("Server")) {
                IPAdressField.setEnabled(false);
                IPAdressField.setText("Server Mode");
            } else {
                IPAdressField.setEnabled(true);
                IPAdressField.setText("");
            }
        }
        else if (evt.getSource() == connectBtn) {
            handleConnection();
        }
        else if (evt.getSource() == map1Btn || evt.getSource() == map2Btn) {
            handleMapSelection(evt.getSource() == map1Btn ? "map1.csv" : "map2.csv");
        }
        else if (evt.getSource() == chatTextField) {
            String strLine = chatTextField.getText();
            ssm.sendText("CHAT:" + myUsername + ":" + strLine);
            chatTextField.setText("");
            chatTextArea.append("\n" + myUsername + ": " + strLine);
            chatTextField.setFocusable(false);
        }
    }

    // Logics
    private void handleNetworkMessage(String msg) {
        System.out.println("[" + (modeChooser.getSelectedItem().equals("Server") ? "SERVER" : "CLIENT") + "] Received: " + msg);
        
        // ===== JOINED MESSAGE =====
        if (msg.startsWith("JOINED:")) {
            String[] data = msg.substring(7).split(",");
            String user = data[0];
            int colorIdx = Integer.parseInt(data[1]);
            
            // SERVER ONLY: When server receives JOINED from a client
            if (modeChooser.getSelectedItem().equals("Server")) {
                
                int spawnX = findSafeSpawnX();
                
                // Add player to server's HashMap
                Player newPlayer = new Player(spawnX, 45, playerColors[colorIdx], user);
                players.put(user, newPlayer);
                
                System.out.println("[SERVER] New player joined: " + user + " at x=" + spawnX);
                
                
                ssm.sendText("SPAWN:" + user + "," + colorIdx + "," + spawnX);
                
                
                for (Player p : players.values()) {
                    if (!p.name.equals(user)) {
                        String existingMsg = "SPAWN:" + p.name + "," + getColorIndex(p.color) + "," + p.x;
                        ssm.sendText(existingMsg);
                        System.out.println("[SERVER] Sending existing player to new client: " + existingMsg);
                    }
                }
            }
            // CLIENT: Should never receive raw JOINED (server converts to SPAWN)
        }
        
        // ===== SPAWN MESSAGE  =====
        else if (msg.startsWith("SPAWN:")) {
            String[] data = msg.substring(6).split(",");
            String user = data[0];
            int colorIdx = Integer.parseInt(data[1]);
            int spawnX = Integer.parseInt(data[2]);
            
            System.out.println("[CLIENT] Spawning player: " + user + " at x=" + spawnX);
            
            
            if (!user.equals(myUsername)) {
                Player newPlayer = new Player(spawnX, 45, playerColors[colorIdx], user);
                players.put(user, newPlayer);
                System.out.println("[CLIENT] Added remote player: " + user);
            } else {
                // Update your own position to match server
                if (localPlayer != null) {
                    localPlayer.x = spawnX;
                    System.out.println("[CLIENT] Updated own position to server's spawn: " + spawnX);
                }
            }
        }
        
        // ===== POSITION UPDATES =====
        else if (msg.startsWith("POS:")) {
            String[] data = msg.substring(4).split(",");
            String user = data[0];
            int newX = Integer.parseInt(data[1]);
            int newY = Integer.parseInt(data[2]);
            
            // SERVER: Broadcast to all other clients
            if (modeChooser.getSelectedItem().equals("Server")) {
                ssm.sendText(msg);
            }
            
            // EVERYONE: Update position
            if (!user.equals(myUsername) && players.containsKey(user)) {
                players.get(user).x = newX;
                players.get(user).y = newY;
            }
        }
        
        // ===== TAG EVENTS =====
        else if (msg.startsWith("TAGGED:")) {
            String target = msg.substring(7);
            
            // SERVER: Broadcast to all clients
            if (modeChooser.getSelectedItem().equals("Server")) {
                ssm.sendText(msg);
            }
            
            // EVERYONE: Update who is IT
            for (Player p : players.values()) {
                p.isIt = false;
                p.isImmune = false;
                p.isFrozen = false; 
            }

            if (players.containsKey(target)) {
                players.get(target).isIt = true;
                players.get(target).isFrozen = true; 
                // Start Freeze  Timer
                if (target.equals(myUsername)) {
                    if (freezeTimer != null) freezeTimer.stop();
                    freezeTimer = new Timer(2000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            localPlayer.isFrozen = false;
                            if (ssm != null) ssm.sendText("UNFROZEN:" + myUsername);
                            ((Timer)e.getSource()).stop();
                        }
                    });
                    freezeTimer.setRepeats(false);
                    freezeTimer.start();
                }
            }
        }

        else if (msg.startsWith("UNFROZEN:")) {
            String user = msg.substring(9);
            if (players.containsKey(user)) {
                players.get(user).isFrozen = false;
            }
        }

        else if (msg.startsWith("UNIMMUNE:")) {
            String user = msg.substring(9);
            if (players.containsKey(user)) {
                players.get(user).isImmune = false;
            }
        }

        else if (msg.startsWith("IMMUNE:") && modeChooser.getSelectedItem().equals("Server")){
            String[] data = msg.substring(7).split(",");
            String immuneUser = data[0];
            tagImmune = Integer.parseInt(data[1]);
            ssm.sendText("IMMUNE2" + immuneUser + "," + tagImmune);
            if(myUsername.equals(immuneUser)){
                immune = true;
            }
            if (tagImmune == 0){
                immune = false;
            }
        }

        else if (msg.startsWith("IMMUNE2:") && modeChooser.getSelectedItem().equals("Client")){
            String[] data = msg.substring(8).split(",");
            String immuneUser = data[0];
            tagImmune = Integer.parseInt(data[1]);
            if(myUsername.equals(immuneUser)){
                immune = true;
            }
            if (tagImmune == 0){
                immune = false;
            }
        }
        
        // ===== EXPLOSION =====
        else if (msg.startsWith("EXPLODE:")) {
            String loser = msg.substring(8);
            System.out.println(loser + " went BOOM!");
            roundsPlayed += 1;
            graceTimer = 5;
            
            if (players.containsKey(loser)){
                players.get(loser).isAlive = false;
                players.get(loser).isIt = false;
            }
        }
        
        // ===== TIMERS =====
        else if (msg.startsWith("TIME:")) {
            bombTimer = Integer.parseInt(msg.substring(5));
        }
        else if (msg.startsWith("GRACETIME:")) {
            graceTimer = Integer.parseInt(msg.substring(10));
            gracePeriod = true;
            if (graceTimer <= 0){
                gracePeriod = false;
            }
        }
        
        // ===== GAME OVER =====
        else if (msg.startsWith("GAMEOVER:")){
            winnerName = msg.substring(9);
            endGame();  
        }
        
        // ===== CHAT =====
        else if (msg.startsWith("CHAT:")) {
            String[] parts = msg.split(":", 3);
            if (parts.length >= 3) {
                // SERVER: Broadcast to all clients
                if (modeChooser.getSelectedItem().equals("Server")) {
                    ssm.sendText(msg);
                }
                chatTextArea.append("\n" + parts[1] + ": " + parts[2]);
            }
        }
        
        // Lobby
        else if (msg.startsWith("LOBBY:")) {
            currentPlayers = Integer.parseInt(msg.substring(6));
            updateLobbyLabel();
        }
        
        // Connection
        else if (msg.equals("JOIN")) {
            // SERVER ONLY: Client wants to join
            if (modeChooser.getSelectedItem().equals("Server")) {
                currentPlayers++;
                ssm.sendText("LOBBY:" + currentPlayers);
                updateLobbyLabel();
                cardLayout.show(mainContainer, "MAP_SELECT");
            }
        }
        
        // ===== MAP SELECTION =====
        else if (msg.equals("MAP:1") || msg.equals("MAP:2")) {
            // CLIENT ONLY: Server selected a map
            if (!modeChooser.getSelectedItem().equals("Server")) {
                String mapFile = msg.equals("MAP:1") ? "map1.csv" : "map2.csv";
                loadMap(mapFile);
                startGameSession();
            }
        }
    }

    private void handleConnection() {
        if (modeChooser.getSelectedItem().equals("Server")) {
            // Server mode: Create a server socket and wait for connections
            ssm = new SuperSocketMaster(1234, this);
            currentPlayers = 1;
            updateLobbyLabel();
            if (ssm.connect()) {
                cardLayout.show(mainContainer, "CONNECT");
                waitingLabel.setVisible(true);
            }
        } else {
            // Client mode: Connect to the server IP
            String ip = IPAdressField.getText();
            ssm = new SuperSocketMaster(ip, 1234, this);
            if (ssm.connect()) {
                ssm.sendText("JOIN"); 
                waitingLabel.setText("CONNECTED! WAITING FOR HOST...");
                waitingLabel.setVisible(true);
                connectBtn.setVisible(false);
                connectBackBtn.setVisible(false);
            }
        }
    }
    private int getColorIndex(Color color) {
        for (int i = 0; i < playerColors.length; i++) {
            if (playerColors[i].equals(color)) {
                return i;
            }
        }
        return 0; // Default to first color
    }

    private void handleMapSelection(String mapFile) {
        loadMap(mapFile);
        if (ssm != null) ssm.sendText(mapFile.equals("map1.csv") ? "MAP:1" : "MAP:2");
        startGameSession();
    }

    private void startGameSession() {
        SwingUtilities.invokeLater(() -> {
            myUsername = username.getText();
            
            // CLIENT: Create temporary local player (server will send authoritative position)
            if (!modeChooser.getSelectedItem().equals("Server")) {
                localPlayer = new Player(640, 45, playerColors[currentColorIndex], myUsername);
            } 
            // SERVER: Create local player with safe spawn
            else {
                int spawnX = findSafeSpawnX();
                localPlayer = new Player(spawnX, 45, playerColors[currentColorIndex], myUsername);
            }
            
            // Add to HashMap (same object reference!)
            players.put(myUsername, localPlayer);
            
            gameActive = true;
            
            // Send JOINED to server (server will respond with SPAWN)
            ssm.sendText("JOINED:" + myUsername + "," + currentColorIndex);
            
            chatPanel.setVisible(true);
            cardLayout.show(mainContainer, "GAME");
            this.requestFocusInWindow();

            if (modeChooser.getSelectedItem().equals("Server")) {
                graceCountdown.stop();
                graceTimer = 5;
                gracePeriod = true;
                graceCountdown.start();
            }
            
            System.out.println("[" + (modeChooser.getSelectedItem().equals("Server") ? "SERVER" : "CLIENT") + 
                            "] Started game session as: " + myUsername);
        });
    }

    private void loadMap(String fileName) {
        File mapFile = new File("MAPS/" + fileName);
        try (BufferedReader br = new BufferedReader(new FileReader(mapFile))) {
            String line;
            int row = 0;
            while ((line = br.readLine()) != null && row < 16) {
                String[] tiles = line.split(",");
                for (int col = 0; col < Math.min(tiles.length, 16); col++) {
                    mapData[row][col] = tiles[col].trim();
                }
                row++;
            }
        } catch (IOException e) {
            System.out.println("File Error: Could not find " + fileName);
        }
    }

    private void handleMovement() {
        if (!gameActive) return;
        if (localPlayer == null) return;
        if (!localPlayer.isAlive) return;
        if (gracePeriod) return;
        if (localPlayer.isFrozen) return;

        // Normal movement (when not knocked back)
        int speed = 5;
        int nextX = localPlayer.x;
        
        if (keys[KeyEvent.VK_A]) nextX -= speed;
        if (keys[KeyEvent.VK_D]) nextX += speed;

        if (!isSolid(nextX, localPlayer.y) && !isSolid(nextX + 39, localPlayer.y + 39)) {
            localPlayer.x = nextX;
        }

        vy += 1; // Gravity
        boolean onGround = isSolid(localPlayer.x, localPlayer.y + 41) || isSolid(localPlayer.x + 39, localPlayer.y + 41);
        if (keys[KeyEvent.VK_W] && onGround) {
            vy = -15;
        }

        int nextYWithGravity = localPlayer.y + vy;
        if (!isSolid(localPlayer.x, nextYWithGravity) && !isSolid(localPlayer.x + 39, nextYWithGravity + 39)) {
            localPlayer.y = nextYWithGravity;
        } else {
            vy = 0;
        }

        if (ssm != null) {
            ssm.sendText("POS:" + myUsername + "," + localPlayer.x + "," + localPlayer.y);
        }

        // Chat Toggle Logic   
        if (keys[KeyEvent.VK_SHIFT]) {
            chatPanel.setVisible(false);
        }

        if (keys[KeyEvent.VK_ENTER]) {
            if(!chatPanel.isVisible()){
                chatTextField.setFocusable(true);
                chatPanel.setVisible(true);
            }
        }
    }

    public boolean isSolid(int pixelX, int pixelY) {
        int col = pixelX / 80;
        int row = pixelY / 45;
        if (row < 0 || row >= 16 || col < 0 || col >= 16) return true;
        if (mapData[row][col] == null) return false;
        return mapData[row][col].equals("bg") || mapData[row][col].equals("tg");
    }
    
    // Find a safe spawn location that's not inside a wall
    private int findSafeSpawnX() {
        // Try to find a safe spawn point by checking multiple positions
        int[] possibleSpawns = {200, 400, 600, 800, 1000, 300, 500, 700, 900, 100, 150, 250, 350, 450, 550, 650, 750, 850, 950, 1050};
        
        for (int x : possibleSpawns) {
            // Skip if this spawn point is already used
            if (usedSpawnPoints.contains(x)) {
                continue;
            }
            
            // Check if this position is safe (not solid and has ground below)
            boolean topLeftClear = !isSolid(x, 50);
            boolean topRightClear = !isSolid(x + 39, 50);
            boolean bottomLeftClear = !isSolid(x, 89);
            boolean bottomRightClear = !isSolid(x + 39, 89);
            
            // Check if there's ground below (within reasonable distance)
            boolean hasGroundBelow = false;
            for (int checkY = 90; checkY < 700; checkY += 45) {
                if (isSolid(x, checkY) || isSolid(x + 39, checkY)) {
                    hasGroundBelow = true;
                    break;
                }
            }
            
            // If all corners are clear and there's ground below, this is a safe spawn
            if (topLeftClear && topRightClear && bottomLeftClear && bottomRightClear && hasGroundBelow) {
                usedSpawnPoints.add(x); // Mark this spawn point as used
                return x;
            }
        }
        
        // If no safe spawn found, find any unused position from the list
        for (int x : possibleSpawns) {
            if (!usedSpawnPoints.contains(x)) {
                usedSpawnPoints.add(x);
                return x;
            }
        }
        
        // Absolute fallback - return center of screen
        return 640;
    }
    
    private void checkCollisions() {
        if (!gameActive) return;
        if (localPlayer == null || !localPlayer.isIt) return;
        if (!localPlayer.isAlive) return;
        if (localPlayer.isFrozen) return;

        for (Player other : players.values()) {
            if (other.name.equals(myUsername)) continue;
            if (!other.isAlive) continue;
            if (other.isImmune) continue;
            
            Rectangle myRect = new Rectangle(localPlayer.x, localPlayer.y, 40, 40);
            Rectangle otherRect = new Rectangle(other.x, other.y, 40, 40);

            if (myRect.intersects(otherRect)) {
                localPlayer.isIt = false;
                localPlayer.isImmune = true;
                
                // Start YOUR immunity timer (2 seconds)
                if (immunityTimer != null) immunityTimer.stop();
                immunityTimer = new Timer(2000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        localPlayer.isImmune = false;
                        if (ssm != null) ssm.sendText("UNIMMUNE:" + myUsername);
                        ((Timer)e.getSource()).stop();
                    }
                });
                immunityTimer.setRepeats(false);
                immunityTimer.start();
                
                if (ssm != null) {
                    ssm.sendText("TAGGED:" + other.name);
                }
                
                for (Player p : players.values()) p.isIt = false;
                if (players.containsKey(other.name)) {
                    players.get(other.name).isIt = true;
                    players.get(other.name).isFrozen = true;
                }
                
                break;
            }
        }
    }

    private void endGame(){
        bombCountdown.stop();
        graceCountdown.stop();
        gameActive = false;
        gracePeriod = false;
        usedSpawnPoints.clear(); // Clear spawn points for next game
        mainContainer.add(endPanel, "END");
        cardLayout.show(mainContainer, "END");
        this.revalidate();
        this.repaint();
    }

    public void pickRandomIt() {
        if (!modeChooser.getSelectedItem().equals("Server")) return;
        if (players.isEmpty()) return;
        if (players.size() < 2) return;
        if (gameOver) return;
        java.util.List<String> survivors = new java.util.ArrayList<>();
        for (Player p : players.values()) {
            if (p.isAlive) survivors.add(p.name);
        }
        if (survivors.size() > 1) {
            String newIt = survivors.get((int)(Math.random() * survivors.size()));
            if (ssm != null) {
                ssm.sendText("TAGGED:" + newIt);
            }
            players.get(newIt).isIt = true;
        }else if (survivors.size() == 1){
            winnerName = survivors.get(0);
            ssm.sendText("GAMEOVER:" + winnerName);
            gameOver = true;
            endGame();
        }
        }

    // Class
    class Player {
        int x, y, width = 40, height = 40;
        Color color;
        String name;
        boolean isIt = false;
        boolean isAlive = true;
        boolean isImmune = false;
        boolean isFrozen = false;

        public Player(int x, int y, Color color, String name) {
            this.x = x; this.y = y; this.color = color; this.name = name;
        }

        public void draw(Graphics g) {

            if(isFrozen) {
                g.setColor(new Color(0, 150, 255, 100)); 
                g.fillOval(x - 10, y - 10, width + 20, height + 20);
            }
        
            if (isImmune) {
                g.setColor(new Color(255, 215, 0, 100)); 
                g.fillOval(x - 5, y - 5, width + 10, height + 10);
            }

            if (isIt) {
                g.setColor(new Color(255, 50, 50, 100)); 
                g.fillOval(x - 10, y - 10, width + 30, height + 30);
                g.setFont(new Font("Arial", Font.BOLD, 25));
                g.setColor(Color.RED);
                if (bombTimer > 0){
                    g.setColor(Color.BLACK);
                    g.drawString(Integer.toString(bombTimer), x, y - 20);
                }
            }
                
            g.setColor(color);
            g.fillRect(x, y, width, height);
            
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            String displayName = name + (isIt ? " 💣" : "");
            g.drawString(displayName, x, y - 5);
        }
    }

    private class endPanel extends JPanel{

    public endPanel(){
        this.setPreferredSize(new Dimension(1280,720));
        this.setLayout(null);

        JButton returnBtn = new JButton("Return to Main Menu");
        returnBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        returnBtn.setFont(new Font("Serif", Font.PLAIN, 28));
        returnBtn.setForeground(new Color(200, 200, 200));
        returnBtn.setFocusPainted(false);
        returnBtn.setContentAreaFilled(false);
        returnBtn.setBorderPainted(false);
        returnBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        returnBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                returnBtn.setText("◈  " + "Return to Main Menu" + "  ◈");
                returnBtn.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                returnBtn.setText("Return to Main Menu");
                returnBtn.setForeground(new Color(200, 200, 200));
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                // Reset game state and return to menu
                if (ssm != null) {
                    ssm.disconnect();
                    ssm = null;
                }
                connectBtn.setVisible(true);
                connectBackBtn.setVisible(true);
                waitingLabel.setVisible(false); 
                gameActive = false;
                gameOver = false;
                gracePeriod = false;

                vy = 0;

                players.clear();
                usedSpawnPoints.clear();
                localPlayer = null;
                winnerName = "";

                bombCountdown.stop();
                graceCountdown.stop();
                cardLayout.show(mainContainer, "MENU");
                setContentPane(mainContainer);
                revalidate();
                repaint();
            }
        });
        
        returnBtn.setBounds(440, 500, 400, 50);
        
        this.add(returnBtn); 
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
    
        g.setColor(new Color(0, 0, 0, 220));
        g.fillRect(0, 0, 1280, 720);
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        String winText = "GAME OVER! WINNER: " + winnerName;
        int x = (1280 - g.getFontMetrics().stringWidth(winText)) / 2;
        g.drawString(winText, x, 360);
    }
}

    private class GamePanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw Map
            if (mapData[0][0] != null) {
                for (int r = 0; r < 16; r++) {
                    for (int c = 0; c < 16; c++) {
                        int x = c * 80;
                        int y = r * 45;
                        if (mapData[r][c] == null) continue;
                        
                        if (mapData[r][c].equals("bg") && groundBtm != null) g.drawImage(groundBtm, x, y, 80, 45, null);
                        else if (mapData[r][c].equals("tg") && groundTop != null) g.drawImage(groundTop, x, y, 80, 45, null);
                        else if (mapData[r][c].equals("bs") && skyBtm != null) g.drawImage(skyBtm, x, y, 80, 45, null);
                        else if (mapData[r][c].equals("ts") && skyTop != null) g.drawImage(skyTop, x, y, 80, 45, null);
                    }
                }
            }
            
            // Draw Players
            for (Player p : players.values()) {
                if(!p.isAlive) continue;
                p.draw(g);
            }

            if(gameActive && gracePeriod){
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 250, 1280, 150);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 50));
                 String text;
                if (graceTimer > 0) {
                    text = "STARTING IN " + graceTimer;
                } else {
                    text = "GO!";
                }
                
                // Center text logic
                FontMetrics metrics = g.getFontMetrics();
                int x = (1280 - metrics.stringWidth(text)) / 2;
                g.drawString(text, x, 340);
            }
        }
    }

    public static void main(String[] args) {
        new BOOMTAG();
    }
}