package nekowei.poker.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Map;

import org.imgscalr.Scalr;

import nekowei.poker.Loader;

@SuppressWarnings("serial")
public class PokerUI extends Component {

	private BufferedImage image;
	private Poker p;
	private int lastWidth = 0;
	/**
	 * 显示槽位
	 */
	private boolean showSlot = false;
	/**
	 * 显示数字花色
	 */
	private boolean showNumberSuit = true;
	/**
	 * 扑克的宽高比。
	 */
	public static final double RATIO = 1.4;

	public PokerUI(Poker p) {
		this.p = p;
		if (!p.isEmpty() && p.getNumber() != 0) {
			Map<Poker, BufferedImage> imageMap = Loader.getImageMap();
			for (Poker obj : imageMap.keySet()) {
				if (p.equals(obj)) {
					this.image = imageMap.get(obj);
				}
			}
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		// 通过检测宽度是否变化来判断是否需要缩放图片
		boolean resize = false;
		if (lastWidth == 0 || lastWidth != getWidth()) {
			lastWidth = getWidth();
			resize = true;
		}
		if (showSlot) {
			g.setColor(Color.WHITE);
			g.drawRect(0, 0, getWidth() - 2, getHeight() - 2);
		}
		if (!p.isEmpty()) {
			if (p.getNumber() == 0) {
				if (p.isRed()) {
					g.setColor(Color.RED);
				} else {
					g.setColor(Color.BLACK);
				}
				g.setFont(new Font(getFont().getName(), Font.BOLD, getWidth()));
				g.drawString(p.getFormedSuit(), getWidth() / 16, getHeight() * 3 / 4);
			} else {				
				if (resize) {
					image = Scalr.resize(image, getWidth(), getHeight());
				}
				g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
				if (showNumberSuit) {				
					if (p.isRed()) {			
						g.setColor(Color.RED);
					} else {
						g.setColor(Color.BLACK);
					}
					g.setFont(new Font(getFont().getName(), Font.BOLD, getWidth() / 8));
					g.drawString(p.getFormedSuit()+""+p.getFormedNumber(), getWidth() / 20, getWidth() / 6);
				}
			}
		}
	}

	@Override
	public int getHeight() {
		return (int)((double)getWidth() * RATIO);
	}

	public boolean isShowSlot() {
		return showSlot;
	}

	public void setShowSlot(boolean showSlot) {
		this.showSlot = showSlot;
	}

	public boolean isShowNumberSuit() {
		return showNumberSuit;
	}

	public void setShowNumberSuit(boolean showNumberSuit) {
		this.showNumberSuit = showNumberSuit;
	}

	public Poker getP() {
		return p;
	}
	
}
