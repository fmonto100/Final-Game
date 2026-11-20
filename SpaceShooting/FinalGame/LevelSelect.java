package FinalGame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class LevelSelect extends JPanel {
	private static final long serialVersionUID = 1L;
	private JFrame frame;
	private int unlockedLevel = 1;
	private int coins = 302;
	
	
	//Animation variables
	private Timer animTimer;
	private double planetAngle = 0;
	private double pulsePhase = 0;
	
	// Fixed starfield positions
	private int[] starX = new int[55];
	private int[] starY = new int[55];
	
	// Coordinates for the 4 zigzag level 
	private Point[] levelNodes = {
			new Point(245, 470), // Level 1
			new Point(110, 355),
			new Point(195, 255),
			new Point(255, 115),
			new Point(155, 38)
	};
	
	public LevelSelect(JFrame frame) {
		this.frame = frame;
		setPreferredSize(new Dimension(400, 600));
		setBackground(new Color(14, 5, 28));
		
		// Generate random background stars once
		Random rand = new Random(42);
		for (int i = 0; i < starX.length; i++) {
			starX[i] = rand.nextInt(400);
			starY[i] = rand.nextInt(600);
		}
		
		// Start 30ms animation loop for planet rotation and node glow
		animTimer = new Timer(30, e -> {
			planetAngle += .015;
			pulsePhase += .1;
			repaint();
		});
		animTimer.start();
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Point click = e.getPoint();
				
				// 1. check top-right green Back button
				if (new Rectangle(350, 10, 36, 26).contains(click)) {
					goBackToStart();
					return;
				}
				
				// 2. Check if user clicked an unlocked level node
				for (int i = 0; i < levelNodes.length; i++) {
					int levelNum = i + 1;
					if (levelNum <= unlockedLevel && click.distance(levelNodes[i]) <= 34) {
						launchLevel(levelNum);
						break;
					}
				}
			}
		});
	}
	
	private void goBackToStart() {
		if (animTimer != null) animTimer.stop();
		StartScreen start = new StartScreen(frame);
		frame.getContentPane().removeAll();
		frame.add(start);
		frame.revalidate();
		frame.repaint();
	}
	
	private void launchLevel (int level) {
		if (animTimer != null) animTimer.stop();
		SpaceShooter gamePanel = new SpaceShooter(level);
		frame.getContentPane().removeAll();
		frame.add(gamePanel);
		frame.revalidate();
		frame.repaint();
		gamePanel.requestFocusInWindow();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		try {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
	// 1. Nebula, Stars, Red Moon & Rotating Planet
		drawSpaceBackground(g2);
		
	// 2. Multi-Layered Glowing Cyan Path
		drawGlowingPath(g2);
		
		
		// 3. Draw Each level Node
		for (int i = 0; i < levelNodes.length; i++) {
			drawNode(g2, levelNodes[i].x, levelNodes[i].y, i + 1);
		}
		
		// 4. Draw Top HUD Bar (Score pill + Back Button)
		drawTopBar(g2);
		} catch (Exception ex) {
			System.out.println("Crash: " + ex);
			ex.printStackTrace();
		}
	}
	
	private void drawSpaceBackground(Graphics2D g2) {
		GradientPaint nebula = new GradientPaint(50, 100, new Color(95, 22, 115), 350, 550, new Color(16, 6, 34));
		g2.setPaint(nebula);
		g2.fillRect(0, 0, 400, 600);
		
		// Soft pink nebula cloud glow behind left-centered
		RadialGradientPaint nebulaCloud = new RadialGradientPaint(
			new Point2D.Float(130, 220), 180f,
			new float[] {0f, 1f},
			new Color[] {new Color(220, 70, 180, 95), new Color(220, 70, 180, 0)}
		);
		g2.setPaint(nebulaCloud);
		g2.fillOval(-50, 40, 360, 360);
		
		// Twinkling starts
		g2.setColor(new Color(255, 255, 255, 190));
		for (int i = 0; i < starX.length; i++) {
			int size = (i % 3 == 0) ? 3 : 2;
			g2.fillOval(starX[i], starY[i], size, size);
		}
		
		// Small reddish-orange moon on the bottom-left
		RadialGradientPaint moonPaint = new RadialGradientPaint(
				new Point2D.Float(102, 415), 32f,
				new float[] {0f, 1f},
				new Color[] {new Color(220, 85, 65), new Color(75, 20, 25)}
			);
		g2.setPaint(moonPaint);
		g2.fillOval(92, 405, 44, 44);
		
		// Large Rotating Purple Planet
		int px = 145, py = 325, pSize = 230;
		Shape oldClip = g2.getClip();
		Ellipse2D planetShere = new Ellipse2D.Double(px, py, pSize, pSize);
		
		// Atmospheric rim glow around the planet
		g2.setColor(new Color(190, 110, 255, 70));
		g2.setStroke(new BasicStroke(8f));
		g2.draw(planetShere);
		
		// Clip inside the planet
		g2.setClip(planetShere);
		
		// 3D Lit Planet Base
		RadialGradientPaint planetLit = new RadialGradientPaint(
				new Point2D.Float(px + 65, py + 55), pSize * 0.85f,
				new float[] {0f, 0.65f, 1f},
				new Color[] {new Color(185, 125, 225), new Color(85, 35, 120), new Color(22, 8, 40)}
			);
			g2.setPaint(planetLit);
			g2.fill(planetShere);
			
			// Rotating surface craters/bands moving horizontally across the sphere
			g2.setColor(new Color(40, 14, 65, 110));
			for (int c = 0; c < 6; c++) {
				double angle = planetAngle + c * (Math.PI / 3);
				double offsetX = Math.sin(angle) * (pSize * 0.42);
				double visibleScale = Math.cos(angle);
				
				if (visibleScale > -0.2) {
					int craterW = (int) (38 * Math.max(0.25, visibleScale));
					int craterH = 26 + (c % 3) * 8;
					int cx = (int) (px + pSize / 2.0 + offsetX - craterW / 2.0);
					int cy = py + 45 + (c * 28);
					g2.fillOval(cx, cy, craterW, craterH);
				}
			}
			
			RadialGradientPaint shadow = new RadialGradientPaint(
				new Point2D.Float(px + 60, py + 50), pSize * 0.9f,
				new float[] {0.55f, 1f},
				new Color[] {new Color(0, 0 , 0, 0), new Color(10, 2, 20, 195)}
			);
			g2.setPaint(shadow);
			g2.fill(planetShere);
		
			g2.setClip(oldClip);
		}
	
	private void drawGlowingPath(Graphics2D g2) {
		for (int i = 0; i < levelNodes.length - 1; i++) {
			int x1 = levelNodes[i].x, y1 = levelNodes[i].y;
			int x2 = levelNodes[i + 1].x, y2 = levelNodes[i + 1].y;
			
			// Outer soft cyan aura
			g2.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2.setColor(new Color(0, 220, 255, 55));
			g2.drawLine(x1, y1, x2, y2);
			
			// Mid cyan glow
			g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2.setColor(new Color(40, 245, 255, 140));
			g2.drawLine(x1, y1, x2, y2);
			
			// Bright white-cyan core
			g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2.setColor(new Color(220, 255, 255));
			g2.drawLine(x1, y1, x2, y2);
		}
	}
	
	private void drawNode(Graphics2D g2, int cx, int cy, int level) {
		boolean isUnlocked = level <= unlockedLevel;
		boolean isCurrent = (level == unlockedLevel);
		boolean isBoss = (level == 4);
		
		// Pulsing gold halo on the active/current
		if (isCurrent) {
			float pulse = (float) (Math.sin(pulsePhase) * 4.0);
			int haloR = (int) (34 + pulse);
			g2.setColor(new Color(255, 210, 40, 65));
			g2.fillOval(cx - haloR, cy - haloR, haloR * 2, haloR * 2);
			
			g2.setColor(new Color(255, 220, 60, 220));
			g2.setStroke(new BasicStroke(2.5f));
			g2.drawOval(cx - haloR, cy - haloR, haloR * 2, haloR * 2);
		}
		
		// Theme colors per node type
		Color coreColor = isUnlocked ? new Color(35, 165, 45) 
						: (isBoss ? new Color(120, 35, 165) : new Color(20, 95, 195));
		Color glowColor = isUnlocked ? new Color(110, 255, 110) 
						: (isBoss ? new Color(215, 120, 255) : new Color(70, 210, 255));
		
		// Soft outer glow behind the circle
		g2.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 75));
		g2.fillOval(cx - 28, cy - 28, 56, 56);
		
		// Inner filled circle
		g2.setColor(coreColor);
		g2.fillOval(cx - 21, cy - 21, 42, 42);
		
		// Segment 4-arc sci-fi tech border
		g2.setColor(glowColor);
		g2.setStroke(new BasicStroke(3.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		for (int a = 0; a < 4; a++) {
			g2.drawArc(cx - 23, cy - 23, 46, 46, a * 90 + 12, 66);
		}
		
		// Center icon or number
		g2.setColor(Color.WHITE);
		if (isUnlocked) {
			g2.setFont(new Font("Arial", Font.BOLD, 20));
			String num = String.valueOf(level);
			FontMetrics fm = g2.getFontMetrics();
			g2.drawString(num, cx - fm.stringWidth(num) / 2, cy + 7);
		} else if (isBoss) {
			// Boss skull + lock icon for Level 4
			g2.fillOval(cx - 8, cy - 14, 16, 12);
			g2.fillRect(cx -5, cy - 4, 10, 5);
			g2.setColor(coreColor);
			g2.fillOval(cx - 5, cy - 10, 3, 3);
			g2.fillOval(cx + 2, cy - 10, 3, 3);
			g2.setColor(Color.WHITE);
			g2.fillRoundRect(cx - 7, cy + 2, 14, 11, 3, 3);
		} else {
			// Padlock icon for locked Levels
			g2.fillRoundRect(cx - 8, cy - 2, 16, 13, 4, 4);
			g2.setStroke(new BasicStroke(2.5f));
			g2.drawArc(cx - 5, cy - 10, 10, 12, 0, 180);
		}
	}
	
	private void drawTopBar(Graphics2D g2) {
		g2.setColor(new Color(0, 150, 230));
		g2.fillRect(0, 0, 400, 48);
		
		// Draw blue coin counter pill in center
		g2.setColor(new Color(10,60,130));
		g2.fillRoundRect(150, 12, 100, 26, 12, 12);
		g2.setColor(Color.WHITE);
		g2.drawString(String.valueOf(coins), 180, 30);
		
		// Green Back arrow button
		g2.setColor(new Color(50, 200, 50));
		g2.fillRoundRect(350, 12, 36, 26, 8, 8);
		g2.setColor(Color.WHITE);
		g2.drawString("<-", 361, 29);
	}	
}
