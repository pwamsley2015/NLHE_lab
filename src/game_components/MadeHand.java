package game_components;

import game_components.HandEvaluator.HandType; 

import static game_components.HandEvaluator.HandType.*;


/**
 * Represents a made, 5 card HE hand, including information such as hand type and high card. 
 * 
 * @see HandEvaluator
 * @see HandEvaluator.HandType 
 * @author Patrick Wamsley
 */
public class MadeHand implements Comparable<MadeHand> {
	
	/**
	 * The high card of this hand, ie the Ace in a nut flush, the 8 in an 8-high straight, or the King in King-high. 
	 */
	public final int highCardRank; 
	
	public final HandType handType;  
	
	public MadeHand(int highCardRank, HandType handType) {
		this.highCardRank = highCardRank; 
		this.handType = handType; 
	}

	@Override
	public int compareTo(MadeHand other) {
		int deltaStregnths =  handType.compareStrenghts(other.handType); 
		return deltaStregnths != 0 ? deltaStregnths : this.highCardRank - other.highCardRank; 
	}
	

}
