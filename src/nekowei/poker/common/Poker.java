package nekowei.poker.common;

import nekowei.util.Logger;

/**
 * 黑桃spade,红桃heart,方片diamond,梅花club A ace,J jack,Q queen,K king
 * @author nekowei
 * @date 2016-1-9
 */
public class Poker {
	public enum Suit {
		spade,heart,diamond,club;
	}
	
	private Suit suit;
	private int number;
	/**
	 * 隐藏卡牌本身
	 */
	private boolean empty;
	
	public Poker(Suit suit, int number) {
		this.suit = suit;
		this.number = number;
		this.empty = false;
	}
	
	public Poker() {
		this.empty = true;
	}
	
	public String getFormedSuit() {
		if (suit==Suit.spade) return "♠";
		else if (suit==Suit.heart) return "♥";
		else if (suit==Suit.diamond) return "♦";
		else if (suit==Suit.club) return "♣";
		else return "";
	}
	
	public String getFormedNumber() {
		if (number==13) return "K";
		else if (number==12) return "Q";
		else if (number==11) return "J";
		else if (number==1) return "A";
		else return ""+number;
	}
	
	public boolean isRed() {
		if (suit.equals(Suit.heart) || suit.equals(Suit.diamond)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 判断是否可以连接的上（颜色不同，顺序差1）
	 */
	public boolean isConnectableUpTo(Poker target) {
		if (((this.isRed() && !target.isRed()) || (!this.isRed() && target.isRed()))
				&& target.getNumber() - this.getNumber() == 1) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 重载了eqauls方法，只要花色和数值都相等就认为是等同的扑克对象。
	 */
	@Override
	public boolean equals(Object obj) {
		boolean flag = false;
		if (obj instanceof Poker) {
			Poker o = (Poker) obj;
			if (!this.isEmpty() && !o.isEmpty() 
			 && this.getSuit().equals(o.getSuit()) 
			 && this.getNumber() == o.getNumber()) {
				flag = true;
			}
		}
		return flag;
	}
	
	@Override
	public String toString() {
		String s = "口";
		if (!isEmpty()) {
			s = getFormedSuit() + getFormedNumber();
		} 
		return s;
	}
	
	public Suit getSuit() {
		return suit;
	}
	
	public int getNumber() {
		return number;
	}
	
	public boolean isEmpty() {
		return empty;
	}

	public static void main(String[] args) {
		for (Suit s : Suit.values()) {
			Logger.info(s.ordinal());
		}
	}
	
}
