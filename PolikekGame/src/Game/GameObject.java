package Game;

public class GameObject {
	private String objectName;
	private int width;
	private int hight;
	private int x;
	private int y;
	private Texture texture;
	private Sound sound;
	private int mass;
	private boolean collision;
	private boolean isPlayer;
	
	public GameObject(int x, int y, int width,int hight, Texture texture, Sound sound, int mass) {
		this.width=width;
		this.hight=hight;
		this.x=x;
		this.y=y;
		this.texture=texture;
		this.sound=sound;
		this.mass=mass;
		this.isPlayer=false;
		this.collision=true;
	}
	
	public String getObjectName() {
		return objectName;
	}
	
	public void setObjectName(String objectName) {
		this.objectName=objectName;
	}
	
	public int getWidth() {
		return width;
	}
	
	public void setWidth(int width) {
		this.width=width;
	}
	
	public int getHight() {
		return hight;
	}
	
	public void setHight(int hight) {
		this.hight=hight;
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x=x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y=y;
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public void setTexture(Texture texture) {
		this.texture=texture;
	}
	
	public Sound getSound() {
		return sound;
	}
	
	public void setSound(Sound sound) {
		this.sound=sound;
	}
	
	public int getMass() {
		return mass;
	}
	
	public void setMass(int mass) {
		this.mass=mass;
	}
	
	public boolean getCollision() {
		return collision;
	}
	
	public void setCollision(boolean collision) {
		this.collision=collision;
	}
	
	public boolean getIsPlayer() {
		return isPlayer;
	}
	
	public void setIsPlayer(boolean isPlayer) {
		this.isPlayer=isPlayer;
	}
}
