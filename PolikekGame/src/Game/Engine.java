package Game;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import Game.GameObjects.Enemy;
import Game.GameObjects.NonCollisionGameObject;
import Game.GameObjects.PlayerGameObject;
import Game.GameObjects.PlayerStats;
import Game.GameObjects.Trigger;
import Game.GameObjects.Trigger.Actions;
import Game.GameObjects.Trigger.Events;


enum State {MENU,SETTINGS,GAME};

public class Engine{
	
	//state
	private State engineState;
	//CurrentMap
	private Map map;
	private boolean needNextMap;
	
	
	public Engine() {
		engineState = State.MENU;
		map = null;
		needNextMap=false;
	}
	
	public void initSettings() {
		JFrame settingsFrame = new JFrame("Settings");
		settingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		settingsFrame.setLayout(new FlowLayout());
		settingsFrame.setSize(1000, 1000);
		settingsFrame.setVisible(true);
		settingsFrame.addWindowListener(new WindowListener() {
			@Override
			public void windowClosed(WindowEvent e) {
				init();   
		    }

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		engineState = State.SETTINGS;
	}
	
	public void initGame() {
	//  смена карты
	//	Map linkMap = new Map("Link");
	//	linkMap.setNextMap("testMap");
	//	this.setMap(linkMap);
		this.getResources();
		
		JFrame gameFrame = new JFrame("Game");
		gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		gameFrame.setSize(512, 512);
		
		getGameLoop(gameFrame);
	}
	
	public void init() {
		JPanel mPanel = new JPanel();
				
		JFrame menuFrame = new JFrame("Menu");
		menuFrame.setVisible(true);
		menuFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		menuFrame.setSize(1024, 1024);
		menuFrame.setResizable(false);
		
		JButton start = new JButton("Start game button");
		start.setText("Start new game");
		start.setBounds(442, 100, 140, 30);
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeState(menuFrame,State.GAME);
				initGame();
		    }
		});
				
		JButton settings = new JButton("Settings button");
		settings.setText("Settings");
		settings.setBounds(442, 230, 140, 30);
		settings.addActionListener(new ActionListener() {
		     public void actionPerformed(ActionEvent e) {
		         changeState(menuFrame,State.SETTINGS);
		         initSettings();
		     }
		});
		
		JButton quit = new JButton("Exit button");
		quit.setText("Exit");
		quit.setBounds(442, 360, 140, 30);
		quit.addActionListener(new ActionListener() {
		     public void actionPerformed(ActionEvent e) {
		         changeState(menuFrame,State.MENU);
		     }
		});
				
		mPanel.setLayout(null);
		mPanel.add(start);
		mPanel.add(settings);
		mPanel.add(quit);
				
		menuFrame.add(mPanel);
		
