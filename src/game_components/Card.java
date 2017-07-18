package game_components;

/**
 * Represents a playing card, including rank and suit. 
 * 
 * @author Patrick Wamsley
 */
public class Card implements Comparable<Card> {
	
	/**
	 * Dummy card used to avoid using null. 
	 */
	public static final Card BLUE_EYES_WHITE_DRAGON = new Card(Suit.SPADES, -10); 
	
	public static enum Suit {HEARTS, DIAMONDS, SPADES, CLUBS} 
	
	public static final int TWO		= 2, 
							THREE	= 3, 
							FOUR	= 4, 
							FIVE 	= 5, 
							SIX 	= 6,
							SEVEN	= 7,
							EIGHT	= 8,
							NINE	= 9,
							TEN		= 10,
							JACK	= 11,
							QUEEN	= 12,
							KING	= 13,
							ACE		= 14; 
	
	public final Suit suit; 
	
	public final int rank; 
	
	public Card(Suit suit, int rank) {
		this.suit = suit;
		this.rank = rank; 
	}

	@Override
	public int compareTo(Card other) {
		return other.rank - this.rank; 
	}
	
	@Override
	public String toString() {
		String rankName; 
		
		if (rank == JACK)
			rankName = "Jack"; 
		if (rank == QUEEN)
			rankName = "Queen"; 
		if (rank == KING)
			rankName = "King"; 
		if (rank == ACE)
			rankName = "Ace"; 
		else rankName = "" + rank;
		
		return rankName + " of " + suit; 
	}

}
