package battle;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Panel extends JPanel{
    private String enemyURL = "/Assets/EnemiesSprites/ScalperIdle.gif";
    private String backgroundURL = "/Assets/HospitalHallway1.png";
    private String selectionPanel = "/Assets/Selection.png";
    private Image background;
    private Image enemyAnim;
    private Image selection;

    public Panel(){
        background = new ImageIcon(Objects.requireNonNull(getClass().getResource(backgroundURL))).getImage();
        enemyAnim = new ImageIcon(Objects.requireNonNull(getClass().getResource(enemyURL))).getImage();
        selection = new ImageIcon(Objects.requireNonNull(getClass().getResource(selectionPanel))).getImage();
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(background,0,0,800,500,null);
        g.drawImage(enemyAnim,276,165,this);
        g.drawImage(selection,0,0,null);
    }
}
