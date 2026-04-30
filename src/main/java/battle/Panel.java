package battle;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Objects;

public class Panel extends JPanel {
    private final main.GamePanel gp;

    private Image background, enemyAnim, enemyAttackAnim, portrait;
    private int enemyHP = 200, maxEnemyHP = 200;
    private boolean isEnemyAttacking = false;
    private int enemyXOffset = 0;

    private boolean isProcessing = false;
    private boolean victoryTransitionActive = false;
    private int victoryFadeAlpha = 0;
    private boolean defeatTransitionActive = false;
    private int defeatFadeAlpha = 0;

    private JButton suppressBtn, protectBtn, recoverBtn, skill1Btn, skill2Btn, skill3Btn, backBtn;
    private JButton action1Btn, action2Btn, action3Btn;
    private JButton item1Btn, item2Btn, item3Btn;

    private Character player;
    private Enemy enemy;
    private final BattleLauncher.BattleResultListener resultListener;

    public Panel(main.GamePanel gp, Character player, Enemy enemy, BattleLauncher.BattleResultListener resultListener) {
        this.gp = gp;
        this.player = player;
        this.enemy = enemy;
        this.resultListener = resultListener;
        this.enemyHP = enemy.getHealth();
        this.maxEnemyHP = enemy.getHealth();

        setLayout(null);

        background = new ImageIcon(Objects.requireNonNull(getClass().getResource("/Assets/HospitalHallway1.png")))
                .getImage();
        portrait = new ImageIcon(Objects.requireNonNull(getClass().getResource("/Assets/andrew.png"))).getImage();
        enemyAnim = new ImageIcon(Objects.requireNonNull(getClass().getResource(enemy.getIdleURL()))).getImage();
        enemyAttackAnim = new ImageIcon(Objects.requireNonNull(getClass().getResource(enemy.getAttackURL())))
                .getImage();

        initButtons();
    }

    private void initButtons() {
        suppressBtn = createActionBtn("SUPPRESS", "Attack enemy", new Color(60, 80, 150));
        protectBtn = createActionBtn("ACTION", "Select an action", new Color(160, 150, 60));
        recoverBtn = createActionBtn("ITEM", "Use an item", new Color(60, 150, 80));

        skill1Btn = createActionBtn(player.skills.get(0).getSkillName(),
                player.skills.get(0).getFloorDMG() + "-" + player.skills.get(0).getCeilDMG() + "DMG",
                new Color(60, 80, 150));
        skill2Btn = createActionBtn(player.skills.get(1).getSkillName(),
                player.skills.get(1).getFloorDMG() + "-" + player.skills.get(1).getCeilDMG() + "DMG",
                new Color(160, 150, 60));
        skill3Btn = createActionBtn(player.skills.get(2).getSkillName(),
                player.skills.get(2).getFloorDMG() + "-" + player.skills.get(2).getCeilDMG() + "DMG",
                new Color(60, 150, 80));

        action1Btn = createActionBtn(player.actions.get(0).getName(), "", new Color(160, 150, 60));
        action2Btn = createActionBtn(player.actions.get(1).getName(), "", new Color(160, 150, 60));
        action3Btn = createActionBtn(player.actions.get(2).getName(), "", new Color(160, 150, 60));

        item1Btn = createActionBtn(player.getItem(0).getName(), player.getItem(0).getQuantity() + "x",
                new Color(40, 100, 100));
        item2Btn = createActionBtn(player.getItem(1).getName(), player.getItem(1).getQuantity() + "x",
                new Color(40, 100, 100));
        item3Btn = createActionBtn(player.getItem(2).getName(), player.getItem(2).getQuantity() + "x",
                new Color(40, 100, 100));

        backBtn = createActionBtn("BACK", "Return", Color.DARK_GRAY);

        toggleSkills(false);
        toggleProtectActions(false);
        toggleItems(false);

        suppressBtn.addActionListener(e -> {
            if (isProcessing)
                return;
            toggleMenu(false);
            toggleSkills(true);
        });

        protectBtn.addActionListener(e -> {
            if (isProcessing)
                return;
            toggleMenu(false);
            toggleProtectActions(true);
        });

        recoverBtn.addActionListener(e -> {
            if (isProcessing)
                return;
            toggleMenu(false);
            toggleItems(true);
        });

        backBtn.addActionListener(e -> {
            toggleSkills(false);
            toggleProtectActions(false);
            toggleItems(false);
            toggleMenu(true);
        });

        item1Btn.addActionListener(e -> useBattleItem(0));
        item2Btn.addActionListener(e -> useBattleItem(1));
        item3Btn.addActionListener(e -> useBattleItem(2));

        skill1Btn.addActionListener(e -> {
            if (!isProcessing)
                applyPlayerAction(player.useSkill(0));
        });
        skill2Btn.addActionListener(e -> {
            if (!isProcessing)
                applyPlayerAction(player.useSkill(1));
        });
        skill3Btn.addActionListener(e -> {
            if (!isProcessing)
                applyPlayerAction(player.useSkill(2));
        });

        action1Btn.addActionListener(e -> {
            if (!isProcessing) {
                isProcessing = true;
                player.useAction(0, this);
                finalizeAction();
            }
        });
        action2Btn.addActionListener(e -> {
            if (!isProcessing) {
                isProcessing = true;
                player.useAction(1, this);
                finalizeAction();
            }
        });
        action3Btn.addActionListener(e -> {
            if (!isProcessing) {
                isProcessing = true;
                player.useAction(2, this);
                finalizeAction();
            }
        });

        add(suppressBtn);
        add(protectBtn);
        add(recoverBtn);

        add(skill1Btn);
        add(skill2Btn);
        add(skill3Btn);

        add(action1Btn);
        add(action2Btn);
        add(action3Btn);

        add(item1Btn);
        add(item2Btn);
        add(item3Btn);

        add(backBtn);
    }

