package nekowei.poker.freeCell;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JPanel;

import nekowei.resources.Images;

@SuppressWarnings("serial")
public class HeadPanel extends JPanel {

	private BufferedImage left;
	private BufferedImage right;
	private boolean leftSide;

	public HeadPanel() {
		try {
			left = Images.loadDefaultImage("icons/left.png");
			right = Images.loadDefaultImage("icons/right.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (isLeftSide()) {
			g.drawImage(left, getWidth() / 4, getHeight() / 3, getWidth() / 2, getHeight() / 2, null);
		} else {
			g.drawImage(right, getWidth() / 4, getHeight() / 3, getWidth() / 2, getHeight() / 2, null);
		}
	}

	public boolean isLeftSide() {
		return leftSide;
	}

	public void setLeftSide(boolean leftSide) {
		this.leftSide = leftSide;
	}
	
}
