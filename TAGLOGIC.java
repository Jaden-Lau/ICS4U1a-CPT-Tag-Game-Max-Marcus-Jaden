import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import javax.swing.*;

public class TAGLOGIC extends JPanel implements ActionListener {
    HashMap<String, Player> players = new HashMap<>();
    String itPlayerName = ""; 
    Timer gameTimer;
    long lastPointGainTime = System.currentTimeMillis();

    public TAGLOGIC() {
        players.put("Player1", new Player(100, 100, Color.BLUE, "Player1"));
        players.put("Player2", new Player(300, 100, Color.RED, "Player2"));
        
        itPlayerName = "Player1"; 
        
        gameTimer = new Timer(1000/60, this);
        gameTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        checkCollisions(); 
        updatePoints();
        repaint();
    }

    private void checkCollisions() {
        if (itPlayerName.equals("")) return;

        Player it = players.get(itPlayerName);
        Rectangle itBounds = new Rectangle(it.x, it.y, it.width, it.height);

        for (Player p : players.values()) {
            if (p.name.equals(itPlayerName)) continue;

            Rectangle pBounds = new Rectangle(p.x, p.y, p.width, p.height);

            if (itBounds.intersects(pBounds)) {
                System.out.println(itPlayerName + " tagged " + p.name + "!");
                itPlayerName = p.name;
                break;
            }
        }
    }

    private void updatePoints() {
        if (System.currentTimeMillis() - lastPointGainTime >= 1000) {
            for (Player p : players.values()) {
                if (!p.name.equals(itPlayerName)) {
                    p.score++;
                }
            }
            lastPointGainTime = System.currentTimeMillis();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Player p : players.values()) {
            if (p.name.equals(itPlayerName)) {
                g.setColor(Color.BLACK);
                g.drawRect(p.x - 2, p.y - 2, p.width + 4, p.height + 4);
                g.drawString("IT", p.x + 15, p.y - 20);
            }
            p.draw(g);
        }
    }

    class Player {
        int x, y, width = 40, height = 40, score = 0;
        Color color;
        String name;

        public Player(int x, int y, Color color, String name) {
            this.x = x; this.y = y; this.color = color; this.name = name;
        }

        public void draw(Graphics g) {
            g.setColor(color);
            g.fillRect(x, y, width, height);
            g.setColor(Color.BLACK);
            g.drawString(name + " (Score: " + score + ")", x, y - 5);
        }
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Tag Logic Test");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new TAGLOGIC());
        f.setSize(1280, 720);
        f.setVisible(true);
    }
}