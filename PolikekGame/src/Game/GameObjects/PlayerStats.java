package Game.GameObjects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import Game.GameObject;
import Game.Sound;
import Game.Texture;

public class PlayerStats extends GameObject{
	private int health;
	private int reload;
	private int jumpTime;
	private int speed;
	private int damage;
	private Texture healthText;
	private Texture reloadText;
	private int maxHealth;
	private int maxReload;

	public PlayerStats(int x, int y, int width, int hight, Texture texture, Sound sound, int mass) {
		super(x, y, width, hight, texture, sound, mass);
		setHealth(100);
		setReload(20);
		setJumpTime(80);
		setSpeed(4);
		setDamage(10);
		setHealthText(null);
		setReloadText(null);
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
		this.maxHealth = health;
	}
	
	public void getDecHealth() {
		if(getHealth()>0) {
			this.health--; 
		}
	}
	
	public void getDecHealthMultiply(int size) {
		for(int i=0;i<size;i++) {
			getDecHealth();
		} 
	}
	
	public void getIncHealth() {
		if(getHealth()>0) {
			this.health++;
		}
	}
	
	public void getIncHealthMultiply(int size) {
		for(int i=0;i<size;i++) {
			getIncHealth();
		} 
	}

	public int getReload() {
		return reload;
	}

	public void setReload(int reload) {
		this.reload = reload;
		this.maxReload = reload;
	}
	
	public void getDecReload() {
		this.reload--; 
	}
	
	public void getDecReloadMultiply(int size) {
		for(int i=0;i<size;i++) {
			getDecReload();
		} 
	}
	
	public void getIncReload() {
		this.reload++; 
	}
	
	public void getIncReloadMultiply(int size) {
		for(int i=0;i<size;i++) {
			getIncReload();
		} 
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getJumpTime() {
		return jumpTime;
	}

	public void setJumpTime(int jumpTime) {
		this.jumpTime = jumpTime;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public Texture getHealthText() {
		return healthText;
	}

	public void setHealthText(Texture healthText) {
		this.healthText = healthText;
	}

	public Texture getReloadText() {
		return reloadText;
	}

	public void setReloadText(Texture reloadText) {
		this.reloadText = reloadText;
	}
	
	public Image getPlayerPanel() {
		float lfSize = (float) (health/maxHealth);
		float rldSize = (float) (reload/maxReload);
		int lSize = (int) (180*lfSize);
		int rSize = (int) (180*rldSize);
		System.out.println("Life "+lSize);
		
		ImageIcon panel = null;
		
		BufferedImage combined = new BufferedImage(200, 145, BufferedImage.TYPE_INT_ARGB);
		
		Graphics g = combined.getGraphics();
		
		g.setColor(Color.black);
		g.fillRect(0, 0, 200, 160);
		g.drawImage(this.getHealthText().getTexture().getImage(), 10, 15,lSize,50,null);
		g.drawImage(this.getReloadText().getTexture().getImage(), 10, 80,rSize,50,null);

		g.dispose();
		
		panel = new ImageIcon(combined);
		
		return panel.getImage();
	}
	
}
