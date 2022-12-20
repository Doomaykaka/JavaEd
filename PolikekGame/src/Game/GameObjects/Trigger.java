package Game.GameObjects;

import Game.GameObject;

import Game.Sound;
import Game.Texture;

public class Trigger extends GameObject{
	private Events triggerEvent;
	private Actions triggerAction;
	private Texture newTexture;
	private Sound triggerSound;
	private long timer;
	private long soundTimer;
	private long pastTime;
	private boolean activate;
	private boolean isFinal;
	private int[] playerCoord = new int[2];
	public enum Events {ONTIMER,ONPLAYERINPLACE,SYNTHETICACTIVATION};
	public enum Actions {PLAYSOUND,CHANGETEXTURE,ISFINAL};
	
	public Trigger(int x, int y, int width, int hight, Texture texture, Sound sound, int mass) {
		super(x, y, width, hight, texture, sound, mass);
		triggerEvent=Events.ONTIMER;
		triggerAction=Actions.PLAYSOUND;
		newTexture=null;
		triggerSound=this.getSound();
		setTimer(0);
		soundTimer=0;
		pastTime=System.currentTimeMillis();
		setActivate(false);
		playerCoord[0]=0;
		playerCoord[1]=0;
		isFinal=false;
	}

	public Events getTriggerEvent() {
		return triggerEvent;
	}

	public void setTriggerEvent(Events triggerEvent) {
		this.triggerEvent = triggerEvent;
	}

	public Actions getTriggerAction() {
		return triggerAction;
	}

	public void setTriggerAction(Actions triggerAction) {
		this.triggerAction = triggerAction;
	}

	public Texture getNewTexture() {
		return newTexture;
	}

	public void setNewTexture(Texture newTexture) {
		this.newTexture = newTexture;
	}

	public long getTimer() {
		return timer;
	}

	public void setTimer(long timer) {
		this.timer = timer;
	}

	public boolean isActivate() {
		return activate;
	}

	public void setActivate(boolean activate) {
		this.activate = activate;
	}
	
	public void setPlayerCoord(int x, int y) {
		this.playerCoord[0] = x;
		this.playerCoord[1] = y;
	}
	
	public int[] getPlayerCoord() {
		return playerCoord;
	}
	
	public void setIsFinal() {
		this.isFinal=true;
	}
	
	public void checkTrigger() {
		switch(triggerEvent){
			case ONTIMER:
				if(getTimer()<0) {
					executeTrigger();
					setTimer(0);
				}
				break;
			case ONPLAYERINPLACE:
				if(checkPlayerInPlace()) {
					executeTrigger();
				}
				break;
			case SYNTHETICACTIVATION:
				if(isActivate()) {
					executeTrigger();
				}
				break;
			default:
				break;
		}
		
		if(soundTimer<100) {
			triggerSound.stopSound();
			soundTimer=0;
		}
		if(soundTimer>0) {
			soundTimer-=(System.currentTimeMillis()-pastTime);
			pastTime=System.currentTimeMillis();
		}
		
		if(getTimer()>0) {
			setTimer(getTimer()-System.currentTimeMillis()-pastTime);
			pastTime=System.currentTimeMillis();
		}
	}
	
	public void executeTrigger() {
		switch(triggerAction){
			case PLAYSOUND:
				triggerSound.playSound();
				soundTimer=(long)(triggerSound.getSound().getMicrosecondLength()/1000);
				break;
			case CHANGETEXTURE:
				this.setTexture(newTexture);
				break;
			case ISFINAL:
				setIsFinal();
				break;
			default:
				break;
		}
	}
	
	public boolean checkPlayerInPlace() {
		int[] playerPlace = getPlayerCoord();
		if((this.getX()<playerPlace[0])&&((this.getX()+this.getWidth())>playerPlace[0])&&(this.getY()<playerPlace[1])&&((this.getY()+this.getHight())>playerPlace[1])) {
			return true;
		}
		return false;
	}
	
	public boolean checkIsFinal() {
		return isFinal;
	}
}
