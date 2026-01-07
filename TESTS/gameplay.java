package TESTS;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class gameplay implements ActionListener{
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
      }
    }
  }
  
  public gameplay(){
    gamePanel = new JGraphics();
    gamePanel.setLayout(null);
    gamePanel.setPreferredSize(new Dimension(1280, 720));
    // SuperSocketMaster server mode
    ssm = new SuperSocketMaster(6112, this);
    ssm.connect();
  } 
}