package game_components;

import game_components.HandEvaluator.HandType;


/**
 * Represents a made, 5 card HE hand, including information such as hand type and high card. 
 * 
 * @see HandEvaluator
 * @see HandEvaluator.HandType 
 * @author Patrick Wamsley
 */
public class MadeHand implements Comparable<MadeHand> {

	public final int[] highCardRankings; 

	public final HandType handType;  

	public MadeHand(int[] highCardRankings, HandType handType) {
		if (highCardRankings.length != 5) 
			throw new IllegalArgumentException("Made hands have 5 cards."); 
		this.highCardRankings = highCardRankings; 
		this.handType = handType; 
	}

	@Override
	public int compareTo(MadeHand other) {
		
		int deltaStregnths =  handType.compareStrenghts(other.handType); 
		if (deltaStregnths != 0)
			return deltaStregnths; 

		for (int i = 0; i < highCardRankings.length; i++) {
			int deltaCurrentHighCard = highCardRankings[i] - other.highCardRankings[i];
			
			if (deltaCurrentHighCard != 0)
				return deltaCurrentHighCard; 
		}

		return 0; 
	}

}
