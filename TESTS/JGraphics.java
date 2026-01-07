package TESTS;

import java.awt.*;
import javax.swing.*;

public class JGraphics extends JPanel{
    //Variables
    static int playerX;
    static int playerY;
    
    
    // Repainting screen based on network input
    // 
    public void paintComponent(Graphics g){
      g.clearRect(0, 0, 1280, 720);
      g.setColor(Color.BLUE);
      g.fillRect(playerX, playerX, 100, 100);
    }

    public JGraphics(){
      super(); 
    }
  }
