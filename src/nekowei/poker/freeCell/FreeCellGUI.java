package nekowei.poker.freeCell;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import nekowei.poker.common.Poker;
import nekowei.poker.common.Poker.Suit;
import nekowei.poker.common.PokerUI;
import nekowei.resources.Images;
import nekowei.util.Logger;

@SuppressWarnings("serial")
public class FreeCellGUI extends JFrame implements MouseInputListener {

	private JPanel mainPanel;
	private JPanel freeCellPanel;
	private JPanel finalStackPanel;
	private List<JPanel> playStackPanelList = new ArrayList<JPanel>();
	private HeadPanel headPanel;
	private JPanel movingPanel;
	
	private volatile boolean updating = false; 
	
	private int stackWidth;
	private int stackHeight;
	private int xOffset;
	private int yOffset;
	private int pokerWidth;
	private int pokerHeight;
	private int headWidth;

	public static final Color BGC = new Color(0x5bd553);
	public static final Color BGL = new Color(0x7bf573);
	public static final Color BGB = new Color(0x60e060);

	public FreeCellGUI() {
		Logger.info("init begin");

		init();
		
		 try {
		 setIconImage(Images.loadDefaultImage("icons/up.png"));
		 } catch (IOException e) {
		 e.printStackTrace();
		 }

		final FreeCellCore fcc = FreeCellCore.getInstance();
		setTitle("空当接龙 #" + fcc.getSeed());

		update(fcc);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Logger.info("resize");
				super.componentResized(e);
				if (!updating) {
					update(fcc);
				}
			}
		});
		
		addMouseMotionListener(this);
		
		Logger.info("init end");

	}

	/**
	 * 初始化
	 * 基本就是界面设置，加载初始构成
	 */
	public void init() {
		setVisible(true);
		setSize(1600, 900);
		setMinimumSize(new Dimension(1024, 768));
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screen.width / 2 - getWidth() / 2, screen.height / 2 - getHeight() / 2);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(new BorderLayout());
		mainPanel = new JPanel();
		mainPanel.setBackground(BGC);
		mainPanel.setLayout(null);
		mainPanel.setName("mainPanel");
		add(mainPanel, BorderLayout.CENTER);

		initMovingPanel();	// 原来先add的会显示在上面……
		initHeadPanel();
		initFreeCellPanel();
		initFinalStackPanel();
		initPlayStackPanel();

	}

	private void initHeadPanel() {
		headPanel = new HeadPanel();
		headPanel.setBackground(null);
		headPanel.setName("headPanel");
		mainPanel.add(headPanel);
	}

	private void initFreeCellPanel() {
		freeCellPanel = new JPanel();
		mainPanel.add(freeCellPanel);
		freeCellPanel.addMouseListener(this);
		freeCellPanel.addMouseMotionListener(this);
		freeCellPanel.setBackground(null);
		freeCellPanel.setName("freeCellPanel");
	}
	
	private void initFinalStackPanel() {
		finalStackPanel = new JPanel();
		mainPanel.add(finalStackPanel);
		finalStackPanel.addMouseListener(this);
		finalStackPanel.addMouseMotionListener(this);
		finalStackPanel.setBackground(null);
		finalStackPanel.setName("finalStackPanel");
	}

	public void initPlayStackPanel() {
		if (playStackPanelList.size() == 0) {
			for (Integer i = 0; i < FreeCellCore.PLAY_SIZE; i++) {
				JPanel p = new JPanel();
				p.setBackground(null);
				playStackPanelList.add(p);
				mainPanel.add(p);
				p.addMouseListener(this);
				p.addMouseMotionListener(this);
				p.setName(i.toString()); // 在名字里标记下是第几列，用于逻辑判断的column
			}
		}
	}

	private void initMovingPanel() {
		movingPanel = new JPanel();
		movingPanel.setBackground(null);
		movingPanel.setName("movingPanel");
		mainPanel.add(movingPanel);
	}

	///////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 更新界面，按顺序重绘界面
	 * @param fcc
	 */
	public void update(FreeCellCore fcc) {
		updating = true;
		long time = System.currentTimeMillis();
		updateBasicValues();
		updateHeadPanel();
		updateFreeCellPanel(fcc.getFreeCell());
		updateFinalStackPanel(fcc.getFinalStack());
		updatePlayStackPanel(fcc.getPlayStack());
		updateMovingPanel(fcc.getMovingStack());
		Logger.info("update cost:" + (System.currentTimeMillis() - time) + "ms");
		updating = false;
	}

	private void updateBasicValues() {
		headWidth = getWidth() / FreeCellCore.PLAY_SIZE;
		stackWidth = (getWidth() - headWidth) / FreeCellCore.PLAY_SIZE;
		stackHeight = 800;
		yOffset = (int) ((double) stackWidth * PokerUI.RATIO);
		pokerWidth = stackWidth - 40;
		pokerHeight = (int) ((double) pokerWidth * PokerUI.RATIO);
	}

	private void updateHeadPanel() {
		headPanel.setBounds((getWidth() - headWidth) / 2, 0, headWidth, pokerHeight);
	}

	private void updateFreeCellPanel(List<Poker> list) {
		freeCellPanel.removeAll();
		freeCellPanel.setBounds(0, 0, stackWidth * 4, yOffset);
		for (Integer i = 0; i < list.size(); i++) {
			PokerUI pui = new PokerUI(list.get(i));
			pui.setShowSlot(true);
			freeCellPanel.add(pui);
			pui.setBounds(stackWidth * i + 10, 10, pokerWidth, pokerHeight);
			pui.setName(i.toString());
		}
	}

	private void updateFinalStackPanel(Map<Suit, Poker> map) {
		finalStackPanel.removeAll();
		for (Suit suit : Suit.values()) {
			PokerUI pui = new PokerUI(map.get(suit));
			pui.setShowSlot(true);
			finalStackPanel.add(pui);
			pui.setBounds(stackWidth * suit.ordinal(), 10, pokerWidth, pokerHeight);
			pui.setName(suit.ordinal()+"");
		}
		finalStackPanel.setBounds(getWidth() - stackWidth * 4 + 10, 0, stackWidth * 4, yOffset);
	}

	public void updatePlayStackPanel(Map<Integer, List<Poker>> map) {
		int column = 0;
		for (List<Poker> l : map.values()) {
			JPanel panel = playStackPanelList.get(column);
			xOffset = stackWidth * column + headWidth / 2;
			panel.removeAll(); // 简单粗暴的清空重新生成一遍，效率上应该问题不大。
			for (int j = l.size() - 1; j >= 0; j--) {
				Poker p = l.get(j);
				PokerUI pui = new PokerUI(p);
//				pui.setShowSlot(true);
				panel.add(pui);
				if (j < l.size() - 1) {
					if (j < 9) {						
						pui.setBounds(10, j * 50, pokerWidth, pokerHeight / 10);
					} else {
						int jy = (int)(8.0 / (double)j * 50.0); // stack大于8时压缩间隔
						pui.setBounds(10, jy, pokerWidth, pokerHeight / 10);
					}
				} else {
					pui.setBounds(10, j * 50, pokerWidth, pokerHeight);
				}
			}
			stackHeight = (l.size() - 1) * 50 + pokerHeight;
			panel.setBounds(xOffset, yOffset, stackWidth, stackHeight);
			column++;
		}
	}

	private void updateMovingPanel(List<Poker> list) {
		for (int j = list.size() - 1; j >= 0; j--) {
			PokerUI pui = new PokerUI(list.get(j));
			movingPanel.add(pui);
			if (j < list.size() - 1) {
				pui.setBounds(0, j * 50, pokerWidth, pokerHeight / 10);
			} else {
				pui.setBounds(0, j * 50, pokerWidth, pokerHeight);
			}
		}
		if (list.size() == 0) {
			stackHeight = 0;
		} else {			
			stackHeight = (list.size() - 1) * 50 + pokerHeight;
		}
		Point p = getMousePosition();
		if (p != null) {			
			movingPanel.setBounds(p.x - pokerWidth / 10, p.y - pokerHeight / 5, pokerWidth, stackHeight);
		}
	}

	///////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void mouseMoved(MouseEvent e) {
		Point p = getMousePosition();
		if (p.getX() < getWidth() / 2) {
			headPanel.setLeftSide(true);
		} else {
			headPanel.setLeftSide(false);
		}
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getSource().equals(freeCellPanel)) {
			Logger.info("press free111"+e.getPoint().toString());
			
		} else if (e.getSource().equals(finalStackPanel)) {
			Logger.info("press final"+e.getPoint().toString());
			
		} else {
			Logger.info("press at"+e.getPoint().toString());
			JPanel p = (JPanel) e.getSource();
			if (p.getComponentAt(e.getPoint()) instanceof PokerUI) {
				PokerUI pui = (PokerUI) p.getComponentAt(e.getPoint());
				int column = Integer.valueOf(p.getName());
				// 如果拖的动就把选中的扑克队列取出来操作
				FreeCellCore.getInstance().getCardsMove(column, pui.getP());
			}
		}
		update(FreeCellCore.getInstance());
		FreeCellCore.getInstance().debug();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		update(FreeCellCore.getInstance());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getSource().equals(freeCellPanel)) {
			
			Logger.info("release at free "+e.getPoint().toString());
			JPanel p = (JPanel) e.getSource();
			if (p.getComponentAt(e.getPoint()) instanceof PokerUI) {
				int column = Integer.valueOf(p.getName());
				FreeCellCore.getInstance().tryMoveToFreeCell(column);
			} else {
				FreeCellCore.getInstance().moveBack();
			}
			
		} else if (e.getSource().equals(finalStackPanel)) {
			
			Logger.info("release at final "+e.getPoint().toString());
			JPanel p = (JPanel) e.getSource();
			if (p.getComponentAt(e.getPoint()) instanceof PokerUI) {
				int column = Integer.valueOf(p.getName());
				FreeCellCore.getInstance().tryMoveToFinalStack(column);
			} else {
				FreeCellCore.getInstance().moveBack();
			}
			
		} else if (e.getSource().equals(movingPanel)) {
			
			Logger.info("shit");
			
		} else if (e.getSource() instanceof PokerUI) {
			PokerUI pui = (PokerUI)e.getSource();
			Logger.info("shit!?"+pui.getParent().toString());
		} else {

			Logger.info("release move to "+e.getSource().toString()+e.getPoint().toString());
			Logger.info("i am your father:"+((Component)e.getSource()).getParent().toString());
			JPanel p = (JPanel) e.getSource();
			if (p.getComponentAt(e.getPoint()) instanceof PokerUI) {
				int column = Integer.valueOf(p.getName());
				// 如果拖的动就把选中的扑克队列取出来操作
				FreeCellCore.getInstance().tryMoveToPlayStack(column);
			} else {
				FreeCellCore.getInstance().moveBack();
			}
		}
		update(FreeCellCore.getInstance());
		FreeCellCore.getInstance().debug();
	}

	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("click!?");
	}

}
