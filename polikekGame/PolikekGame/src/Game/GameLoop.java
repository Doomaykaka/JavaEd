package Game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferStrategy;
import java.util.List;

import Game.GameObjects.Enemy;
import Game.GameObjects.NonCollisionGameObject;
import Game.GameObjects.PlayerGameObject;
import Game.GameObjects.PlayerStats;
import Game.GameObjects.Trigger;

public class GameLoop  extends Canvas implements Runnable {
	//params
	private boolean running;    
	public static int WIDTH = 512;
	public static int HEIGHT = 512;
	//objects
	private List<GameObject> gameObjects;
	private List<NonCollisionGameObject> nonCollisionGameObjects;
	private List<Trigger> triggerObjects;
	private List<Enemy> enemyObjects;
	private PlayerGameObject playerObject;
	private PlayerStats playerStats;
	//engine settings
	private int[] currentKey = new int[4];
	private long renderTimeCounter=0;
	private int x=0;
	private int y=0;
	private long timer=0;
	private float gravitation=0.8f;
	private final int frameTime=16;
	private int sceneDeltaCoord=0;
	private boolean isNextMap=false;
	private boolean isKilled=false;
	    
	public void start() {
		running = true;
		new Thread(this).start();
	}
	
	public void stop() {
		running = false;
	}
	    
	public void run() {
		long lastTime = System.currentTimeMillis();
		long delta;
	        
		init();
	        
		while(running) {
			delta = System.currentTimeMillis() - lastTime;
			lastTime = System.currentTimeMillis();  
			render(delta);
			update(delta);
			renderTimeCounter++;
		}
	}
	    
	public void init() {
		
	}
	    
	public void render(long delta) {
		try {	
			if(timer>frameTime) {
				getFrame();
				
				timer=0;
				renderTimeCounter=0;
			}else {
				timer=timer+delta;
			}
		}catch(IllegalStateException e) {
			
		}
	}
	    
	public void update(long delta) {
		if(renderTimeCounter%100000==2) {
			getControl(currentKey);
			getGravitation();
			getScroll();
		}	
	}
	
	public void getFrame() {
	
		BufferStrategy bs = getBufferStrategy();
	
		if (bs == null) {
			createBufferStrategy(2);
			requestFocus();
			return;
		}
		
		Graphics g = bs.getDrawGraphics(); //получаем Graphics из созданной нами BufferStrategy
		
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight()); //заполнить прямоугольник
		for(NonCollisionGameObject ncgo:nonCollisionGameObjects) {
			g=getPaint(g,ncgo);
		}
		for(Enemy en:enemyObjects) {
			playerStats=en.getFinalStateMachineLoop(playerObject, playerStats);
			g=getPaint(g,en);
		}
		for(Trigger tg:triggerObjects) {
			g=getPaint(g,tg);
			tg.setPlayerCoord(x, y);
			tg.checkTrigger();
			if(tg.checkIsFinal()) {
				isNextMap=true;
			}
		}
		for(GameObject go:gameObjects) {
			g=getPaint(g,go);
		}
		x=playerObject.getX();
		y=playerObject.getY();
		g=getPaint(g,playerObject);
		g=getStaticPaint(g,playerStats);
		if(playerStats.getHealth()<=0) {
			isKilled=true;
		}
		g.dispose();
		
