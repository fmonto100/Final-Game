package FinalGame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class SpaceShooter extends JPanel implements ActionListener, KeyListener {
	private static final long serialVersionUID = 1L;
	private int playerX = 180;
	private ArrayList<double[]> lasers = new ArrayList<>();
	private ArrayList<Point> enemies = new ArrayList<>();
	private boolean isShooting = false;
	private int shootCooldown;
	private Timer timer;
	private int enemySpeed = 2;
	private Image playerImg;
	private Image enemyImg;
	
	public SpaceShooter() {
		setPreferredSize(new Dimension(400, 600));
		setBackground(Color. BLACK);
		setFocusable(true);
		addKeyListener(this);
		loadImage();
		
		// Spawn a row of enemies
		int row;
		int rows = 5;
		int spacingX = 60;
		int spacingY = 45;
		for (row = 0; row < rows; row++) {
			int enemiesInRow = rows - row;
			int rowWidth = (enemiesInRow - 1) * spacingX;
			int startX = (400 - rowWidth) / 2 - 15;
			
			int col;
			for (col = 0; col < enemiesInRow; col++) {
				enemies.add(new Point(startX + col * spacingX, 40 + row * spacingY));
			}
		}
		
		//Run game loop at 60
		timer = new Timer(10, this);
		timer.start();
		
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Draw player Ship
		if (playerImg != null) {
			g.drawImage(playerImg, playerX - 25,515, 50,50, this);
		} else {
			g.setColor(Color.CYAN);
			int[] xPoints = {playerX, playerX - 15, playerX + 15};
			int[] yPoints = {520, 550, 550};
			g.fillPolygon(xPoints, yPoints, 3);
			}
		
		
		// Draw Glowing Blue Lasers
		for (double[] b : lasers) {
			int bx = (int) b[0];
			int by = (int) b[1];
			
			// Outer blue glow
			g.setColor(new Color(0, 140, 255, 160));
			g.fillOval(bx - 5, by, 10, 22);
			
			// Bright cyan/white core
			g.setColor(new Color(180, 245, 255));
			g.fillOval(bx - 2, by + 3, 4, 15);
			
		}
		
		// Draw Enemies
		for (Point enemy : enemies) {
			if (enemyImg != null) {
				g.drawImage(enemyImg, enemy.x, enemy.y, 40, 40, this);
			} else {
				g.setColor(Color.GREEN);
				g.fillRect(enemy.x, enemy.y, 30, 30);
			}
		}
	}
			
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// Move laser upward
		int laserCount;
		if (isShooting && shootCooldown <= 0) {
			lasers.add(new double[] {playerX - 18, 520,-1.5, -12}); // Far left angled
			lasers.add(new double[] {playerX - 7, 515, -0.4, -12}); // Inner left
			lasers.add(new double[] {playerX + 7, 515, 0.4, -12}); // Inner right
			lasers.add(new double[] {playerX + 18, 520, 1.5, -12}); // Far right angled
			shootCooldown = 4; //Lower = faster stream ( try 3 to 5)
		}
		if (shootCooldown > 0) {
			shootCooldown--;
		}
		for (laserCount = 0; laserCount < lasers.size(); laserCount++) {
			double[] b = lasers.get(laserCount);
			b[0] += b[2];
			b[1] += b[3];
			if (b[1] < 0) {
				lasers.remove(laserCount--);
			}
			// Move enemies side-to-side
			boolean hitEdge = false;
			for (Point enemy : enemies) {
				enemy.x += enemySpeed;
				if (enemy.x <= 10 || enemy.x >= 360) {
					hitEdge = true;
				}
			}
			// Reverse direction and drop down when hitting a wall
			if (hitEdge) {
				enemySpeed = -enemySpeed;
				for (Point enemy : enemies) {
					enemy.y += 20;
				}
			}
		}
		
		// Check for collisions between lasers and enemies
		int del;
		for (del = 0; del < lasers.size() - 1; del++) {
			Rectangle laserRect = new Rectangle ((int) lasers.get(del)[0] - 2, (int) lasers.get(del)[1], 6, 18);
			int col;
			for (col = 0; col < enemies.size(); col++) {
				Rectangle enemyRect = new Rectangle(enemies.get(col).x, enemies.get(col).y, 30, 30);
				if (laserRect.intersects(enemyRect) ) {
					lasers.remove(del);
					enemies.remove(col);
					del--;
					break;
				}
			}	
		}
		repaint();
	}
	
	public void loadImage() {
		try {
			this.playerImg = ImageIO.read(new File("Image/fighter.png"));
			this.enemyImg = ImageIO.read(new File("Image/enemy.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT && playerX > 20) playerX -= 15;
		if (e.getKeyCode() == KeyEvent.VK_RIGHT && playerX < 380) playerX += 15;
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			isShooting = true;
		}
		
	}

	@Override public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			isShooting = false;
		}
	}
	@Override public void keyTyped(KeyEvent e) {}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Alien Shooter");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new SpaceShooter());
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
