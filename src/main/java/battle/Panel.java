package battle;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Panel extends JPanel {

    private String enemyURL = "/Assets/EnemiesSprites/ScalperIdle.gif";
    private String backgroundURL = "/Assets/HospitalHallway1.png";
    private String portraitURL = "/Assets/andrew.png"; // had to change for testing instead of the selection
    private Image background;
    private Image enemyAnim;
    private Image portrait;

    private int enemyHP = 100;
    private int maxHP = 100;
    private int playerHP = 100; // player's current health

    JButton suppressBtn;
    JButton protectBtn;
    JButton recoverBtn;

    // Added for Skill Menu
    JButton skill1Btn;
    JButton skill2Btn;
    JButton skill3Btn; // Added Skill 3
    JButton backBtn;

    // Linking your classes
    battle.Character playerCharacter = new battle.Character(100, 0.5);
    battle.Enemy enemyObject = new battle.Enemy(100, true);

    //JButton escalateBtn;

    public Panel() {
        setLayout(null);
        background = new ImageIcon(Objects.requireNonNull(getClass().getResource(backgroundURL))).getImage();
        enemyAnim = new ImageIcon(Objects.requireNonNull(getClass().getResource(enemyURL))).getImage();
        portrait = new ImageIcon(Objects.requireNonNull(getClass().getResource(portraitURL))).getImage();

        initButtons();
    }

//gi change

    private void initButtons() {
        suppressBtn = createActionBtn("SUPPRESS", "Attack enemy", new Color(60, 80, 150));
        protectBtn = createActionBtn("PROTECT", "Defend patient", new Color(160, 150, 60));
        recoverBtn = createActionBtn("RECOVER", "Manage heart", new Color(60, 150, 80));

        // New Skill Buttons
        skill1Btn = createActionBtn("PUNCH", "10-20 DMG", new Color(60, 80, 150));
        skill2Btn = createActionBtn("KICK", "20-30 DMG", new Color(160, 150, 60));
        skill3Btn = createActionBtn("SLAM", "30-45 DMG", new Color(60, 150, 80)); // Added Skill 3
        backBtn = createActionBtn("BACK", "Return", Color.DARK_GRAY);

        toggleSkills(false); // Hide skills by default

        suppressBtn.addActionListener(e -> {
            toggleMenu(false);   // Hide: Suppress, Protect, Recover, Escalate
            toggleSkills(true);  // Show Skills
        });

        // Skill 1 Logic
        skill1Btn.addActionListener(e -> {
            int damage = playerCharacter.skill1();
            applySkillDamage(damage);
        });

        // Skill 2 Logic
        skill2Btn.addActionListener(e -> {
            int damage = playerCharacter.skill2();
            applySkillDamage(damage);
        });

        // Skill 3 Logic (SLAM)
        skill3Btn.addActionListener(e -> {
            // Using a high damage range for the Slam move
            int damage = (int) (Math.random() * 16) + 30;
            applySkillDamage(damage);
        });

        // Back Logic
        backBtn.addActionListener(e -> {
            toggleSkills(false);
            toggleMenu(true);
        });

        // recover button
        recoverBtn.addActionListener(e -> {
            int heal = (int) (Math.random() * 11) + 15; // Random 15-25 HP
            playerHP = Math.min(maxHP, playerHP + heal);
            System.out.println("Stabilizing... HP: " + playerHP);
            repaint();

            if (enemyHP > 0) {
                startEnemyTimer();
            }
        });

        // 5. PROTECT Button Logic
        protectBtn.addActionListener(e -> {
            System.out.println("Bracing for impact...");
            playerCharacter.defend();
            if (enemyHP > 0) {
                startEnemyTimer();
            }
        });

        add(suppressBtn);
        add(protectBtn);
        add(recoverBtn);
        add(skill1Btn);
        add(skill2Btn);
        add(skill3Btn); // Added to panel
        add(backBtn);
    }

    // Helper for Skill Damage flow
    private void applySkillDamage(int damage) {
        enemyHP = Math.max(0, enemyHP - damage);
        repaint();
        toggleSkills(false);
        toggleMenu(true);

        if (enemyHP <= 0) {
            JOptionPane.showMessageDialog(this, "The patient has been suppressed. The hallway is quiet.");
            resetBattle();
        } else {
            startEnemyTimer();
        }
    }

    private void toggleSkills(boolean b) {
        if (skill1Btn != null) {
            skill1Btn.setVisible(b);
            skill2Btn.setVisible(b);
            skill3Btn.setVisible(b); // Added toggle
            backBtn.setVisible(b);
        }
    }

    private void toggleMenu(boolean b) {
        if (suppressBtn != null) {
            suppressBtn.setVisible(b);
            protectBtn.setVisible(b);
            recoverBtn.setVisible(b);
        }
    }

    private void startEnemyTimer() {
        Timer timer = new Timer(1000, event -> {
            enemyAttack();
        });
        timer.setRepeats(false);
        timer.start();
    }

//gi-change nako

    private void enemyAttack() {
        int damage = (int) (Math.random() * 21) + 25;
        playerHP = Math.max(0, playerHP - damage);
        repaint();

        // Check for death
        if (playerHP <= 0) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "The heart has stopped. Time of death: 2:28 AM.\nTry again?",
                    "FLATLINE", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                resetBattle(); // We will create this method next
            } else {
                System.exit(0); // Closes the game
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g; // used Graphics2D to match the background ui instead Graphics, basically more
        // smooth tan awn match sa ka andrew na ui
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(); // getting current width n height
        int h = getHeight();
        int hudY = h - 140;
        int sidePad = 25;
        int gap = 15;

        // gi section nako
        drawWorld(g2, w, h); // main area - center
        drawTopBar(g2, w, sidePad); // top
        drawHUD(g2, w, hudY, sidePad, gap); // bottom

        updateButtons(w, hudY, sidePad);
    }

    private void drawWorld(Graphics2D g, int w, int h) {
        if (background != null) {
            g.drawImage(background, 0, 0, w, h, null);
        }

        int ex = (w / 2) - 125;
        int ey = (h / 2) - 140;
        if (enemyAnim != null) {
            g.drawImage(enemyAnim, ex, ey, 250, 250, this);
        }

        // enemy bar health
        int barW = 220;
        int barH = 18;
        int bx = (w / 2) - (barW / 2);
        int by = ey - 30;

        // bar background - red
        g.setColor(new Color(60, 0, 0));
        g.fillRect(bx, by, barW, barH);

        // bar fill
        g.setColor(new Color(255, 60, 60));
        int currentBarW = (int) (barW * (enemyHP / (double) maxHP));
        g.fillRect(bx, by, currentBarW, barH);

        // gloss sa bar
        g.setColor(new Color(255, 255, 255, 40));
        g.fillRect(bx, by, barW, barH / 2);

        // outline
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1.5f)); // Modern thicker line
        g.drawRect(bx, by, barW, barH);

        // text sa bar
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        String hpLabel = enemyHP + " / " + maxHP;

        FontMetrics metrics = g.getFontMetrics();
        int textX = bx + (barW - metrics.stringWidth(hpLabel)) / 2;
        int textY = by + ((barH - metrics.getHeight()) / 2) + metrics.getAscent();

        g.setColor(new Color(0, 0, 0, 150));
        g.drawString(hpLabel, textX + 1, textY + 1);

        g.setColor(Color.WHITE);
        g.drawString(hpLabel, textX, textY);

        g.setStroke(new BasicStroke(1));
    }

    private void drawTopBar(Graphics2D g, int w, int pad) {
        g.setColor(new Color(0, 0, 0, 220));
        g.fillRect(0, 0, w, 45);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 20));
        g.drawString("THIRD FLOOR", pad, 30);
        g.drawString("2:22 AM", w - pad - 100, 30);
    }

    private void drawHUD(Graphics2D g, int w, int y, int pad, int gap) {

        // background sa hud
        g.setColor(new Color(5, 8, 12));
        g.fillRect(0, y, w, 140);
        g.setColor(new Color(45, 50, 55));
        g.drawLine(0, y, w, y);

        // portrait sa player
        g.drawImage(portrait, pad, y + 15, 100, 110, null);
        g.setColor(Color.GRAY);
        g.drawRect(pad, y + 15, 100, 110);

        // monitor <3
        int hrX = pad + 100 + gap;
        int hrY = y + 45;
        drawHeartMonitor(g, hrX, hrY);
    }

    private void drawHeartMonitor(Graphics2D g, int x, int y) {
        g.setColor(new Color(15, 18, 25));
        g.fillRoundRect(x, y, 175, 80, 5, 5);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 14));
        g.drawString("PATIENT: ANDREW", x, y - 13);

        g.setColor(new Color(100, 255, 200));
        int[] px = { x + 10, x + 45, x + 55, x + 65, x + 75, x + 85, x + 115, x + 130, x + 145, x + 165 };
        int[] py = { y + 30, y + 30, y + 10, y + 55, y + 30, y + 30, y + 30, y + 5, y + 65, y + 30 };
        g.drawPolyline(px, py, px.length);

        g.setFont(new Font("SansSerif", Font.BOLD, 36));
        //  g.drawString("90", x + 60, y + 50);

        if (playerHP < 30) {
            g.setColor(new Color(255, 0, 0)); // Turn the number Bright Red
            g.setFont(new Font("Monospaced", Font.BOLD, 12));
            g.drawString("CRITICAL STATE", x + 40, y + 20);

            // Re-set the big font for the HP number
            g.setFont(new Font("SansSerif", Font.BOLD, 36));
        } else {
            g.setColor(new Color(100, 255, 200)); // Keep it Teal
        }

        // gi change nako
        g.drawString(String.valueOf(playerHP), x + 60, y + 50); //gi change
    }

    private void updateButtons(int w, int y, int pad) {
        // Reduced button width slightly to fit 4 buttons in 800px frame
        int btnW = 100, btnGap = 10;
        int startX = w - pad - (btnW * 4 + btnGap * 3);

        if (suppressBtn != null) {
            suppressBtn.setBounds(startX, y + 45, btnW, 80);
            protectBtn.setBounds(startX + (btnW + btnGap), y + 45, btnW, 80);
            recoverBtn.setBounds(startX + (btnW + btnGap) * 2, y + 45, btnW, 80);
        }

        // Skill Menu Positioning
        if(skill1Btn != null) {
            skill1Btn.setBounds(startX, y + 45, btnW, 80);
            skill2Btn.setBounds(startX + (btnW + btnGap), y + 45, btnW, 80);
            skill3Btn.setBounds(startX + (btnW + btnGap) * 2, y + 45, btnW, 80);
            backBtn.setBounds(startX + (btnW + btnGap) * 3, y + 45, btnW, 80);
        }
    }

    // combat controls, template each button
    private JButton createActionBtn(String title, String sub, Color theme) {
        String label = "<html><center><font color='white' size='4'><b>" + title + "</b></font><br>" +
                "<font color='#bbbbbb' size='2'>" + sub + "</font></center></html>";
        JButton btn = new JButton(label);
        btn.setBackground(new Color(10, 12, 18));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(theme, 2));
        return btn;
    }

    public int getPlayerHP() {
        return playerHP;
    }

    public void setPlayerHP(int playerHP) {
        this.playerHP = playerHP;
    }

    // gi add
    private void resetBattle() {
        playerHP = 100;
        enemyHP = 100;
        System.out.println("The cycle restarts at 2:28 AM.");
        repaint();
    }
}