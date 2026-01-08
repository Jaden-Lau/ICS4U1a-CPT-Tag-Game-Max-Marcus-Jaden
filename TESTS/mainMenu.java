package TESTS;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class mainMenu implements ActionListener{
    // Properties
    JFrame frame = new JFrame("TAG Game");
    JPanel mainMenuPanel = new JPanel();
    JPanel instructionsPanel = new JPanel();
    JPanel connectPanel = new JPanel();
    JPanel mapSelectPanel = new JPanel();
    SuperSocketMaster ssm = null;

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

    // Instructions
    JLabel instructionsTitle = new JLabel("HOW TO PLAY");
    JTextArea instructionsText = new JTextArea();
    JScrollPane instructionsScroll;
    JButton instructionsBackBtn = new JButton("BACK TO MENU");


    // Methods
    @Override
    public void actionPerformed(ActionEvent evt) {
        if (ssm != null && evt.getSource() == ssm) {
            String msg = ssm.readText();

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
                if (msg.startsWith("MAP:")) {
                    String chosenMap = msg.substring(4);
                    System.out.println("Server chose: " + chosenMap);
                }
            }
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
        } else if (evt.getSource() == map1Btn) {
            if (ssm != null) ssm.sendText("MAP:1");
            System.out.println("Starting Map 1...");
        } else if (evt.getSource() == map2Btn) {
            if (ssm != null) ssm.sendText("MAP:2");
            System.out.println("Starting Map 2...");
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

        //Current Panel [MainMenuPanel]
        frame.setContentPane(mainMenuPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);   
    }
    public static void main(String[] args) {
        new mainMenu();
    }
}
