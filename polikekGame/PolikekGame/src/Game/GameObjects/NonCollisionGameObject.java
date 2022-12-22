package Game.GameObjects;

import Game.GameObject;
import Game.Sound;
import Game.Texture;

public class NonCollisionGameObject extends GameObject {

	public NonCollisionGameObject(int x, int y, int width, int hight, Texture texture, Sound sound, int mass) {
		super(x, y, width, hight, texture, sound, mass);
		
		this.setCollision(false);
	}
	
}
