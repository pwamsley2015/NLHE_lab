package game_components;


/**
 * Represents an unmade HE hand, including 2 whole cards and 5 community cards. 
 * 
 * @author Patrick Wamsley
 */
public class UnmadeHand {

	public final Card[] cards = new Card[7]; 
	private int cardsInHand = 0; 

	public UnmadeHand addCard(Card c) {
		if (cardsInHand < 7) {
			cards[cardsInHand] = c; 
			cardsInHand++; 
		} else {
			throw new IllegalStateException("Tried to deal too many cards to this hand"); 
		}
		return this; 
	}
}