		engineState = State.MENU;
	}
	
	void getResources() {
		String filename = "";
		Sound addedSound = null;
		Texture addedTexture = null;
		
		
		//objects
		List<GameObject> gameObjects;
		List<NonCollisionGameObject> nonCollisionGameObjects;
		List<Trigger> triggerObjects;
		List<Enemy> enemyObjects;
		PlayerGameObject playerObject;
		PlayerStats playerStats;
		playerObject = null;
		playerStats = null;
		gameObjects = new ArrayList<>();
		nonCollisionGameObjects = new ArrayList<>();
		triggerObjects = new ArrayList<>();
		enemyObjects = new ArrayList<>();
		//resources
		List<Texture> textures;
		List<Sound> sounds;
		textures = new ArrayList<>();
		sounds = new ArrayList<>();
		
		InputStream res;
		InputStreamReader fr = null;
		BufferedReader reader = null; 
		boolean lever = false;
		String line = null;
		
		try {
			res = Engine.class.getResourceAsStream("/resList.dat");
			fr = new InputStreamReader (res);
			reader = new BufferedReader(fr);
			line = reader.readLine();
		
			while (line!=null) {
				if(line.equals("-")) {
					lever = true;
				}else {
					//filename = line.split(".")[0];
					filename=line;
					if(lever) {
						addedTexture = new Texture(filename,line);
						textures.add(addedTexture);
					}else {
						addedSound = new Sound(filename,line);
						sounds.add(addedSound);
					}
				}        
		    
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		File mapDir = new File("maps/");
		
		if(!needNextMap) {
			filename=null;
		}else {
			filename=map.getNextMap();
		}
		
		for(File file : mapDir.listFiles()) {
			if (filename==null) {
				loadMap(file, filename, fr, reader, line,  gameObjects,	nonCollisionGameObjects, triggerObjects, enemyObjects, playerObject, playerStats, textures, sounds);
				break;
			}else {
				if(file.getName().equals(filename)) {
					loadMap(file, filename, fr, reader, line,  gameObjects,	nonCollisionGameObjects, triggerObjects, enemyObjects, playerObject, playerStats, textures, sounds);	
					break;
				}
			}
		}
		
		
	}
	
	void loadMap(File mapCur, String filename, InputStreamReader fr, BufferedReader reader, String line,  List<GameObject> gameObjects,	List<NonCollisionGameObject> nonCollisionGameObjects,List<Trigger> triggerObjects,List<Enemy> enemyObjects, PlayerGameObject playerObject, PlayerStats playerStat,List<Texture> textures, List<Sound> sounds) {
		filename = mapCur.getName();
		try {
			fr = new FileReader(mapCur);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//создаем BufferedReader с существующего FileReader для построчного считывания
		reader = new BufferedReader(fr);
	    //addedTexture = new Texture(filename,file.getName());
	    //AddTexture(addedTexture);
		try {
			line = reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map currentMap = new Map(filename);
		if((line != null)&&(!line.equals(""))) {
			currentMap.setNextMap(line);
			try {
				line = reader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	    while ((line != null)&&(!line.equals(""))) {
	    	String[] objectParametrs = line.split(" ");
	        String type = objectParametrs[0];
	        String name = objectParametrs[1];
	        String objX = objectParametrs[2];
	        String objY = objectParametrs[3];
	        String objWidth = objectParametrs[4];
	        String objHight = objectParametrs[5];
	        String objText = objectParametrs[6];
	        String objSound = objectParametrs[7];
	        String objMass = objectParametrs[8];
	        //For Trigger
	        String objEvent = null;
	        String objAction = null;
	        String objNewTexture = null;
	        String objTimer = null;
	        //For PlayerSats and Enemy
	        String health = null;
	        String reload = null;
	        String jumpTime = null;
	        String speed = null;
	        String damage = null;
	        String healthTextureName = null;
	        String reloadTextureName = null;
	        String finalStateMachineMap = null;
	        
	        if((type.equals("Trigger"))&&(objectParametrs.length>11)) {
	        	objEvent = objectParametrs[11];
		        objAction = objectParametrs[12];
		        objNewTexture = objectParametrs[13];
		        objTimer = objectParametrs[14];
	        }
	        
	        if((type.equals("PlayerStats"))&&(objectParametrs.length>11)) {
	        	health = objectParametrs[11];
	        	reload = objectParametrs[12];
	        	jumpTime = objectParametrs[13];
	        	speed = objectParametrs[14];
	        	damage = objectParametrs[15];
	        	healthTextureName = objectParametrs[16];
	        	reloadTextureName = objectParametrs[17];
	        }
	        
	        if((type.equals("Enemy"))&&(objectParametrs.length>11)) {
	        	health = objectParametrs[11];
	        	reload = objectParametrs[12];
	        	speed = objectParametrs[13];
	        	damage = objectParametrs[14];
	        	finalStateMachineMap = objectParametrs[15];
	        }
	                
	        Texture objectTexture = null;
	        Texture loadedTexture = null;
	        Texture healthTexture = null;
	        Texture reloadTexture = null;
	        
	        Texture objectDefaultTexture = null;
	        Texture healthDefaultTexture = null;
	        Texture reloadDefaultTexture = null;
	        
	        for(Texture texture:textures) {
	        	if(texture.getTextureName().equals("guy.png")) {
	        		objectDefaultTexture=texture;
	        	}
	        	
	        	if(texture.getTextureName().equals("life.png")) {
	        		healthDefaultTexture=texture;
	        	}
	        	
	        	if(texture.getTextureName().equals("reload.png")) {
	        		reloadDefaultTexture=texture;
	        	}
	        	
	        	if(texture.getTextureName().equals(objText)) {
	        		objectTexture=texture;
	        	}
	        	
	        	if(texture.getTextureName().equals(healthTextureName)) {
	        		healthTexture=texture;
	        	}
	        	
	        	if(texture.getTextureName().equals(reloadTextureName)) {
	        		reloadTexture=texture;
	        	}
	        	
	        	if(texture.getTextureName().equals(objNewTexture)) {
        			loadedTexture = texture;
        		}
	        }
	        
	        Sound objectSound = null;
	        
	        Sound objectDefaultSound = null;
	        
	        for(Sound sound:sounds) {
	        	if(sound.getSoundName().equals("takeDamage.wav")) {
	        		objectDefaultSound=sound.clone();
	            }
	        	
	        	if(sound.getSoundName().equals(objSound)) {
	                objectSound=sound.clone();
	            }
	        }
	        	
	        playerObject = new PlayerGameObject(150,0,200,200,objectDefaultTexture,objectDefaultSound,2);
	        playerObject.setObjectName("defPlayer");
	        
	        playerStat = new PlayerStats(150,0,200,200,objectDefaultTexture,objectDefaultSound,2);
	        playerStat.setObjectName("defPlayerStat");
	        playerStat.setHealth(100);
	        playerStat.setReload(100);
	        playerStat.setJumpTime(80);
	        playerStat.setSpeed(4);
        	playerStat.setDamage(10);
        	playerStat.setHealthText(healthDefaultTexture);
        	playerStat.setReloadText(reloadDefaultTexture);
	                
	        switch(type) {
            	case "Object":
                	GameObject newObject = new GameObject(Integer.parseInt(objX),Integer.parseInt(objY),Integer.parseInt(objWidth),Integer.parseInt(objHight),objectTexture,objectSound,Integer.parseInt(objMass));
	             	newObject.setObjectName(name);
		        	gameObjects.add(newObject);
                	break;
            	case "Player":
                	PlayerGameObject newPlayer = new PlayerGameObject(Integer.parseInt(objX),Integer.parseInt(objY),Integer.parseInt(objWidth),Integer.parseInt(objHight),objectTexture,objectSound,Integer.parseInt(objMass));
                	newPlayer.setObjectName(name);
	            	playerObject=newPlayer;
                	break;
            	case "NonCollision":
                	NonCollisionGameObject newNonCollision = new NonCollisionGameObject(Integer.parseInt(objX),Integer.parseInt(objY),Integer.parseInt(objWidth),Integer.parseInt(objHight),objectTexture,objectSound,Integer.parseInt(objMass));
                	newNonCollision.setObjectName(name);
	            	nonCollisionGameObjects.add(newNonCollision);
                	break;
                case "Trigger":
                	Trigger newTrigger = new Trigger(Integer.parseInt(objX),Integer.parseInt(objY),Integer.parseInt(objWidth),Integer.parseInt(objHight),objectTexture,objectSound,Integer.parseInt(objMass));
                	newTrigger.setObjectName(name);
                	newTrigger.setNewTexture(loadedTexture);
                	newTrigger.setTimer(Long.parseLong(objTimer));
                	newTrigger.setTriggerAction(Actions.valueOf(objAction));
                	newTrigger.setTriggerEvent(Events.valueOf(objEvent));
                	triggerObjects.add(newTrigger);
                	break;
                case "PlayerStats":
                	PlayerStats newPlayerStats = new PlayerStats(Integer.parseInt(objX),Integer.parseInt(objY),Integer.parseInt(objWidth),Integer.parseInt(objHight),objectTexture,objectSound,Integer.parseInt(objMass));
                	newPlayerStats.setObjectName(name);
                	newPlayerStats.setHealth(Integer.parseInt(health));
                	newPlayerStats.setReload(Integer.parseInt(reload));
                	newPlayerStats.setJumpTime(Integer.parseInt(jumpTime));
                	newPlayerStats.setSpeed(Integer.parseInt(speed));
                	newPlayerStats.setDamage(Integer.parseInt(damage));
                	newPlayerStats.setHealthText(healthTexture);
                	newPlayerStats.setReloadText(reloadTexture);
                	playerStat=newPlayerStats;
                	break;
                case "Enemy":
                	Enemy newEnemy = new Enemy(Integer.parseInt(objX),Integer.parseInt(objY),Integer.parseInt(objWidth),Integer.parseInt(objHight),objectTexture,objectSound,Integer.parseInt(objMass));
                	newEnemy.setObjectName(name);
                	newEnemy.setHealth(Integer.parseInt(health));
                	newEnemy.setReload(Integer.parseInt(reload));
                	newEnemy.setSpeed(Integer.parseInt(speed));
                	newEnemy.setDamage(Integer.parseInt(damage));
                	newEnemy.setFinalStateMachineMap(finalStateMachineMap);
                	enemyObjects.add(newEnemy);
                	break;
	            }
	                
	            // считываем остальные строки в цикле
	            try {
	            	line = reader.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	            
	        currentMap.loadMap(gameObjects, nonCollisionGameObjects, playerObject, textures, sounds, triggerObjects, playerStat, enemyObjects);
	            
	        map=currentMap;
	}
	
	void getGameLoop(JFrame gameWindow) {	
		GameLoop game = new GameLoop();
		
		game.setPreferredSize(new Dimension(1024, 1024));
		game.getResources(map);
		game.addKeyListener(new KeyAdapter() {
			@Override
		    public void keyPressed(KeyEvent event) {
				if(game.getIsNextMap()) {
					needNextMap=true;
					gameWindow.dispatchEvent(new WindowEvent(gameWindow, WindowEvent.WINDOW_CLOSING));
				}
				
				switch(event.getKeyCode()) {
					case 87:
						game.setCurrentKey(87,0);
						break;
					case 68:
						game.setCurrentKey(68,1);
						break;
					case 65:
						game.setCurrentKey(65,2);
						break;
					case 83:
						game.setCurrentKey(83,3);
						break;
				}
		    }
			
			@Override
		    public void keyTyped(KeyEvent event) {
				switch(event.getKeyCode()) {
					case 87:
						game.setCurrentKey(87,0);
						break;
					case 68:
						game.setCurrentKey(68,1);
						break;
					case 65:
						game.setCurrentKey(65,2);
						break;
					case 83:
						game.setCurrentKey(83,3);
						break;
				}
		    }
			
			@Override
		    public void keyReleased(KeyEvent event) {
				switch(event.getKeyCode()) {
					case 87:
						game.setCurrentKey(0,0);
						break;
					case 68:
						game.setCurrentKey(0,1);
						break;
					case 65:
						game.setCurrentKey(0,2);
						break;
					case 83:
						game.setCurrentKey(0,3);
						break;
				}
		    }
		});
		
		gameWindow.addWindowListener(new WindowListener() {
			
			@Override
			public void windowClosed(WindowEvent e) {
				game.stop();
				init();   
		    }

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		gameWindow.setLayout(new BorderLayout());
		gameWindow.add(game,BorderLayout.CENTER);
		gameWindow.pack();
		gameWindow.setResizable(false);
		gameWindow.setVisible(true);
		
		game.start();
	}
	
	void setMap(Map mp) {
		map = mp;
	}
	
	void changeState(JFrame currentFrame,State newState) {
		currentFrame.setVisible(false);
		currentFrame.dispatchEvent(new WindowEvent(currentFrame, WindowEvent.WINDOW_CLOSING));
		engineState=newState;
		System.out.println("New state is "+newState.toString());
	}
}
