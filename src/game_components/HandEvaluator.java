package game_components;

/**
 * Statically used to evaulate {@code Unmade Hands} into {@code MadeHands}
 * @author Patrick Wamsley
 */
public class HandEvaluator {

	public static enum HandType {
		
		STRAIGHT_FLUSH(8), QUADS(7), FULL_HOUSE(6), FLUSH(5), STRAIGHT(4), TRIPS(3), TWO_PAIR(2), PAIR(1), HIGH_CARD(0); 
		
		private final int stregnth; 
		HandType(int strength) {
			this.stregnth = strength; 
		}
		
		public int compareStrenghts(HandType other) {
			return this.stregnth - other.stregnth; 
		}
	}
	
	public static MadeHand evaluate(UnmadeHand pre) {
		return null; 
	}
	
}
