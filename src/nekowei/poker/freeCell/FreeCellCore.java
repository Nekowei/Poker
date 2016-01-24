package nekowei.poker.freeCell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import nekowei.poker.Loader;
import nekowei.poker.common.Poker;
import nekowei.poker.common.Poker.Suit;
import nekowei.util.Logger;

/**
 * 空当接龙核心模型
 * 
 * @author nekowei
 * @date 2016-1-10
 */
public class FreeCellCore {

	private static Random r = new Random();
	private static int seed = r.nextInt(1000000);
//	private static int seed = 106280;
	private long startTime;
	/**
	 * 已走步数
	 */
	private int moveCount;
	/**
	 * 目前可能的选择数
	 */
	private int possibleMove;
	/**
	 * 临时空间
	 */
	private Map<Integer, Poker> freeCell;
	/**
	 * 完成栈
	 */
	private Map<Suit, Poker> finalStack;
	/**
	 * 操作栈
	 */
	private Map<Integer, List<Poker>> playStack;
	/**
	 * 操作栈列数，好像一般是8列吧
	 */
	public static final int PLAY_SIZE = 8;
	/**
	 * 移动栈
	 */
	private List<Poker> movingStack;	
	/**
	 * 记录取出的是第几列 
	 */
	private int getColumn;
	
	/**
	 * 单例模式
	 */
	private static FreeCellCore fcc;
	private FreeCellCore() {
		newGame(seed); // 构造时默认开始新局。
	}
	public static FreeCellCore getInstance() {
		if (fcc == null) fcc = new FreeCellCore();
		return fcc;
	}

	/**
	 * 初始化方法，所有内容都静态构建，还要保证单例
	 */
	public void newGame(int SEED) {
		r = new Random(SEED);

		freeCell = new HashMap<Integer, Poker>();
		for (int i = 0; i < 4; i++) {
			freeCell.put(i, new Poker());
		}
		finalStack = new HashMap<Suit, Poker>();
		for (Suit suit : Suit.values()) {
			finalStack.put(suit, new Poker(suit, 0));
		}
		playStack = new HashMap<Integer, List<Poker>>();
		for (int i = 0; i < PLAY_SIZE; i++) {
			playStack.put(i, new ArrayList<Poker>());
		}
		movingStack = new ArrayList<Poker>();
		
		initPokers();

		startTime = System.currentTimeMillis();
		// 开头就死了怎么行
		if (isGameOver()) {
			seed = r.nextInt(1000000);
			newGame(seed);
		}
	}

	/**
	 * 初始化牌面
	 */
	private void initPokers() {
		// 顺序生成所有牌
		List<Poker> initList = Loader.getPokerList();
		// 在list中打乱顺序，然后放入map中
		Collections.shuffle(initList, r);
		int column = 0;
		for (Poker poker : initList) {
			playStack.get(column).add(poker);
			column++;
			if (column == PLAY_SIZE)
				column = 0;
		}
	}

	//////////////////////////////////////////////////////

	/**
	 * 检查游戏状态 所有牌都到位，游戏结束 未到位但无法继续移动，游戏结束
	 */
	private boolean isGameOver() {
		boolean gameOver = false;
		boolean finish = true;
		for (Suit suit : Suit.values()) {
			Poker p = finalStack.get(suit);
			if (p == null || p.getNumber() < 13) {
				finish = false;
				break;
			}
		}
		if (finish) {
			gameOver = true;
		} else {
			// 先看空挡，满了才判断可否移动
			boolean full = true;
			for (Poker p : freeCell.values()) {
				if (p == null) {
					full = false;
					break;
				}
			}
			if (full) {
				possibleMove = 0;
				for (int i = 0; i < PLAY_SIZE; i++) {
					Poker p = getMoveHead(i);
					for (int j = 0; j < PLAY_SIZE; j++) {
						if (j==i) continue;
						if (canMove(j, p)) {
							possibleMove++;
						}
					}
				}
			}
			if (possibleMove == 0) {
				gameOver = true;
			}
		}
		return gameOver;
	}

	/**
	 * 获取指定行的最高可移动牌,gameover用
	 */
	private Poker getMoveHead(int column) {
		List<Poker> list = playStack.get(column);
		Poker head = null;
		if (list.size() == 1) {
			head = list.get(0);
		} else {
			for (int i = list.size() - 2; i >= 0; i--) {
				if (!list.get(i+1).isConnectableUpTo(list.get(i))) {
					head = list.get(i+1);
					break;
				}
			}
		}
		return head;
	}
	
	/**
	 * 判断是否可以移动到目标位置,gameover用
	 */
	private boolean canMove(int column, Poker moveHead) {
		List<Poker> list = playStack.get(column);
		if (list.size() == 0 || moveHead.isConnectableUpTo(list.get(list.size() - 1))) {
			return true;
		} else {
			return false;
		}
	}

