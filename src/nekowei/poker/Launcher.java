package nekowei.poker;

import nekowei.poker.freeCell.FreeCellGUI;
import nekowei.util.Logger;

public class Launcher {

	public static void main(String[] args) {

		Logger.info("loading...");
		long time = System.currentTimeMillis();
		Loader.preLoad();
		Logger.info("load cost: "+(System.currentTimeMillis()-time)+"ms");
		
		new FreeCellGUI();
	}
}
