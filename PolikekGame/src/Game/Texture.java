package Game;

import javax.swing.ImageIcon;

public class Texture {
	private String textureName;
	private ImageIcon texture;
	
	public Texture(String textureName,String textureFileName) {
		this.textureName=textureName;
		setTexture(textureFileName);
	}
	
	public ImageIcon getTexture() {
		return texture;
	}
	
	public void setTexture(String textureFileName) {
		ImageIcon newTexture = null;
		
		try {
			newTexture = new ImageIcon(Texture.class.getResource("/textures/"+textureFileName));
		}catch(Exception getFileError){
			System.out.println(getFileError.getMessage());
		}
		
		texture = newTexture;
	}
	
	public String getTextureName() {
		return textureName;
	}
	
	public void setTextureName(String textureName) {
		this.textureName=textureName;
	}
}
