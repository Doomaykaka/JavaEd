package Game.GameObjects;

import Game.GameObject;
import Game.Sound;
import Game.Texture;

public class PlayerGameObject extends GameObject {
	private boolean inJump;
	private int jumpTime;

	public PlayerGameObject(int x, int y, int width, int hight, Texture texture, Sound sound, int mass) {
		super(x, y, width, hight, texture, sound, mass);

		this.setIsPlayer(true);
		
		inJump=false;
		jumpTime=0;
	}
	
	public boolean getInJump() {
		return inJump;
	}
	
	public void setInJump(boolean inJump) {
		this.inJump=inJump;
	}
	
	public int getJumpTime() {
		return jumpTime;
	}
	
	public void setJumpTime(int jumpTime) {
		this.jumpTime=jumpTime;
	}
	
}
