package nekowei.poker;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import nekowei.poker.common.Poker;
import nekowei.poker.common.Poker.Suit;
import nekowei.resources.Images;

public class Loader {

	private static List<Poker> pokerList = new ArrayList<Poker>();
	
	private static Map<Poker, BufferedImage> imageMap = new HashMap<Poker, BufferedImage>();
	
	public static final String DIR = "d:/pokers/";
	public static final String EXT = ".jpg";

	public static void preLoad() {
		BufferedImage defaultImage = null;
		BufferedImage evil = null;
		BufferedImage light = null;
		BufferedImage deep = null;
		BufferedImage normal = null;
		try {
			defaultImage = Images.loadDefaultImage("marisa.jpg");
			evil = Images.loadDefaultImage("evil.jpg");
			light = Images.loadDefaultImage("light.jpg");
			deep = Images.loadDefaultImage("deep.jpg");
			normal = Images.loadDefaultImage("normal.jpg");
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Suit suit : Suit.values()) {
			for (int i = 1; i <= 13; i++) {
				Poker p = new Poker(suit, i);
				pokerList.add(p);
				BufferedImage image = load(suit, i);
				if (image != null) {
					imageMap.put(p, image);
				} else {
					if (suit.equals(Suit.diamond)) {
						imageMap.put(p, evil);
					} else if (suit.equals(Suit.club)) {
						imageMap.put(p, light);
					} else if (suit.equals(Suit.spade)) {
						imageMap.put(p, deep);
					} else if (suit.equals(Suit.heart)) {
						imageMap.put(p, normal);
					}
//					imageMap.put(p, defaultImage);
				}
			}
		}
	}
	
	private static BufferedImage load(Suit suit, int number) {
		BufferedImage image = null;
		File f = new File(DIR + suit + number + EXT);
		if (f.exists()) {
			try {
				image = ImageIO.read(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return image;
	}

	public static List<Poker> getPokerList() {
		return pokerList;
	}

	public static Map<Poker, BufferedImage> getImageMap() {
		return imageMap;
	}

}