	private int getRow(List<Poker> list, Poker p) {
		int row = 0;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals(p)) {
				row = i;
			}
		}
		return row;
	}

	/**
	 * 拿得起：有牌且下面连的上
	 */
	private boolean canGet(int column, int row) {
		boolean flag = true;
		List<Poker> list = playStack.get(column);
		// 没牌
		if (list.size() == 0) {
			flag = false;
		} else if (list.size() > 1) {
			while (row < list.size() - 1) {
				// 下面连不上
				if (!list.get(row + 1).isConnectableUpTo(list.get(row))) {
					flag = false;
					break;
				}
				row++;
			}
		}
		return flag;
	}
	
	/**
	 * 放得下：没牌或上面连得上
	 */
	private boolean canPut(int column) {
		boolean flag = false;
		List<Poker> list = playStack.get(column);
		if (movingStack.size() > 0) {
			// 没牌
			if (list.size() == 0) {
				flag = true;
			} else {
				// 上面连得上
				if (movingStack.get(0).isConnectableUpTo(list.get(list.size() - 1))) {
					flag = true;
				}
			}
		}
		return flag;
	}

	/**
	 * 取出卡牌，放到移动栈 
	 */
	public void getCardsFromPlayStack(int column, Poker p) {
		Logger.info("get moving...");
		List<Poker> list = playStack.get(column);
		int row = getRow(list, p);
		if (canGet(column, row)) {
			// 取出，从上到下，正序，方便之后取出
			for (int i = row; i < list.size(); i++) {
				movingStack.add(list.get(i));
			}
			for (int i = list.size() - 1; i >= row; i--) {
				list.remove(i);
			}
			// 更新原列表
			playStack.put(column, list);
			getColumn = column;
		} else {
			Logger.info("can't get at play column"+column);
		}
	}

	public void getCardFromFreeCell(int column) {
		Logger.info("get moving...");
		Poker p = freeCell.get(column);
		if (!p.isEmpty()) {
			movingStack.add(p);
			freeCell.put(column, new Poker());
		} else {
			Logger.info("can't get at free column"+column);
		}
	}
	
	/**
	 * 将卡牌放入目标，不能放就回到原位
	 */
	public void tryMoveToPlayStack(int column) {
		if (canPut(column)) {
			Logger.info("put to column"+column);
			moveToPlayStack(column);
		} else {
			Logger.info("put back to"+getColumn);
			moveToPlayStack(getColumn);
		}
	}
	
	public void moveBack() {
		Logger.info("eat some TV!");
		moveToPlayStack(getColumn);
	}
	
	private void moveToPlayStack(int column) {
		List<Poker> list = playStack.get(column);
		// 放回原列表
		for (int i = 0; i < movingStack.size(); i++) {
			list.add(movingStack.get(i));
		}
		// 清除列表，let GC do the rest
		movingStack = new ArrayList<Poker>();
		// 更新原列表
		playStack.put(column, list);
		gameStateCheck();
	}

	/**
	 * 尝试移动到空当区域
	 * 如果只移动了1张牌，并且目标空当区域是空的，就ok
	 * 不然给我回去
	 */
	public void tryMoveToFreeCell(int column) {
		if (movingStack.size() == 1 && freeCell.get(column).isEmpty()) {
			moveToFreeCell(column);
		} else {
			moveToPlayStack(getColumn);
		}
	}
	
	private void moveToFreeCell(int column) {
		freeCell.put(column, movingStack.get(0));
		// 清除列表，let GC do the rest
		movingStack = new ArrayList<Poker>();
		gameStateCheck();
	}
	
	/**
	 * 尝试移动到最终区域
	 * 如果只移动了1张牌，并且花色正确，并且数字对的上（或者A进空）就ok
	 * 不然给我回去
	 */
	public void tryMoveToFinalStack(int column) {
		for (Suit suit : Suit.values()) {
			Logger.info(suit.ordinal() +","+ movingStack.size());
			if (suit.ordinal()==column && movingStack.size() == 1) {
				Poker p = movingStack.get(0);
				if (p.getSuit().equals(suit) && (
						(p.getNumber()==1 && finalStack.get(suit).isEmpty()) 
						||(p.getNumber() - finalStack.get(suit).getNumber() == 1)
						)) {				
					moveToFinalStack(suit);
				} else {
					moveToPlayStack(getColumn);
				}
			}
		}
	}
	
	private void moveToFinalStack(Suit suit) {
		finalStack.put(suit, movingStack.get(0));
		// 清除列表，let GC do the rest
		movingStack = new ArrayList<Poker>();
		gameStateCheck();
	}

	private void gameStateCheck() {
		if (isGameOver()) {
			Logger.info("boom111");
		} else {
			Logger.info("possible move = " + possibleMove);
		}
	}
	
	//////////////////////////////////////////////////////

	public Map<Integer, Poker> getFreeCell() {
		return freeCell;
	}

	public Map<Suit, Poker> getFinalStack() {
		return finalStack;
	}

	public Map<Integer, List<Poker>> getPlayStack() {
		return playStack;
	}

	public List<Poker> getMovingStack() {
		return movingStack;
	}
	
	public long getStartTime() {
		return startTime;
	}

	public int getMoveCount() {
		return moveCount;
	}

	public int getPossibleMove() {
		return possibleMove;
	}

	public int getSeed() {
		return seed;
	}
	
	public void debug() {
		for (Poker poker : freeCell.values()) {
			System.out.print(poker.toString()+"\t");
		}
		for (Poker poker : finalStack.values()) {
			System.out.print(poker.toString()+"\t");
		}
		System.out.println();
		boolean flag = true;
		int row = 0;
		while(flag) {
			flag = false;
			for (int col = 0; col < PLAY_SIZE; col++) {
				if (playStack.get(col)!=null 
				 && playStack.get(col).size()>row 
				 && playStack.get(col).get(row) != null) {
					flag = true;
					System.out.print(playStack.get(col).get(row).toString()+"\t");
				} else {
					System.out.print("\t");
				}
			}
			System.out.println();
			row++;
		}
		for (Poker poker : movingStack) {
			System.out.print("m"+poker.toString()+"\t");
		}
		System.out.println();
	}
	
}
