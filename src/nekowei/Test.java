package nekowei;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Test extends JFrame {
	public Test() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setSize(200, 200);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.RED);
		g.drawRect(-10, -10, 100, 100);
		g.fillRect(0, 0, 88, 88);
		
	}
	
	public static void main(String[] args) {
		new Test();
	}
}
