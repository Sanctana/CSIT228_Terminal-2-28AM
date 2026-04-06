package battle;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Panel extends JPanel {

    private Image background, enemyAnim, enemyAttackAnim, portrait;
    private int enemyHP = 200, maxEnemyHP = 200;
    private boolean isEnemyAttacking = false;
    private int enemyXOffset = 0;

    private boolean isProcessing = false;

    private JButton suppressBtn, protectBtn, recoverBtn, skill1Btn, skill2Btn, skill3Btn, backBtn;
    private JButton action1Btn, action2Btn, action3Btn;

    private Character player;
    private Enemy enemy;

    public Panel(Character player, Enemy enemy) {
        this.player = player;
        this.enemy = enemy;
        this.enemyHP = enemy.getHealth();
        this.maxEnemyHP = enemy.getHealth();

        setLayout(null);

        background = new ImageIcon(Objects.requireNonNull(getClass().getResource("/Assets/HospitalHallway1.png"))).getImage();
        portrait = new ImageIcon(Objects.requireNonNull(getClass().getResource("/Assets/andrew.png"))).getImage();
        enemyAnim = new ImageIcon(Objects.requireNonNull(getClass().getResource(enemy.getIdleURL()))).getImage();
        enemyAttackAnim = new ImageIcon(Objects.requireNonNull(getClass().getResource(enemy.getAttackURL()))).getImage();

        initButtons();
    }

    private void initButtons() {
        suppressBtn = createActionBtn("SUPPRESS", "Attack enemy", new Color(60, 80, 150));
        protectBtn = createActionBtn("ACTION", "Select an action", new Color(160, 150, 60));
        recoverBtn = createActionBtn("ITEM", "Use an item", new Color(60, 150, 80));

        skill1Btn = createActionBtn(player.skills.get(0).getSkillName(), player.skills.get(0).getFloorDMG() + "-" + player.skills.get(0).getCeilDMG() + "DMG", new Color(60, 80, 150));
        skill2Btn = createActionBtn(player.skills.get(1).getSkillName(), player.skills.get(1).getFloorDMG() + "-" + player.skills.get(1).getCeilDMG() + "DMG", new Color(160, 150, 60));
        skill3Btn = createActionBtn(player.skills.get(2).getSkillName(), player.skills.get(2).getFloorDMG() + "-" + player.skills.get(2).getCeilDMG() + "DMG", new Color(60, 150, 80));

        action1Btn = createActionBtn(player.actions.get(0).getName(), "", new Color(160, 150, 60));
        action2Btn = createActionBtn(player.actions.get(1).getName(), "", new Color(160, 150, 60));
        action3Btn = createActionBtn(player.actions.get(2).getName(), "", new Color(160, 150, 60));

        backBtn = createActionBtn("BACK", "Return", Color.DARK_GRAY);

        toggleSkills(false);
        toggleProtectActions(false);

        suppressBtn.addActionListener(e -> {
            if (isProcessing) return;
            toggleMenu(false);
            toggleSkills(true);
        });

        protectBtn.addActionListener(e -> {
            if (isProcessing) return;
            toggleMenu(false);
            toggleProtectActions(true);
        });

        backBtn.addActionListener(e -> {
            toggleSkills(false);
            toggleProtectActions(false);
            toggleMenu(true);
        });

        skill1Btn.addActionListener(e -> {
            if(!isProcessing) applyPlayerAction(player.skills.get(0).getDamage());
        });
        skill2Btn.addActionListener(e -> {
            if(!isProcessing) applyPlayerAction(player.skills.get(1).getDamage());
        });
        skill3Btn.addActionListener(e -> {
            if(!isProcessing) applyPlayerAction(player.skills.get(2).getDamage());
        });
        action1Btn.addActionListener(e ->{
            if(!isProcessing){
                isProcessing = true;

                player.setResistance(player.actions.get(0).action());
                toggleProtectActions(false);
                toggleMenu(true);
                startEnemyTimer();

            }
        });
        action2Btn.addActionListener(e ->{
            if(!isProcessing){
                isProcessing = true;

                player.setResistance(player.actions.get(1).action());
                toggleProtectActions(false);
                toggleMenu(true);
                startEnemyTimer();

            }
        });
        action3Btn.addActionListener(e ->{
            if(!isProcessing){
                isProcessing = true;

                player.setResistance(player.actions.get(2).action());
                toggleProtectActions(false);
                toggleMenu(true);
                startEnemyTimer();

            }
        });

        recoverBtn.addActionListener(e -> {
            if (isProcessing) return;
            isProcessing = true;
            int heal = (int) (Math.random() * 11) + 15;
            player.recover(heal);
            repaint();

            if (!checkDeath("Cardiac Arrest")) {
                startEnemyTimer();
            }
        });

        add(suppressBtn); add(protectBtn); add(recoverBtn);
        add(skill1Btn); add(skill2Btn); add(skill3Btn);
        add(action1Btn); add(action2Btn); add(action3Btn);
        add(backBtn);
    }

    private void applyPlayerAction(int damage) {
        isProcessing = true;
        enemyHP = Math.max(0, enemyHP - damage);
        player.resetResistance();
        toggleSkills(false);
        toggleProtectActions(false);
        toggleMenu(true);
        repaint();

        if (enemyHP <= 0) {
            JOptionPane.showMessageDialog(this, "The enemy has been suppressed.");
            resetBattle();
            isProcessing = false;
        } else {
            startEnemyTimer();
        }
    }

    private void startEnemyTimer() {
        Timer timer = new Timer(1000, e -> enemyAttack());
        timer.setRepeats(false);
        timer.start();
    }

    private void enemyAttack() {
        if (enemyAttackAnim != null) {
            enemyAttackAnim.flush();
        }

        isEnemyAttacking = true;
        enemyXOffset = -60;
        repaint();

        Timer damageTimer = new Timer(600, e -> {
            player.takeDamage(enemy.skill());
            repaint();
            checkDeath("Cardiac Event");
        });
        damageTimer.setRepeats(false);
        damageTimer.start();

        Timer resetTimer = new Timer(2000, e -> {
            isEnemyAttacking = false;
            enemyXOffset = 0;
            isProcessing = false;

            player.resetResistance();
            if (enemyAnim != null) {
                enemyAnim.flush();
            }
            repaint();
        });
        resetTimer.setRepeats(false);
        resetTimer.start();
    }

    private boolean checkDeath(String cause) {
        if (!player.getIsAlive()) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "The heart has stopped. " + cause + "\nTry again?",
                    "FLATLINE", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) resetBattle();
            else System.exit(0);
            return true;
        }
        return false;
    }

    private void resetBattle() {
        player.setHeartBeat(70);
        enemyHP = maxEnemyHP;
        isProcessing = false;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        drawWorld(g2, w, h);
        drawTopBar(g2, w, 25);
        drawHUD(g2, w, h - 140, 25, 15);
        updateButtons(w, h - 140, 25);
    }

    private void drawWorld(Graphics2D g, int w, int h) {
        if (background != null) g.drawImage(background, 0, 0, w, h, null);
        int ex = (w / 2) - 125, ey = (h / 2) - 140;
        if (isEnemyAttacking) g.drawImage(enemyAttackAnim, ex + enemyXOffset + 60, ey, 250, 250, this);
        else g.drawImage(enemyAnim, ex, ey, 250, 250, this);
        int barW = 220, barH = 18, bx = (w / 2) - 110, by = ey - 30;
        g.setColor(new Color(60, 0, 0)); g.fillRect(bx, by, barW, barH);
        g.setColor(new Color(255, 60, 60));
        g.fillRect(bx, by, (int) (barW * (enemyHP / (double) maxEnemyHP)), barH);
        g.setColor(Color.WHITE); g.drawRect(bx, by, barW, barH);
    }

    private void drawTopBar(Graphics2D g, int w, int pad) {
        g.setColor(new Color(0, 0, 0, 220)); g.fillRect(0, 0, w, 45);
        g.setColor(Color.WHITE); g.setFont(new Font("Monospaced", Font.BOLD, 20));
        g.drawString("THIRD FLOOR", pad, 30); g.drawString("2:22 AM", w - pad - 100, 30);
    }

    private void drawHUD(Graphics2D g, int w, int y, int pad, int gap) {
        g.setColor(new Color(5, 8, 12)); g.fillRect(0, y, w, 140);
        g.setColor(new Color(45, 50, 55)); g.drawLine(0, y, w, y);
        if (portrait != null) g.drawImage(portrait, pad, y + 15, 100, 110, null);
        drawHeartMonitor(g, pad + 100 + gap, y + 45);
    }

    private void drawHeartMonitor(Graphics2D g, int x, int y) {
        g.setColor(new Color(15, 18, 25)); g.fillRoundRect(x, y, 175, 80, 5, 5);
        g.setColor(Color.WHITE); g.setFont(new Font("Monospaced", Font.BOLD, 14));
        g.drawString("PATIENT: " + player.getName(), x, y - 13);
        g.setColor(new Color(100, 255, 200));
        int[] px = { x+10, x+45, x+55, x+65, x+75, x+85, x+115, x+130, x+145, x+165 };
        int[] py = { y+30, y+30, y+10, y+55, y+30, y+30, y+30, y+5, y+65, y+30 };
        g.drawPolyline(px, py, px.length);
        int bpm = player.getHeartBeat();
        g.setColor( (bpm < 50 || bpm > 170) ? Color.RED : new Color(100, 255, 200));
        g.setFont(new Font("SansSerif", Font.BOLD, 36));
        g.drawString(String.valueOf(bpm), x + 60, y + 50);
    }

    private void updateButtons(int w, int y, int pad) {
        int btnW = 100, btnGap = 10;
        int startX = w - pad - (btnW * 4 + btnGap * 3);
        JButton[] mBtns = {suppressBtn, protectBtn, recoverBtn};
        JButton[] sBtns = {skill1Btn, skill2Btn, skill3Btn, backBtn};
        JButton[] pBtns = {action1Btn, action2Btn, action3Btn, backBtn};

        for (int i = 0; i < 3; i++) if(mBtns[i]!=null) mBtns[i].setBounds(startX + (i*(btnW+btnGap)), y+45, btnW, 80);
        for (int i = 0; i < 4; i++) if(sBtns[i]!=null) sBtns[i].setBounds(startX + (i*(btnW+btnGap)), y+45, btnW, 80);
        for (int i = 0; i < 4; i++) if(pBtns[i]!=null) pBtns[i].setBounds(startX + (i*(btnW+btnGap)), y+45, btnW, 80);
    }

    private JButton createActionBtn(String title, String sub, Color theme) {
        String label = "<html><center><font color='white' size='4'><b>" + title + "</b></font><br>" +
                "<font color='#bbbbbb' size='2'>" + sub + "</font></center></html>";
        JButton btn = new JButton(label);
        btn.setBackground(new Color(10, 12, 18));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(theme, 2));
        return btn;
    }

    private void toggleSkills(boolean b) {
        skill1Btn.setVisible(b); skill2Btn.setVisible(b); skill3Btn.setVisible(b); backBtn.setVisible(b);
    }

    private void toggleProtectActions(boolean b) {
        action1Btn.setVisible(b); action2Btn.setVisible(b); action3Btn.setVisible(b); backBtn.setVisible(b);
    }

    private void toggleMenu(boolean b) {
        suppressBtn.setVisible(b); protectBtn.setVisible(b); recoverBtn.setVisible(b);
    }
}