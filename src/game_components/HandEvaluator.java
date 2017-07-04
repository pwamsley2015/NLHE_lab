package game_components;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import game_components.Card.Suit;
import static game_components.Card.*; 

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
		
		HashMap<Integer, Integer> rankHistogram = getRankHistogram(pre); 
		HashMap<Suit, Integer> suitHistogram = getSuitHistogram(pre); 
		
		HandType handType = null; 
		
		if (isStraightFlush(pre, suitHistogram))
			handType = HandType.STRAIGHT_FLUSH;
		if (isQuads(rankHistogram))
			handType = HandType.QUADS;
		if (isFullHouse(rankHistogram))
			handType = HandType.FULL_HOUSE; 
		if (isFlush(suitHistogram))
			handType = HandType.FLUSH; 
		if (isStraight(pre))
			handType = HandType.STRAIGHT; 
		if (isTrips(rankHistogram))
			handType = HandType.TRIPS; 
		if (isTwoPair(rankHistogram))
			handType = HandType.TWO_PAIR; 
		if (isPair(rankHistogram))
			handType = HandType.PAIR; 
		else
			handType = HandType.HIGH_CARD; 
		
		
		
		return null; 
	}
	
	private static HashMap<Integer, Integer> getRankHistogram(UnmadeHand hand) {
		
		HashMap<Integer, Integer> histogram = new HashMap<>(); 
		
		for (Card c : hand.cards) {
			if (histogram.get(c.rank) == null) {
				histogram.put(c.rank, 1); 
			} else {
				histogram.put(c.rank, histogram.get(c.rank) + 1); 
			}
		}
		return histogram; 
	}
	
	private static HashMap<Suit, Integer> getSuitHistogram(UnmadeHand hand) {
		
		HashMap<Suit, Integer> histogram = new HashMap<>(); 
		
		for (Card c : hand.cards) {
			if (histogram.get(c.suit) == null) {
				histogram.put(c.suit, 1); 
			} else {
				histogram.put(c.suit, histogram.get(c.suit) + 1); 
			}
		}
		
		return histogram; 
	}
	
	
	private static boolean isStraightFlush(UnmadeHand hand, HashMap<Suit, Integer> histrogram) {
		
		if (isStraightFlushWheel(hand, histrogram)) {
			return true; 
		}
		
		Arrays.sort(hand.cards, new Comparator<Card>() {

			@Override
			public int compare(Card o1, Card o2) {
				int deltaSuit = o1.compareTo(o2); //don't care how this is ordered
				return deltaSuit != 0 ? deltaSuit : o1.rank - o2.rank; 
			}
		});
		
		int amountToStraightFlush = 1;
		Card previous = BLUE_EYES_WHITE_DRAGON; 
		
		for (Card c : hand.cards) {
			if (c.rank - 1 == previous.rank && c.suit == previous.suit) {
				amountToStraightFlush++;
				if (amountToStraightFlush == 5) {
					return true; 
				} 
			} else {
				amountToStraightFlush = 1; 
			}
		}
		
		return false; 
	}
	
	private static boolean isQuads(HashMap<Integer, Integer> histogram) {
		for (int count : histogram.values()) 
			if (count == 4) 
				return true;
		
		return false;  
	}
	
	private static boolean isFullHouse(HashMap<Integer, Integer> histogram) {
		return isTrips(histogram) && isTwoPair(histogram); 
	}
	
	private static boolean isFlush(HashMap<Suit, Integer> histogram) {
		for (int count : histogram.values()) 
			if (count >= 5) 
				return true;
		
		return false; 
	}
	
	private static boolean isStraight(UnmadeHand hand) {
		
		if (isWheel(hand)) {
			return true; 
		}
		
		Arrays.sort(hand.cards); 
		
		
		int amountToStraight = 1; 
		int previousRank = Integer.MIN_VALUE; 
		for (Card c : hand.cards) {
			if (c.rank - 1 == previousRank) {
				amountToStraight++; 
				if (amountToStraight == 5) {
					return true; 
				}
			} else {
				amountToStraight = 1; 
			}
			previousRank = c.rank; 
		}
		
		return false; 
	}
	
	private static boolean isTrips(HashMap<Integer, Integer> histogram) {
		for (int count : histogram.values()) 
			if (count == 3) 
				return true;
		
		return false; 
	}
	
	private static boolean isTwoPair(HashMap<Integer, Integer> histogram) {
		int numPairs = 0; 
		
		for (int count : histogram.values()) 
			if (count >= 2) 
				numPairs++;
		
		return numPairs >= 2;  
	}
	
	private static boolean isPair(HashMap<Integer, Integer> histogram) {
		
		for (int count : histogram.values()) 
			if (count == 2) 
				return true;
		
		return false; 
	}
	
	private static boolean isStraightFlushWheel(UnmadeHand hand, HashMap<Suit, Integer> histogram) {
		
		Suit flushedSuit = null; 
		
		for (Suit s : histogram.keySet()) 
			if (histogram.get(s) >= 5)
				flushedSuit = s; 
		
		if (flushedSuit == null)
			return false; 
		
		boolean ace = false, two = false, three = false, four = false, five = false; 
		
		for (Card c : hand.cards) {
			if (c.rank == ACE && c.suit == flushedSuit)
				ace = true; 
			if (c.rank == TWO && c.suit == flushedSuit)
				two = true; 
			if (c.rank == THREE && c.suit == flushedSuit)
				three = true; 
			if (c.rank == FOUR && c.suit == flushedSuit)
				four = true; 
			if (c.rank == FIVE && c.suit == flushedSuit)
				five = true; 
		}
		
		return ace && two && three && four && five; 
	}
	
	private static boolean isWheel(UnmadeHand hand) {
		boolean ace = false, two = false, three = false, four = false, five = false;  
		
		for (Card c : hand.cards) {
			if (c.rank == ACE)
				ace = true; 
			if (c.rank == TWO)
				two = true; 
			if (c.rank == THREE)
				three = true; 
			if (c.rank == FOUR)
				four = true; 
			if (c.rank == FIVE)
				five = true; 
		}
		
		return ace && two && three && four && five;
	}
}
