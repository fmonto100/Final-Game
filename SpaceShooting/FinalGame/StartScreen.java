package FinalGame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.Timer;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class StartScreen extends JPanel {
	private static final long serialVersionUID = 2002830237432588230L;
	private JFrame frame;
	private Image playerImg;
	private Timer animTimer;
	private float fontSize = 24f;
	private float pulseSpeed = .25f;
	
	
	public StartScreen(JFrame frame) {
		this.frame = frame; 
		setPreferredSize(new Dimension(400, 600));
		setBackground(Color.BLACK);
		
		// Load existing fighter ship image
		try {
			playerImg = ImageIO.read(new File("image/fighter.png"));
		} catch (Exception e) {
			System.out.println("Could not load preview ship image.");
		}
		
		// Switch to SpaceShooter when clicked
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				startGame();
			}
		});
				
		animTimer = new Timer(30, e -> {
			fontSize += pulseSpeed;
			if (fontSize >= 29f || fontSize <= 22f) {
				pulseSpeed = -pulseSpeed;
			}
			repaint();
		});
		animTimer.start();
		}		
	
	private void startGame() {
		if (animTimer != null) {
			animTimer.stop();
		}
	
		LevelSelect mapPanel = new LevelSelect(frame);
		frame.getContentPane().removeAll();
		frame.add(mapPanel);
		frame.revalidate();
		frame.repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Title Text
		g.setColor(Color.YELLOW);
		g.setFont(new Font("Arial", Font.BOLD, 32));
		g.drawString("GALAXY ATTACK", 65, 140);
		
		g.setColor(Color.CYAN);
		g.setFont(new Font("Arial", Font.BOLD, 22));
		g.drawString("ALIEN SHOOTER", 105, 175);
		
		// Center Fighter Preview
		if (playerImg != null) {
			g.drawImage(playerImg, 150, 230, 100,100, null);
		}
		
		// Click Prompt
		g.setColor(Color.WHITE);
		Font pulseFont = new Font("Arial", Font.BOLD, Math.round(fontSize));
		g.setFont(pulseFont);
		
		String prompt = "Tap To Play";
		FontMetrics metrics = g.getFontMetrics(pulseFont);
		int textX = (getWidth() - metrics.stringWidth(prompt)) / 2;
		g.drawString(prompt, textX, 420);
		
		// Level Badge
		g.setColor(new Color(0, 120, 215));
		g.fillRoundRect(160,  450, 80, 28, 10, 10);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.BOLD, 14));
		g.drawString("Lv 1", 185, 469);
		
	}

}
