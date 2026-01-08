package TESTS;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.*;

public class mainMenu implements ActionListener{
    // Properties
    JFrame frame = new JFrame("TAG Game");
    JPanel mainMenuPanel = new JPanel();
    JPanel instructionsPanel = new JPanel();
    JPanel connectPanel = new JPanel();
    JPanel mapSelectPanel = new JPanel();

    JPanel gamePanel = new GamePanel();
    SuperSocketMaster ssm = null;

    HashMap<String, Player> players = new HashMap<>();
    String myUsername;
    Timer gameTimer;

    Player localPlayer; 
    boolean[] keys = new boolean[256];
    int vy = 0;

    int bombTimer = 15; // 15 seconds
    Timer bombCountdown;
    int roundsPlayed = 0;
    boolean gameActive = false;

    //Main Menu
    JLabel title = new JLabel("TAG GAME");
    JButton startGame = new JButton("START");
    JButton instruction = new JButton("INSTRUCTIONS");
    JButton exit = new JButton("EXIT");

    //ConnectPanel
    JTextField username = new JTextField("Enter Username");
    JLabel SPCtextPanel1 = new JLabel("Select Player Color");
    JButton rightColorButton = new JButton(">");
    JButton leftColorButton = new JButton("<");
    JButton backBtn = new JButton("BACK TO MENU");
    JButton connectBtn = new JButton("CONNECT");
    String[] strcs = {"Client", "Server"};
    JComboBox csChooser = new JComboBox<>(strcs);
    JTextField IPAdressField = new JTextField("Enter IP Adress");
    Font newFont = new Font("Arial", Font.BOLD, 24);

    Color[] playerColors = {Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW};
    int currentColorIndex = 0;
    JPanel colorPreview = new JPanel();

    JLabel waitingLabel = new JLabel("WAITING FOR PLAYERS...");
    
    //Map Select Panel
    JButton map1Btn = new JButton("map1");
    JLabel map1NameLabel = new JLabel("Map1");
    JButton map2Btn = new JButton("map2");
    JLabel map2NameLabel = new JLabel("Map2");
    
    // Map Properties
    String[][] mapData = new String[16][16];
    BufferedImage groundImg = null;
    BufferedImage airImg = null;

    // Instructions
    JLabel instructionsTitle = new JLabel("HOW TO PLAY");
    JTextArea instructionsText = new JTextArea();
    JScrollPane instructionsScroll;
    JButton instructionsBackBtn = new JButton("BACK TO MENU");

    // Chat
    JLayeredPane layeredPane = new JLayeredPane();
    JPanel chatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JTextArea chatTextArea = new JTextArea("Press shift to close chat and enter to reopen it.");
    JScrollPane chatLabel = new JScrollPane(chatTextArea);
    JTextField chatTextField = new JTextField("Click here to type a message and press enter to send.");