    private void useBattleItem(int index) {
        if (isProcessing)
            return;

        if (player.getItem(index).isUsable()) {
            isProcessing = true;
            player.useItem(index);

            refreshItemButtonText();
            toggleItems(false);
            toggleMenu(true);
            repaint();

            if (!checkDeath("Cardiac Arrest")) {
                startEnemyTimer();
            }
        } else {
            JOptionPane.showMessageDialog(this, "You are out of " + player.getItem(index).getName() + "!");
        }
    }

    private void finalizeAction() {
        toggleProtectActions(false);
        toggleMenu(true);
        repaint();
        startEnemyTimer();
    }

    private void applyPlayerAction(int damage) {
        isProcessing = true;
        if (gp.isOneShotModeEnabled()) {
            damage = Math.max(damage, maxEnemyHP);
        }
        enemyHP = Math.max(0, enemyHP - damage);
        player.resetResistance();
        toggleSkills(false);
        toggleProtectActions(false);
        toggleMenu(true);
        repaint();

        if (enemyHP <= 0) {
            JOptionPane.showMessageDialog(this, "The enemy has been suppressed.");
            startVictoryTransition();
        } else {
            startEnemyTimer();
        }
    }

    private void startVictoryTransition() {
        victoryTransitionActive = true;
        Timer timer = new Timer(30, null);
        timer.addActionListener(e -> {
            victoryFadeAlpha = Math.min(255, victoryFadeAlpha + 20);
            repaint();

            if (victoryFadeAlpha >= 255) {
                timer.stop();
                resultListener.onBattleWon(player);
            }
        });
        timer.start();
    }

