package Game;

import java.util.ArrayList;
import java.util.List;

import Game.GameObjects.Enemy;
import Game.GameObjects.NonCollisionGameObject;
import Game.GameObjects.PlayerGameObject;
import Game.GameObjects.PlayerStats;
import Game.GameObjects.Trigger;

public class Map {
	private String mapName;
	private String nextMap;
	//objects
	private List<GameObject> gameObjects;
	private List<NonCollisionGameObject> nonCollisionGameObjects;
	private List<Trigger> triggerObjects;
	private List<Enemy> enemyObjects;
	private PlayerGameObject playerObject;
	private PlayerStats playerStats;
	//resources
	private List<Texture> textures;
	private List<Sound> sounds;
	
	public Map(String name) {
		setMapName(name);
		nextMap = null;
		gameObjects = new ArrayList<GameObject>();
		nonCollisionGameObjects = new ArrayList<NonCollisionGameObject>();
		triggerObjects = new ArrayList<Trigger>();
		enemyObjects = new ArrayList<Enemy>();
		playerObject = null;
		playerStats = null;
		textures = new ArrayList<Texture>();
		sounds = new ArrayList<Sound>();
	}
	
	public void addEnemyObject(Enemy eo) {
		enemyObjects.add(eo);
	}
	
	public void deleteEnemyObject(Enemy eo) {
		enemyObjects.remove(eo);
	}
	
	public void addGameObject(GameObject go) {
		gameObjects.add(go);
	}
	
	public void deleteGameObject(GameObject go) {
		gameObjects.remove(go);
	}
	
	public void addNonCollisionGameObject(NonCollisionGameObject no) {
		nonCollisionGameObjects.add(no);
	}
	
	public void deleteNonCollisionGameObject(NonCollisionGameObject no) {
		nonCollisionGameObjects.remove(no);
	}
	
	public void addTriggerObjects(Trigger to) {
		triggerObjects.add(to);
	}
	
	public void deleteTriggerObjects(Trigger to) {
		triggerObjects.remove(to);
	}
	
	public String getNextMap() {
		return nextMap;
	}
	
	public void setNextMap(String nextMap) {
		this.nextMap=nextMap;
	}
	
	public void loadMap(List<GameObject> gameObjects, List<NonCollisionGameObject> nonCollisionGameObjects, PlayerGameObject playerObject, List<Texture> textures, List<Sound> sounds, List<Trigger> triggerObjects, PlayerStats playerStats, List<Enemy> enemyObjects) {
		this.gameObjects=gameObjects;
		this.nonCollisionGameObjects=nonCollisionGameObjects;
		this.playerObject=playerObject;
		this.textures=textures;
		this.sounds=sounds;
		this.triggerObjects=triggerObjects;
		this.playerStats=playerStats;
		this.enemyObjects=enemyObjects;
	}
	
	public List<GameObject> getGameObjects(){
		return gameObjects;
	}
	
	public List<NonCollisionGameObject> getNonCollisionGameObjects(){
		return nonCollisionGameObjects;
	}
	
	public List<Trigger> getTriggerObjects(){
		return triggerObjects;
	}
	
	public List<Enemy> getEnemyObjects(){
		return enemyObjects;
	}
	
	public PlayerGameObject getPlayerGameObject(){
		return playerObject;
	}
	
	public List<Texture> getTextures(){
		return textures;
	}
	
	public List<Sound> getSounds(){
		return sounds;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public PlayerStats getPlayerStats() {
		return playerStats;
	}
}
