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
       
        //Connect Panel
        connectPanel.setLayout(null);
        connectPanel.setPreferredSize(new Dimension(1280, 720));
        
        //Username
        username.setBounds(50, 100, 225, 50);
        connectPanel.add(username);
        
        //SPC
        SPCtextPanel1.setBounds(110, 150, 200, 50);
        connectPanel.add(SPCtextPanel1);
        
        //Left and Right color
        rightColorButton.setBounds(250, 300, 25, 50);
        connectPanel.add(rightColorButton);
        leftColorButton.setBounds(50, 300, 25, 50);
        connectPanel.add(leftColorButton);
        
        
        //Save Changes
        saveChangesButton.setBounds(75, 650, 200, 50);
        connectPanel.add(saveChangesButton);

        //Client server button
        csChooser.setBounds(500, 300, 300, 50);
        connectPanel.add(csChooser);

        //Connect button
        connectBtn.setBounds(500, 600, 700, 100);
        connectPanel.add(connectBtn);

        //IP Adress Enter
        IPAdressField.setBounds(500, 500, 400, 50);
        connectPanel.add(IPAdressField);
    

        //Back Button
        backBtn.setFont(newFont);
        backBtn.setBounds(500, 650, 700, 50);
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
