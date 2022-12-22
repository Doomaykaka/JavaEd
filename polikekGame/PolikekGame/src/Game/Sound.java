package Game;

import java.io.BufferedInputStream;
import java.io.InputStream;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
	private String soundName;
	private Clip sound;
	private String soundFileName;
	
	public Sound(String soundName,String soundFileName) {
		this.soundName=soundName;
		this.soundFileName=soundFileName;
		this.setSound(soundFileName);
	}
	
	public Sound clone() {
		Sound clone = new Sound(soundName,soundFileName);
		return clone;
	}
	
	public String getSoundName() {
		return soundName;
	}
	
	public void setSoundName(String soundName) {
		this.soundName=soundName;
	}
	
	public Clip getSound() {
		return sound;
	}
	
	public void setSound(String soundFileName) {
		Clip clip = null;
		try {
			clip = AudioSystem.getClip();
			InputStream audioSrc = Sound.class.getResourceAsStream("/sounds/"+soundFileName);
			InputStream bufferedIn = new BufferedInputStream(audioSrc);
			clip.open(AudioSystem.getAudioInputStream(bufferedIn));
		}catch(Exception getFileError) {
			System.out.println(getFileError.getMessage());
		}
	    sound=clip;  
	}
	
	public void playSound() {
		if(!sound.equals(null)) {
			sound.start();
		}
	}
	
	public void stopSound() {
		if(!sound.equals(null)) {
			sound.stop();
			sound.setMicrosecondPosition(0);
		}
	}
	
}
