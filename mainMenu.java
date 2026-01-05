import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.*; 

public class mainMenu implements ActionListener{
    // Properties

    JFrame frame = new JFrame("Tag Game");
    JPanel mainMenuPanel = new JPanel();
    JPanel instructionsPanel = new JPanel();
    JPanel connectPanel = new JPanel();
    
    JLabel title = new JLabel("TAG GAME");
    JTextField IPAdressField = new JTextField();
    JButton startGame = new JButton("START");
    JButton instruction = new JButton("Instructions");
    JButton exit = new JButton("Exit");
    String[] strcs = {"Client", "Server"};
    JComboBox csChooser = new JComboBox<>(strcs);

    // Methods
    public void actionPerformed(ActionEvent evt){
      if(evt.getSource() == startGame){
        System.out.println("BOOM START");
      } else if (evt.getSource() == instruction){
        System.out.println("BOOM INSTRUCTIONS");
      } else if (evt.getSource() == exit){
        System.out.println("close game");
      }
    }


    public mainMenu(){
      // Main Menu
      mainMenuPanel.setLayout(null);
      mainMenuPanel.setSize(new Dimension(1280,720));
      mainMenuPanel.add(title);
      mainMenuPanel.add(startGame);
      mainMenuPanel.add(instruction);
      mainMenuPanel.add(exit);

      title.setBounds(320, 100, 640, 100);
      startGame.setBounds(400, 300, 100, 50);
      startGame.addActionListener(this);
      instruction.setBounds(400, 400, 100, 50);
      instruction.addActionListener(this);
      exit.setBounds(400, 500, 100, 50);
      exit.addActionListener(this);
      
      connectPanel.setLayout(null);
      connectPanel.setSize(new Dimension(1280, 720));
      connectPanel.add(IPAdressField);
      
      
    
      
      frame.setContentPane(mainMenuPanel);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setResizable(false);
      frame.pack();
    }
}

// Main Method
    public void main(String[] args) {
      new mainMenu();
    }

