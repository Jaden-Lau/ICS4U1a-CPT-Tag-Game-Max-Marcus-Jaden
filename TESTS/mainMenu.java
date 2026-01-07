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
    JButton saveChangesButton = new JButton("Save Changes");
    JButton backBtn = new JButton("BACK TO MENU");
    JButton connectBtn = new JButton("CONNECT");
    String[] strcs = {"Client", "Server"};
    JComboBox csChooser = new JComboBox<>(strcs);
    JTextField IPAdressField = new JTextField("Enter IP Adress");
    Font newFont = new Font("Arial", Font.BOLD, 24);

    Color[] playerColors = {Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW};
    int currentColorIndex = 0;
    JPanel colorPreview = new JPanel();
    
    //Map Select Panel
    JButton map1Btn = new JButton("map1");
    JLabel map1NameLabel = new JLabel("Map1");
    JButton map2Btn = new JButton("map2");
    JLabel map2NameLabel = new JLabel("Map2");

    // Methods
    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == startGame) {
            frame.setContentPane(connectPanel);
        } else if (evt.getSource() == instruction) {
            frame.setContentPane(instructionsPanel);
        } else if (evt.getSource() == backBtn) {
            frame.setContentPane(mainMenuPanel);
        } else if (evt.getSource() == exit) {
            frame.dispose();
        } else if (evt.getSource() == map1Btn){

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

        saveChangesButton.setBounds(30, 620, 260, 50);
        connectPanel.add(saveChangesButton);

        // Right Side
        JLabel titleLabel = new JLabel("TAG GAME", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setBounds(350, 40, 850, 100);
        connectPanel.add(titleLabel);

        csChooser.setBounds(450, 200, 650, 60);
        connectPanel.add(csChooser);

        IPAdressField.setBounds(450, 300, 400, 50);
        connectPanel.add(IPAdressField);

        connectBtn.setFont(newFont);
        connectBtn.setBounds(450, 400, 650, 80);
        connectPanel.add(connectBtn);

        backBtn.setFont(newFont);
        backBtn.setBounds(450, 520, 650, 70);
        backBtn.addActionListener(this);
        connectPanel.add(backBtn);

        //Instructions Panel
        instructionsPanel.setLayout(null);
        instructionsPanel.setPreferredSize(new Dimension(1280, 720));

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
