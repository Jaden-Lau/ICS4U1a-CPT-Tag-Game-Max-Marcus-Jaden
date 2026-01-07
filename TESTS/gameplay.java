package TESTS;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class gameplay implements ActionListener{
  JFrame frame = new JFrame();
  JGraphics gamePanel;
  Timer thetimer;
  String strNetText;
  
  // Super Socket Master
  SuperSocketMaster ssm;
  
  public void actionPerformed(ActionEvent evt){
    if(evt.getSource() == thetimer){
     gamePanel.repaint(); 
      
    }else if(evt.getSource() == ssm){
      strNetText = ssm.readText();
      if(strNetText.equals("a")){
        JGraphics.playerX -= 1;
      }else if(strNetText.equals("d")){
        JGraphics.playerX += 1;
      }
    }
  }
  public void keyPressed(KeyEvent evt) {
       if(evt.getKeyChar() == 'a'){
            ssm.sendText("a");
       }else if(evt.getKeyChar() == 'd'){
            ssm.sendText("d");
       }
    }

  
  public gameplay(){
    gamePanel = new JGraphics();
    gamePanel.setLayout(null);
    gamePanel.setPreferredSize(new Dimension(1280, 720));
    frame.setContentPane(gamePanel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.pack();
    frame.setVisible(true);
  } 

  public static void main (String[] args){
    new gameplay();
  }
}