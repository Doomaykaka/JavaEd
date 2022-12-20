package Game.GameObjects;

import Game.GameObject;
import Game.Sound;
import Game.Texture;

public class Enemy extends GameObject{
	private int health;
	private int reload;
	private int speed;
	private int damage;
	private String finalStateMachineMap;
	private int frameNumber;
	private long timer;
	
	public Enemy(int x, int y, int width, int hight, Texture texture, Sound sound, int mass) {
		super(x, y, width, hight, texture, sound, mass);
		setHealth(100);
		setReload(20);
		setSpeed(4);
		setDamage(10);
		setFrameNumber(0);
		timer=0;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getReload() {
		return reload;
	}

	public void setReload(int reload) {
		this.reload = reload;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public String getFinalStateMachineMap() {
		return finalStateMachineMap;
	}

	public void setFinalStateMachineMap(String finalStateMachineMap) {
		this.finalStateMachineMap = finalStateMachineMap;
	}

	public int getFrameNumber() {
		return frameNumber;
	}

	public void setFrameNumber(int frameNumber) {
		this.frameNumber = frameNumber;
	}
	
	public PlayerStats getFinalStateMachineLoop(PlayerGameObject playerObject, PlayerStats playerStat) {
		System.out.println("X = "+playerObject.getX()+" Y = "+playerObject.getY());
		System.out.println("Enemy X = "+this.getX()+" Enemy Y = "+this.getY());
		int frameNumberCount = 0;
		String[] actions = finalStateMachineMap.split("\\|");
		frameNumberCount = actions.length;
		timer=System.currentTimeMillis()-timer;
		if(timer%1000==0)
		if(frameNumber>=frameNumberCount) {
			frameNumber=0;
		}else {
			int ind=0;
			for(String action:actions) {
				if(ind==frameNumber) {
					System.out.println(action);
					String[] commands = action.split(",");
					for(String command:commands) {
						String[] params = command.split("=");
						String commandName = params[0];
						String commandValue = params[1];
						System.out.println(commandName);
						switch(commandName){
							case "+x":
								this.setX(this.getX()+(Integer.parseInt(commandValue)*this.speed));
								break;
							case "-x":
								this.setX(this.getX()+(-1*Integer.parseInt(commandValue)*this.speed));
								break;
							case "+y":
								this.setY(this.getY()+Integer.parseInt(commandValue)*this.speed);
								break;
							case "-y":
								this.setY(this.getY()+(-1*Integer.parseInt(commandValue)*this.speed));
								break;
							case "d":
								if(checkPlayerInPlace(playerObject.getX(),playerObject.getY())) {
									playerStat.getDecHealthMultiply(this.damage);
								}
								break;
							default:
								break;
						}
					}
					ind++;
					
				}
			
				
			}
			
			frameNumber++;
		}
		
		return playerStat;
	}
	
	public boolean checkPlayerInPlace(int x , int y) {
		int[] objectPlace = {x,y};
		if((this.getX()<objectPlace[0])&&((this.getX()+this.getWidth())>objectPlace[0])&&(this.getY()<objectPlace[1])&&((this.getY()+this.getHight())>objectPlace[1])) {
			return true;
		}
		return false;
	}
}