    private void startDefeatTransition() {
        defeatTransitionActive = true;
        Timer timer = new Timer(30, null);
        timer.addActionListener(e -> {
            defeatFadeAlpha = Math.min(255, defeatFadeAlpha + 20);
            repaint();

            if (defeatFadeAlpha >= 255) {
                timer.stop();
                resultListener.onBattleLost(player);
            }
        });
        timer.start();
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
            JOptionPane.showMessageDialog(this, "The patient has died. " + cause, "FLATLINE",
                    JOptionPane.WARNING_MESSAGE);
            startDefeatTransition();
            return true;
        }
        return false;
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
        drawVictoryTransition(g2, w, h);
        drawDefeatTransition(g2, w, h);
    }

    private void drawWorld(Graphics2D g, int w, int h) {
        if (background != null)
            g.drawImage(background, 0, 0, w, h, null);
        int ex = (w / 2) - 125, ey = (h / 2) - 140;
        if (isEnemyAttacking)
            g.drawImage(enemyAttackAnim, ex + enemyXOffset + 60, ey, 250, 250, this);
        else
            g.drawImage(enemyAnim, ex, ey, 250, 250, this);
        int barW = 220, barH = 18, bx = (w / 2) - 110, by = ey - 30;
        g.setColor(new Color(60, 0, 0));
        g.fillRect(bx, by, barW, barH);
        g.setColor(new Color(255, 60, 60));
        g.fillRect(bx, by, (int) (barW * (enemyHP / (double) maxEnemyHP)), barH);
        g.setColor(Color.WHITE);
        g.drawRect(bx, by, barW, barH);
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
        g.setColor(new Color(5, 8, 12));
        g.fillRect(0, y, w, 140);
        g.setColor(new Color(45, 50, 55));
        g.drawLine(0, y, w, y);
        if (portrait != null)
            g.drawImage(portrait, pad, y + 15, 100, 110, null);
        drawHeartMonitor(g, pad + 100 + gap, y + 45);
    }

    private void drawHeartMonitor(Graphics2D g, int x, int y) {
        g.setColor(new Color(15, 18, 25));
        g.fillRoundRect(x, y, 175, 80, 5, 5);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 14));
        g.drawString("PATIENT: " + player.getName(), x, y - 13);
        g.setColor(new Color(100, 255, 200));
        int[] px = { x + 10, x + 45, x + 55, x + 65, x + 75, x + 85, x + 115, x + 130, x + 145, x + 165 };
        int[] py = { y + 30, y + 30, y + 10, y + 55, y + 30, y + 30, y + 30, y + 5, y + 65, y + 30 };
        g.drawPolyline(px, py, px.length);
        int bpm = player.getHeartBeat();
        if (bpm < 60) {
            g.setColor(Color.cyan);
        } else if (bpm < 100) {
            g.setColor(new Color(0, 200, 120));
        } else if (bpm < 140) {
            g.setColor(new Color(255, 140, 0));
        } else {
            g.setColor(new Color(200, 40, 40));
        }
        g.setFont(new Font("SansSerif", Font.BOLD, 36));
        g.drawString(String.valueOf(bpm), x + 60, y + 50);
    }

    private void updateButtons(int w, int y, int pad) {
        int btnW = 100, btnGap = 10;
        JButton[] mBtns = { suppressBtn, protectBtn, recoverBtn };
        JButton[] sBtns = { skill1Btn, skill2Btn, skill3Btn, backBtn };
        JButton[] pBtns = { action1Btn, action2Btn, action3Btn, backBtn };
        JButton[] iBtns = { item1Btn, item2Btn, item3Btn, backBtn };

        layoutVisibleButtons(mBtns, w, y, pad, btnW, btnGap);
        layoutVisibleButtons(sBtns, w, y, pad, btnW, btnGap);
        layoutVisibleButtons(pBtns, w, y, pad, btnW, btnGap);
        layoutVisibleButtons(iBtns, w, y, pad, btnW, btnGap);
    }

    private void layoutVisibleButtons(JButton[] buttons, int panelWidth, int y, int pad, int btnW, int btnGap) {
        int visibleCount = 0;
        for (JButton button : buttons) {
            if (button != null && button.isVisible()) {
                visibleCount++;
            }
        }

        if (visibleCount == 0) {
            return;
        }

        int totalWidth = (visibleCount * btnW) + ((visibleCount - 1) * btnGap);
        int startX = panelWidth - pad - totalWidth;
        int visibleIndex = 0;

        for (JButton button : buttons) {
            if (button != null && button.isVisible()) {
                button.setBounds(startX + (visibleIndex * (btnW + btnGap)), y + 45, btnW, 80);
                visibleIndex++;
            }
        }
    }

    private JButton createActionBtn(String title, String sub, Color theme) {
        String label = "<html><center><font color='white' size='4'><b>" + title + "</b></font><br>"
                + "<font color='#bbbbbb' size='2'>" + sub + "</font></center></html>";
        JButton btn = new JButton(label);
        btn.setBackground(new Color(10, 12, 18));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(theme, 2));

        btn.setForeground(Color.WHITE);
        return btn;
    }

    private void drawVictoryTransition(Graphics2D g2, int w, int h) {
        if (!victoryTransitionActive) {
            return;
        }

        g2.setColor(new Color(0, 0, 0, victoryFadeAlpha));
        g2.fillRect(0, 0, w, h);
    }

    private void drawDefeatTransition(Graphics2D g2, int w, int h) {
        if (!defeatTransitionActive) {
            return;
        }

        g2.setColor(new Color(0, 0, 0, defeatFadeAlpha));
        g2.fillRect(0, 0, w, h);
    }

    private void toggleSkills(boolean b) {
        skill1Btn.setVisible(b);
        skill2Btn.setVisible(b);
        skill3Btn.setVisible(b);
        backBtn.setVisible(b);
    }

    private void toggleItems(boolean b) {
        item1Btn.setVisible(b);
        item2Btn.setVisible(b);
        item3Btn.setVisible(b);
        backBtn.setVisible(b);
    }

    private void toggleProtectActions(boolean b) {
        action1Btn.setVisible(b);
        action2Btn.setVisible(b);
        action3Btn.setVisible(b);
        backBtn.setVisible(b);
    }

    private void toggleMenu(boolean b) {
        suppressBtn.setVisible(b);
        protectBtn.setVisible(b);
        recoverBtn.setVisible(b);
    }

    public void refreshButtonText() {
        skill1Btn.setText("<html><center><font color='white'><b>" + player.skills.get(0).getSkillName()
                + "</b></font><br>" + "<font color='white' size='2'>" + player.skills.get(0).getFloorDMG() + "-"
                + player.skills.get(0).getCeilDMG() + "DMG</font></center></html>");

        skill2Btn.setText("<html><center><font color='white'><b>" + player.skills.get(1).getSkillName()
                + "</b></font><br>" + "<font color='white' size='2'>" + player.skills.get(1).getFloorDMG() + "-"
                + player.skills.get(1).getCeilDMG() + "DMG</font></center></html>");
        repaint();
    }

    public void refreshItemButtonText() {
        item1Btn.setText("<html><center><font color='white'><b>" + player.getItem(0).getName() + "</b></font><br>"
                + "<font color='#bbbbbb' size='2'>" + player.getItem(0).getQuantity() + "x</font></center></html>");
        item2Btn.setText("<html><center><font color='white'><b>" + player.getItem(1).getName() + "</b></font><br>"
                + "<font color='#bbbbbb' size='2'>" + player.getItem(1).getQuantity() + "x</font></center></html>");
        item3Btn.setText("<html><center><font color='white'><b>" + player.getItem(2).getName() + "</b></font><br>"
                + "<font color='#bbbbbb' size='2'>" + player.getItem(2).getQuantity() + "x</font></center></html>");
    }
}