    // Methods
    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == gameTimer) {
            handleMovement();
            gamePanel.repaint();
        }
        if (ssm != null && evt.getSource() == ssm) {
            String msg = ssm.readText();

           if (msg.startsWith("JOINED:")) {
                String[] data = msg.substring(7).split(",");
                String user = data[0];
                int colorIdx = Integer.parseInt(data[1]);
                
                players.put(user, new Player(100, 100, playerColors[colorIdx], user));
            }

            if (msg.startsWith("POS:")) {
                String[] data = msg.substring(4).split(",");
                String user = data[0];
                int newX = Integer.parseInt(data[1]);
                int newY = Integer.parseInt(data[2]);

                if (players.containsKey(user)) {
                    players.get(user).x = newX;
                    players.get(user).y = newY;
                } else {
                    players.put(user, new Player(newX, newY, Color.GRAY, user));
                }
            }

            if (msg.startsWith("TAGGED:")) {
                String target = msg.substring(7);
                for (Player p : players.values()) p.isIt = false;
                if (players.containsKey(target)) {
                    players.get(target).isIt = true;
                    bombTimer = 15; 
                }
            }

            if (msg.startsWith("EXPLODE:")) {
                String loser = msg.substring(8);
                System.out.println(loser + " went BOOM!");
            }

            if (msg.startsWith("CHATUSER")){
                String msgUser = msg.substring(8);
                chatTextArea.append("\n" + msgUser);
            }

            if (msg.startsWith("CHATTEXT")){
                String msgText = msg.substring(8);
                chatTextArea.append(msgText);
            }

            // SERVER SIDE
            if (csChooser.getSelectedItem().equals("Server")) {
                if (msg.equals("JOIN")) {
                    System.out.println("Client joined! Moving to Map Select.");
                    frame.setContentPane(mapSelectPanel);
                    frame.revalidate();
                    frame.repaint();
                }
            } 
            // CLIENT SIDE
            else {
                if (msg.equals("MAP:1") || msg.equals("MAP:2")) {
                    String mapFile = msg.equals("MAP:1") ? "map1.csv" : "map2.csv";
                    loadMap(mapFile);

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            myUsername = username.getText();
                            localPlayer = new Player(100, 100, playerColors[currentColorIndex], myUsername);
                            players.put(myUsername, localPlayer);
                            ssm.sendText("JOINED:" + myUsername + "," + currentColorIndex);

                            frame.setContentPane(layeredPane);
                            frame.requestFocusInWindow();
                            frame.revalidate();
                            frame.repaint();
                        }
                    });
                }
            }
        }

        if (evt.getSource() == map1Btn || evt.getSource() == map2Btn) {
            String mapFile = (evt.getSource() == map1Btn) ? "map1.csv" : "map2.csv";
            loadMap(mapFile);
            
            if (ssm != null) ssm.sendText(mapFile.equals("map1.csv") ? "MAP:1" : "MAP:2");

            myUsername = username.getText(); 
            localPlayer = new Player(100, 100, playerColors[currentColorIndex], myUsername);
            players.put(myUsername, localPlayer);

            if (ssm != null) {
                ssm.sendText("JOINED:" + myUsername + "," + currentColorIndex);
            }

            frame.setContentPane(layeredPane);
            frame.revalidate();
            frame.repaint();

            frame.requestFocusInWindow();
        }

        if (evt.getSource() == startGame) {
            frame.setContentPane(connectPanel);
        } else if (evt.getSource() == instruction) {
            frame.setContentPane(instructionsPanel);
        } else if (evt.getSource() == backBtn) {
            frame.setContentPane(mainMenuPanel);
        } else if (evt.getSource() == instructionsBackBtn) {
            frame.setContentPane(mainMenuPanel);
        } else if (evt.getSource() == exit) {
            frame.dispose();
        } else if (evt.getSource() == rightColorButton) {
            currentColorIndex++;
            if (currentColorIndex >= playerColors.length)
                currentColorIndex = 0;
            colorPreview.setBackground(playerColors[currentColorIndex]);
        } else if (evt.getSource() == leftColorButton) {
            currentColorIndex--;
            if (currentColorIndex < 0)
                currentColorIndex = playerColors.length - 1;
            colorPreview.setBackground(playerColors[currentColorIndex]);
        }
        
        else if (evt.getSource() == connectBtn) {
            if (csChooser.getSelectedItem().equals("Server")) {
                // Setup Server
                ssm = new SuperSocketMaster(1234, this);
                if (ssm.connect()) {
                    waitingLabel.setVisible(true);
                    connectBtn.setVisible(false);
                    backBtn.setVisible(false);
                    csChooser.setEnabled(false);
                }
            } else {
                // Setup Client
                String ip = IPAdressField.getText();
                ssm = new SuperSocketMaster(ip, 1234, this);
                if (ssm.connect()) {
                    // Client sends "JOIN" so Server knows to switch screens
                    ssm.sendText("JOIN"); 
                    
                    waitingLabel.setText("CONNECTED! WAITING FOR HOST...");
                    waitingLabel.setVisible(true);
                    connectBtn.setVisible(false);
                    backBtn.setVisible(false);
                }
            }
        } else if (evt.getSource() == csChooser) {
            if (csChooser.getSelectedItem().equals("Server")) {
                IPAdressField.setEnabled(false);
                IPAdressField.setText("Server mode - no IP needed");
            } else {
                IPAdressField.setEnabled(true);
                IPAdressField.setText("");
            }
        } else if (evt.getSource() == chatTextField){
            String strLine = chatTextField.getText();
            ssm.sendText("CHATUSER" + myUsername);
            ssm.sendText("CHATTEXT" + strLine);
            chatTextField.setText("");
            chatTextArea.append( "\n" + myUsername + ":" + strLine);   
            chatTextField.setFocusable(false);
        }
        
        frame.revalidate();
        frame.repaint();
    }

    public mainMenu(){
      // Main Menu
        mainMenuPanel.setLayout(null);
        mainMenuPanel.setPreferredSize(new Dimension(1280, 720));
        mainMenuPanel.add(title);
        mainMenuPanel.add(startGame);
        mainMenuPanel.add(instruction);
        mainMenuPanel.add(exit);
       
        //Main Menu Panel
        title.setFont(new Font("Arial", Font.BOLD, 150));
        title.setBounds(200, 100, 1000, 200);
        startGame.setFont(newFont);
        startGame.setBounds(320, 350, 600, 90);
        startGame.addActionListener(this);
        instruction.setFont(newFont);
        instruction.setBounds(320, 450, 600, 90);
        instruction.addActionListener(this);
        exit.setFont(newFont);
        exit.setBounds(320, 550, 600, 90);
        exit.addActionListener(this);
       
        //  Connect Panel
        connectPanel.setLayout(null);
        connectPanel.setPreferredSize(new Dimension(1280, 720));
        connectPanel.setBackground(new Color(220, 220, 220));

        // Left Sidebar
        username.setBounds(30, 30, 260, 45);
        connectPanel.add(username);

        SPCtextPanel1.setBounds(30, 90, 260, 30);
        connectPanel.add(SPCtextPanel1);

        leftColorButton.setBounds(30, 320, 40, 60);
        rightColorButton.setBounds(250, 320, 40, 60);

        leftColorButton.addActionListener(this);
        rightColorButton.addActionListener(this);

        connectPanel.add(leftColorButton);
        connectPanel.add(rightColorButton);

        colorPreview.setBounds(110, 300, 100, 100);
        colorPreview.setBackground(playerColors[currentColorIndex]);
        connectPanel.add(colorPreview);

        // Right Side
        JLabel titleLabel = new JLabel("TAG GAME", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setBounds(350, 40, 850, 100);
        connectPanel.add(titleLabel);

        csChooser.setBounds(450, 200, 650, 60);
        connectPanel.add(csChooser);
        csChooser.addActionListener(this);

        IPAdressField.setBounds(450, 300, 400, 50);
        connectPanel.add(IPAdressField);

        connectBtn.setFont(newFont);
        connectBtn.setBounds(450, 400, 650, 80);
        connectBtn.addActionListener(this);
        connectPanel.add(connectBtn);

        backBtn.setFont(newFont);
        backBtn.setBounds(450, 520, 650, 70);
        backBtn.addActionListener(this);
        connectPanel.add(backBtn);

        waitingLabel.setForeground(Color.BLACK);
        waitingLabel.setOpaque(true);
        waitingLabel.setBackground(new Color(200, 200, 200));
        waitingLabel.setFont(new Font("Arial", Font.BOLD, 36));
        waitingLabel.setBounds(450, 500, 650, 60);
        waitingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        waitingLabel.setVisible(false);

        connectPanel.add(waitingLabel);


        // Instructions Panel
        instructionsPanel.setLayout(null);
        instructionsPanel.setPreferredSize(new Dimension(1280, 720));
        instructionsPanel.setBackground(new Color(220, 220, 220));

        instructionsTitle.setFont(new Font("Arial", Font.BOLD, 72));
        instructionsTitle.setBounds(350, 30, 600, 90);
        instructionsPanel.add(instructionsTitle);

        instructionsText.setEditable(false);
        instructionsText.setLineWrap(true);
        instructionsText.setWrapStyleWord(true);
        instructionsText.setFont(new Font("Arial", Font.PLAIN, 22));

        instructionsText.setText(
            "GAMEPLAY:\n\n" +
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

        instructionsScroll = new JScrollPane(instructionsText);
        instructionsScroll.setBounds(200, 150, 880, 430);
        instructionsPanel.add(instructionsScroll);

        instructionsBackBtn.setFont(newFont);
        instructionsBackBtn.setBounds(390, 600, 500, 70);
        instructionsBackBtn.addActionListener(this);
        instructionsPanel.add(instructionsBackBtn);


        //Map Select Panel
        mapSelectPanel.setLayout(null);
        mapSelectPanel.setPreferredSize(new Dimension(1280,720));
        
        map1Btn.setBounds(200, 300, 300, 300);
        map1Btn.addActionListener(this);
        mapSelectPanel.add(map1Btn);
        map2Btn.setBounds(700, 300, 300, 300);
        map2Btn.addActionListener(this);
        mapSelectPanel.add(map2Btn);

        // Load the Tiles
        try {
            groundImg = ImageIO.read(new File("Map Tiles/ground.png"));
            airImg = ImageIO.read(new File("Map Tiles/air.png"));
        } catch (IOException e) {
            System.out.println("Error: Could not find image files in 'Map Tiles' folder.");
        }

        gameTimer = new Timer(1000/60, this);
        gameTimer.start();

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) { keys[e.getKeyCode()] = true; }
            @Override
            public void keyReleased(KeyEvent e) { keys[e.getKeyCode()] = false; }
        });
        frame.setFocusable(true);

        //Chat
        gamePanel.setBounds(0, 0, 1280, 720);
        chatPanel.setPreferredSize(new Dimension(200,200));
        chatTextArea.setEditable(false);
        chatTextArea.setLineWrap(true);
        chatPanel.add(chatTextArea);
        chatPanel.add(chatTextField);
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBounds(0, 600, 300, 120); // bottom-left overlay
        chatPanel.setBackground(new Color(0, 0, 0, 200)); // semi-transparent
        chatTextArea.setEditable(false);
        chatTextArea.setLineWrap(true);
        chatTextField.addActionListener(this);
        chatPanel.add(new JScrollPane(chatTextArea), BorderLayout.CENTER);
        chatPanel.add(chatTextField, BorderLayout.SOUTH);
        layeredPane.setPreferredSize(new Dimension(1280, 720));
        layeredPane.setLayout(null);
        layeredPane.add(gamePanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(chatPanel, JLayeredPane.PALETTE_LAYER);

        //Current Panel [MainMenuPanel]
        frame.setContentPane(mainMenuPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);   
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
            System.out.println("Successfully loaded: " + fileName);
        } catch (IOException e) {
            System.out.println("File Error: Could not find " + fileName + " in MAPS folder.");
        }
    }

    private class GamePanel extends JPanel {

        public GamePanel() {
            this.setPreferredSize(new Dimension(1280, 720));
            this.setLayout(null);
        }
        
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (mapData[0][0] != null) {
                for (int r = 0; r < 16; r++) {
                    for (int c = 0; c < 16; c++) {
                        int x = c * 80;
                        int y = r * 45;

                        if (mapData[r][c].equals("g")) {
                            g.drawImage(groundImg, x, y, 80, 45, null);
                        } else if (mapData[r][c].equals("a")) {
                            g.drawImage(airImg, x, y, 80, 45, null);
                        }
                    }
                }
            }
            if (players.size() > 0) {
                int screenWidth = 1280;
                int sectionWidth = screenWidth / players.size();
                int i = 0;
                g.setFont(new Font("Arial", Font.BOLD, 18));
                for (Player p : players.values()) {
                    int centerX = (i * sectionWidth) + (sectionWidth / 2);
                    g.setColor(p.color);
                    g.fillRect(centerX - 50, 10, 20, 20);
                    g.setColor(Color.BLACK);
                    g.drawString(p.name + ": " + p.score, centerX - 25, 27);
                    i++;
                }
            }
            for (Player p : players.values()) {
                p.draw(g);
            }
        }
    }

    class Player {
        int x, y, width = 40, height = 40;
        Color color;
        String name;
        int score = 0;
        boolean isIt = false;

        public Player(int x, int y, Color color, String name) {
            this.x = x; this.y = y; this.color = color; this.name = name;
        }

        public void draw(Graphics g) {
            // Draw Highlight if IT
            if (isIt) {
                g.setColor(new Color(255, 255, 0, 150));
                g.fillOval(x - 10, y - 10, width + 20, height + 20);
            }
            
            g.setColor(color);
            g.fillRect(x, y, width, height);
            
            g.setColor(Color.BLACK);
            String displayName = name + (isIt ? " 💣" : "");
            g.drawString(displayName, x, y - 5);
        }
    }

    private void handleMovement() {
        if (localPlayer == null) return;

        int speed = 5;
        int nextX = localPlayer.x;
        int nextY = localPlayer.y;

        // Left/Right Movement
        if (keys[KeyEvent.VK_A]) nextX -= speed;
        if (keys[KeyEvent.VK_D]) nextX += speed;

        // Horizontal Collision Check
        if (!isSolid(nextX, localPlayer.y) && !isSolid(nextX + 39, localPlayer.y + 39)) {
            localPlayer.x = nextX;
        }

        // Gravity & Jumping
        vy += 1;
        boolean onGround = isSolid(localPlayer.x, localPlayer.y + 41) || 
                   isSolid(localPlayer.x + 39, localPlayer.y + 41);
        if (keys[KeyEvent.VK_W] && onGround) {
            vy = -15;
        }

        // Vertical Collision Check
        int nextYWithGravity = localPlayer.y + vy;
        if (!isSolid(localPlayer.x, nextYWithGravity) && !isSolid(localPlayer.x + 39, nextYWithGravity + 39)) {
            localPlayer.y = nextYWithGravity;
        } else {
            vy = 0;
        }

        // Network Sync
        if (ssm != null) {
            ssm.sendText("POS:" + myUsername + "," + localPlayer.x + "," + localPlayer.y);
        }

        if (keys[KeyEvent.VK_SHIFT]){
            if(chatPanel.isVisible()){
                chatTextField.setFocusable(false);
                chatPanel.setVisible(false);
            }
        }
        if (keys[KeyEvent.VK_ENTER]){
            if(! chatPanel.isVisible()){
                chatTextField.setFocusable(true);
                chatPanel.setVisible(true);
            }
        }
    }

    public boolean isSolid(int pixelX, int pixelY) {
        int col = pixelX / 80;
        int row = pixelY / 45;
        if (row < 0 || row >= 16 || col < 0 || col >= 16) return true;
        return mapData[row][col].equals("g");
    }
    
    private void checkCollisions() {
        if (localPlayer == null || !localPlayer.isIt) return;

        for (Player other : players.values()) {
            if (other == localPlayer) continue;

            // Rectangle Collision
            Rectangle myRect = new Rectangle(localPlayer.x, localPlayer.y, 40, 40);
            Rectangle otherRect = new Rectangle(other.x, other.y, 40, 40);

            if (myRect.intersects(otherRect)) {
                // Transfer Bomb
                localPlayer.isIt = false;
                ssm.sendText("TAGGED:" + other.name);
                
                // Knockback
                applyKnockback(other);
            }
        }
    }

    private void applyKnockback(Player other) {
        // Launch opposite directions
        if (localPlayer.x < other.x) {
            localPlayer.x -= 50;
        } else {
            localPlayer.x += 50;
        }
        ssm.sendText("POS:" + myUsername + "," + localPlayer.x + "," + localPlayer.y);
    }


    public static void main(String[] args) {
        new mainMenu();
    }
}