		bs.show(); //показать
	}
	
	public Graphics getPaint(Graphics g,GameObject obj) {
		g.drawImage(obj.getTexture().getTexture().getImage(),obj.getX()-sceneDeltaCoord,obj.getY(),null);
		return g;
	} 
	
	public Graphics getStaticPaint(Graphics g,GameObject obj) {
		g.drawImage(playerStats.getPlayerPanel(),playerStats.getX(),playerStats.getY(),null);
		return g;
	}
	
	public boolean checkCollision(int colX,int colY,int colW,int colH,String name) {
		boolean haveCollision=false;
		boolean checkXone;
		boolean checkXtwo;
		boolean checkYone;
		boolean checkYtwo;
		
		for(GameObject gameObject:gameObjects) {
			if((!haveCollision)&&(!gameObject.getObjectName().equals(name))){
				checkXone = (colX+colW)<gameObject.getX();
				checkXtwo = (gameObject.getX()+gameObject.getWidth())<colX;
				checkYone = (colY+colH)<gameObject.getY();
				checkYtwo = (gameObject.getY()+gameObject.getHight())<colY;
				haveCollision=!(checkXone||checkXtwo||checkYone||checkYtwo);
				gameObject.setCollision(haveCollision);
			}
		}
		
		if((!haveCollision)&&(!name.equals(playerObject.getObjectName()))) {
			checkXone = (colX+colW)<playerObject.getX();
			checkXtwo = (playerObject.getX()+playerObject.getWidth())<colX;
			checkYone = (colY+colH)<playerObject.getY();
			checkYtwo = (playerObject.getY()+playerObject.getHight())<colY;
			haveCollision=!(checkXone||checkXtwo||checkYone||checkYtwo);
			playerObject.setCollision(haveCollision);
		}
		
		return haveCollision;
	}
	
	public void getScroll() {
		if((playerObject.getX()-sceneDeltaCoord)>824) {
			sceneDeltaCoord+=1024;
			playerObject.setX(sceneDeltaCoord+250);
			x=sceneDeltaCoord+250;
		}
		if(((playerObject.getX()-sceneDeltaCoord)<200)&&(playerObject.getX()>1024)) {
			sceneDeltaCoord-=1024;
			playerObject.setX(sceneDeltaCoord+774);
			x=sceneDeltaCoord+774;
		}
	}
	
	public void getGravitation() {
		for(GameObject testObject:gameObjects) {
			int dy = (int)(gravitation*(frameTime*frameTime)*testObject.getMass()/20);
			if(!checkCollision(testObject.getX(),testObject.getY()+dy,testObject.getWidth(),testObject.getHight(),testObject.getObjectName())) {
				int objY = testObject.getY();
				testObject.setY(objY+dy);
			}
		}
		
		int dy = (int)(gravitation*(frameTime*frameTime)*playerObject.getMass()/20);
		int jumpTime = 0;
		jumpTime = playerObject.getJumpTime();
		if(jumpTime<=0) {
			if(!checkCollision(playerObject.getX(),playerObject.getY()+dy,playerObject.getWidth(),playerObject.getHight(),playerObject.getObjectName())) {
				int objY = playerObject.getY();
				playerObject.setY(objY+dy);
			}
		}
	}
	
	public void getControl(int[] keyCodes) {	
		int dx=0;
		int dy=0;
		boolean playerInJump=false;
		int jumpTime=0;
		
		playerInJump=playerObject.getInJump();
		jumpTime=playerObject.getJumpTime();

		if((keyCodes[0]==87)&&(!playerInJump)&&(jumpTime<=0)) { //UP
			dy+=-1*playerStats.getSpeed();
			playerInJump=true;
			jumpTime=playerStats.getJumpTime();
		}
		
		if(keyCodes[1]==68) { //RIGHT
			dx+=playerStats.getSpeed();
		}
		
		if(keyCodes[2]==65){ //LEFT
			dx+=-1*playerStats.getSpeed();
		}
			
		if((playerInJump)&&(jumpTime>=0)) {
			dy+=-1*playerStats.getSpeed();
			jumpTime--;
		}
		
		if((playerInJump)&&(jumpTime<=0)) {
			dy+=playerStats.getSpeed();
			jumpTime--;
		}
			
		if(keyCodes[3]==83) { //DOWN
			dy+=playerStats.getSpeed();
		}
		
		if(!checkCollision(x+dx,y+dy,playerObject.getWidth(),playerObject.getHight(),playerObject.getObjectName())) {
			x+=dx;
			y+=dy;
			playerObject.setX(x);
			playerObject.setY(y);
			playerObject.setInJump(playerInJump);
			playerObject.setJumpTime(jumpTime);
		}else{
			playerObject.setInJump(false);
			playerObject.setJumpTime(0);
		}
	}
	
	public void setCurrentKey(int keyCode,int idx) {
		currentKey[idx]=keyCode;
	}
	
	public void getResources(Map map) {
		this.gameObjects=map.getGameObjects();
		this.nonCollisionGameObjects=map.getNonCollisionGameObjects();
		this.playerObject=map.getPlayerGameObject();
		this.triggerObjects=map.getTriggerObjects();
		this.playerStats=map.getPlayerStats();
		this.enemyObjects=map.getEnemyObjects();
	}
	
	public boolean getIsNextMap() {
		return isNextMap;
	}
	
	public boolean getIsKilled() {
		return isKilled;
	}
}
