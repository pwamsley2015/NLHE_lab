package game_components;

import static game_components.Card.ACE;
import static game_components.Card.BLUE_EYES_WHITE_DRAGON;
import static game_components.Card.FIVE;
import static game_components.Card.FOUR;
import static game_components.Card.THREE;
import static game_components.Card.TWO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import game_components.Card.Suit; 

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
			return other.stregnth - this.stregnth; 
		}
	}

	/**
	 * Evaulates an Unmade 7-card hand into the best 5 card HE hand. 
	 */
	public static MadeHand evaluate(UnmadeHand pre) {

		HashMap<Integer, Integer> rankHistogram = getRankHistogram(pre); 
		HashMap<Suit, Integer> suitHistogram = getSuitHistogram(pre); 

		HandType handType = null; 

		if (isStraightFlush(pre, suitHistogram))
			handType = HandType.STRAIGHT_FLUSH;
		else if (isQuads(rankHistogram))
			handType = HandType.QUADS;
		else if (isFullHouse(rankHistogram))
			handType = HandType.FULL_HOUSE; 
		else if (isFlush(suitHistogram))
			handType = HandType.FLUSH; 
		else if (isStraight(pre))
			handType = HandType.STRAIGHT; 
		else if (isTrips(rankHistogram))
			handType = HandType.TRIPS; 
		else if (isTwoPair(rankHistogram))
			handType = HandType.TWO_PAIR; 
		else if (isPair(rankHistogram))
			handType = HandType.PAIR; 
		else
			handType = HandType.HIGH_CARD; 

		return getHand(pre, handType, rankHistogram, suitHistogram); 
	}

	private static MadeHand getHand(UnmadeHand pre, HandType type, 
			HashMap<Integer, Integer> rankHistogram, HashMap<Suit, Integer> suitHistogram) {

		Card[] fiveCards = new Card[5]; 

		switch (type) {
			case HIGH_CARD: 
				fiveCards = Arrays.copyOf(pre.cards, 5); 
				break; 
			case PAIR:
				fiveCards = getOnePairHand(pre, rankHistogram); 
				break; 
			case TWO_PAIR:
				fiveCards = getTwoPairHand(pre, rankHistogram); 
				break; 
			case TRIPS: 
				fiveCards = getTripsHand(pre, rankHistogram); 
				break; 
			case STRAIGHT:
				fiveCards = getStraightHand(pre); 
				break; 
			case FLUSH:
				fiveCards = getFlushHand(pre, suitHistogram);
				break; 
			case FULL_HOUSE:
				fiveCards = getFullHouseHand(pre, rankHistogram); 
				break; 
			case QUADS: 
				fiveCards = getQuadsHand(pre, rankHistogram); 
				break; 
			case STRAIGHT_FLUSH: 
				fiveCards = getStraightFlushHand(pre, suitHistogram); 
				break;
		}

		/*
		 * Right now, I think all that matters is the card ranks. 
		 * If instead (maybe for gui stuff) we need to keep track of actual cards,
		 * just get rid of this for loop and change MadeHand to keep a Card[] instead of int[]
		 */
		int[] ranks = new int[5];
		for (int i = 0; i < ranks.length; i++)
			ranks[i] = fiveCards[i].rank; 

		return new MadeHand(ranks, type); 
	}

	/**
	 * Returns a {@code Card[]} in the format {STRAIGHT FLUSH HIGH CARD 1... 5}
	 */
	private static Card[] getStraightFlushHand(UnmadeHand pre, HashMap<Suit, Integer> suitHistogram) {
		//suited by suit and rank going into this
		Suit flushedSuit = null; 

		for (Suit s : suitHistogram.keySet()) 
			if (suitHistogram.get(s) >= 5)
				flushedSuit = s; 

		ArrayList<Card> cards = new ArrayList<>(); 
		Card previous = BLUE_EYES_WHITE_DRAGON; 

		for (Card c : pre.cards) {
			if (previous.rank - 1 == c.rank && c.suit == flushedSuit) {
				cards.add(c); 
				if (cards.size() == 5) {
					break; 
				}
			} else {
				cards.clear(); 
				cards.add(c); 
			}
			previous = c; 
		}

		if (cards.size() == 5) {
			return cards.toArray(new Card[] {}); 
		} else {
			//has to be a wheel, cards after the five don't matter. 
			return new Card[] {new Card(flushedSuit, FIVE), BLUE_EYES_WHITE_DRAGON, BLUE_EYES_WHITE_DRAGON,
					BLUE_EYES_WHITE_DRAGON, BLUE_EYES_WHITE_DRAGON}; 
		}

	}

	/**
	 * Returns a {@code Card[]} in the format {QUADS CARD 1... QUADS CARD 4, HIGH CARD}
	 */
	private static Card[] getQuadsHand(UnmadeHand pre, HashMap<Integer, Integer> rankHistogram) {

		ArrayList<Card> fiveCards = new ArrayList<>(); 
		Card highCard = BLUE_EYES_WHITE_DRAGON; 

		for (Card c : pre.cards) {
			if (rankHistogram.get(c.rank) == 4) {
				fiveCards.add(c); 
			} else if (c.rank > highCard.rank) {
				highCard = c; 
			}
		}

		fiveCards.add(highCard); 

		return fiveCards.toArray(new Card[] {}); 
	}

	/**
	 * Returns a {@code Card[]} in the format {TRIPS CARD 1, TRIPS CARD 2, TRIPS CARD 3, PAIR CARD 1, PAIR CARD 2}
	 */
	private static Card[] getFullHouseHand(UnmadeHand pre, HashMap<Integer, Integer> rankHistogram) {

		Arrays.sort(pre.cards);

		//since this is sorted, no need to check for two sets of trips. Just worry about the bigger one, and treat the other as a potential pair
		int rankOfTrips = Integer.MIN_VALUE; 
		for (Integer rank : rankHistogram.keySet()) 
			if (rankHistogram.get(rank) == 3) 
				rankOfTrips = rank; 

		ArrayList<Card> fiveCards = new ArrayList<>();  

		for (Card c : pre.cards) 
			if (c.rank == rankOfTrips)
				fiveCards.add(c); 

		rankHistogram.remove(rankOfTrips); 
		
		for (int i = 0; i < pre.cards.length; i++) 
			if (pre.cards[i].rank == rankOfTrips)
				pre.cards[i] = BLUE_EYES_WHITE_DRAGON; 
	

		//sorted so no high card finding required
		for (Card c : pre.cards) {
			if (c.rank == BLUE_EYES_WHITE_DRAGON.rank) 
				continue; 
			if (rankHistogram.get(c.rank) >= 2) 
				fiveCards.add(c);
			if (fiveCards.size() == 5) 
				break;
		}

		return fiveCards.toArray(new Card[] {}); 
	}


	/**
	 * Returns a {@code Card[]} in the format {SUITED HIGH CARD 1, SUITED HIGH CARD 2... SUITED HIGH CARD 5}
	 */
	private static Card[] getFlushHand(UnmadeHand pre, HashMap<Suit, Integer> suitHistogram) {

		Suit flushedSuit = null;

		for (Suit s : suitHistogram.keySet()) {
			if (suitHistogram.get(s) >= 5) {
				flushedSuit = s; 
				break; 
			}
		}

		ArrayList<Card> suitedCards = new ArrayList<>(); 

		for (Card c : pre.cards) 
			if (c.suit == flushedSuit)
				suitedCards.add(c); 

		Collections.sort(suitedCards);

		//could have more than 5, we just want the 5 highest-ranked, and in order. 
		return Arrays.copyOf(suitedCards.toArray(new Card[] {}), 5); 

	}

	/**
	 * Returns an {@code Card[]} in the format {STRAIGHT HIGH CARD 1 ... 5}
	 */
	private static Card[] getStraightHand(UnmadeHand pre) {

		//pre.cards is sorted going into this
		ArrayList<Card> cards = new ArrayList<>(); 
		Card previous = BLUE_EYES_WHITE_DRAGON; 

		for (Card c : pre.cards) {
			if (previous.rank - 1 == c.rank) {
				if (cards.size() == 0) {
					cards.add(previous);
				}
				cards.add(c); 
				if (cards.size() == 5) {
					break; 
				}
			} else {
				cards.clear(); 
			}
			
			previous = c; 
		}

		if (cards.size() == 5) {
			return cards.toArray(new Card[] {}); 
		} else {
			//has to be a wheel, cards after the five don't matter. 
			return new Card[] {new Card(Suit.HEARTS, FIVE), BLUE_EYES_WHITE_DRAGON, BLUE_EYES_WHITE_DRAGON,
					BLUE_EYES_WHITE_DRAGON, BLUE_EYES_WHITE_DRAGON}; 
		}
	}

	/**
	 * Creates an {@code Card[]} in the format {PAIRED CARD, PAIRED CARD, PAIRED CARD, HIGH CARD 1, HIGH CARD 2} 
	 */
	private static Card[] getTripsHand(UnmadeHand pre, HashMap<Integer, Integer> rankHistogram) {

		Card[] fiveCards = new Card[5]; 
		ArrayList<Card> pairedCards = new ArrayList<>(), highCards = new ArrayList<>(); 

		for (int i = 0; i < pre.cards.length; i++) {
			Card c = pre.cards[i]; 
			if (rankHistogram.get(c.rank) == 3) {
				pairedCards.add(c); 
			} else {
				highCards.add(c); 
			}
		}

		//high cards are sorted going into this, and we've done nothing to break order

		fiveCards[0] = pairedCards.get(0); 
		fiveCards[1] = pairedCards.get(1); 
		fiveCards[2] = pairedCards.get(2); 
		fiveCards[3] = highCards.get(0); 
		fiveCards[4] = highCards.get(1); 

		return fiveCards; 
	}

	/**
	 * Creates an {@code Card[]} in the format {HIGHER PAIRED CARD, HIGHER PAIRED CARD, LOWER PAIRED CARD, LOWER PAIRED CARD, HIGH CARD}
	 */
	private static Card[] getTwoPairHand(UnmadeHand pre, HashMap<Integer, Integer> rankHistogram) {

		Card[] fiveCards = new Card[5]; 
		ArrayList<Card> pairedCards = new ArrayList<>(), highCards = new ArrayList<>(); 

		for (int i = 0; i < pre.cards.length; i++) {
			Card c = pre.cards[i]; 
			if (rankHistogram.get(c.rank) == 2) {
				pairedCards.add(c); 
			} else {
				highCards.add(c); 
			}
		}
		//3 pair case
		if (pairedCards.size() == 6) {
			Card thirdPairCard = pairedCards.get(6);
			highCards.add(pairedCards.remove(5));
			highCards.add(pairedCards.remove(6));
			Collections.sort(highCards); 

		}

		fiveCards[0] = pairedCards.get(0); 
		fiveCards[1] = pairedCards.get(1); 
		fiveCards[2] = pairedCards.get(2); 
		fiveCards[3] = pairedCards.get(3); 
		fiveCards[4] = highCards.get(0); 

		return fiveCards; 

	}

	/**
	 * Creates an {@code Card[]} in the format {PAIRED CARD, PAIRED CARD, HIGH CARD 1, HIGH CARD 2, HIGH CARD 3}
	 */
	private static Card[] getOnePairHand(UnmadeHand pre, HashMap<Integer, Integer> rankHistogram) {

		Card[] fiveCards = new Card[5]; 
		ArrayList<Card> pairedCards = new ArrayList<>(), highCards = new ArrayList<>(); 

		for (int i = 0; i < pre.cards.length; i++) {
			Card c = pre.cards[i]; 
			if (rankHistogram.get(c.rank) == 2) {
				pairedCards.add(c); 
			} else {
				highCards.add(c); 
			}
		}

		//high cards are sorted going into this, and we've done nothing to break order

		fiveCards[0] = pairedCards.get(0); 
		fiveCards[1] = pairedCards.get(1); 

		int handIndex = 2; 
		for (Card c : highCards) {
			fiveCards[handIndex++] = c; 
			if (handIndex == 5)
				break; 
		}

		return fiveCards; 
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
				return deltaSuit != 0 ? deltaSuit : o2.rank - o1.rank; 
			}
		});

		int amountToStraightFlush = 1;
		Card previous = BLUE_EYES_WHITE_DRAGON; 

		for (Card c : hand.cards) {
			if (c.rank + 1 == previous.rank && c.suit == previous.suit) {
				amountToStraightFlush++;
				if (amountToStraightFlush == 5) {
					return true; 
				} 
			} else {
				amountToStraightFlush = 1; 
			}
			previous = c; 
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
			if (c.rank + 1 == previousRank) { 
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
