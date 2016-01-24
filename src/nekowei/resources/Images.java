package nekowei.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Images {

	public static Icon getIcon(String name) {
		Icon i = new ImageIcon(Images.class.getResource("icons/" + name + ".png"));
		return i;
	}
	
	public static BufferedImage loadDefaultImage(String name) throws IOException {
		return ImageIO.read(Images.class.getResource(name));
	}
	
}
