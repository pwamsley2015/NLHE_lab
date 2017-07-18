package test_bed;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import com.sun.org.apache.bcel.internal.generic.NEW;

import game_components.Card;
import game_components.HandEvaluator;
import game_components.MadeHand;
import game_components.UnmadeHand;
import sun.applet.Main;

import static game_components.Card.Suit.*;
import static game_components.Card.*; 
import static game_components.HandEvaluator.HandType.*;

public class HandTypeEvalTests {

	@Test
	public void highCardTest() {

		UnmadeHand pre = new UnmadeHand(); 
		pre.addCard(new Card(DIAMONDS, ACE));
		pre.addCard(new Card(DIAMONDS, JACK));
		pre.addCard(new Card(HEARTS, TWO));
		pre.addCard(new Card(DIAMONDS, SEVEN));
		pre.addCard(new Card(DIAMONDS, THREE));
		pre.addCard(new Card(SPADES, KING)); 
		pre.addCard(new Card(CLUBS, EIGHT)); 

		MadeHand post = HandEvaluator.evaluate(pre); 

		assertEquals(post.handType, HIGH_CARD);
		assertArrayEquals(post.highCardRankings, new int[] {ACE, KING, JACK, EIGHT, SEVEN});
	}

	@Test
	public void pairTest() {
		UnmadeHand pre = new UnmadeHand(); 
		pre.addCard(new Card(DIAMONDS, JACK))
		.addCard(new Card(HEARTS, JACK))
		.addCard(new Card(SPADES, NINE))
		.addCard(new Card(CLUBS, SEVEN))
		.addCard(new Card(DIAMONDS, KING))
		.addCard(new Card(HEARTS, TWO))
		.addCard(new Card(CLUBS, THREE)); 

		MadeHand post = HandEvaluator.evaluate(pre); 

		assertEquals(post.handType, PAIR);
		assertArrayEquals(post.highCardRankings, new int[] {JACK, JACK, KING, NINE, SEVEN});
	}

	@Test
	public void twoPairTest() {
		UnmadeHand pre = new UnmadeHand(); 
		pre.addCard(new Card(DIAMONDS, JACK))
		.addCard(new Card(HEARTS, JACK))
		.addCard(new Card(SPADES, NINE))
		.addCard(new Card(CLUBS, SEVEN))
		.addCard(new Card(DIAMONDS, KING))
		.addCard(new Card(HEARTS, TWO))
		.addCard(new Card(CLUBS, NINE)); 

		MadeHand post = HandEvaluator.evaluate(pre); 

		assertEquals(post.handType, TWO_PAIR); 
		assertArrayEquals(post.highCardRankings, new int[] {JACK, JACK, NINE, NINE, KING});

		UnmadeHand pre3pair = new UnmadeHand(); 
		pre3pair.addCard(new Card(DIAMONDS, JACK))
		.addCard(new Card(HEARTS, JACK))
		.addCard(new Card(SPADES, NINE))
		.addCard(new Card(CLUBS, SEVEN))
		.addCard(new Card(DIAMONDS, KING))
		.addCard(new Card(HEARTS, SEVEN ))
		.addCard(new Card(CLUBS, THREE));

		MadeHand post3pair = HandEvaluator.evaluate(pre3pair); 

		assertEquals(post3pair.handType, TWO_PAIR); 
		assertArrayEquals(post3pair.highCardRankings, new int[] {JACK, JACK, SEVEN, SEVEN, KING});
	}

	@Test
	public void tripsTest() {
		UnmadeHand pre = new UnmadeHand(); 
		pre.addCard(new Card(DIAMONDS, JACK))
		.addCard(new Card(HEARTS, JACK))
		.addCard(new Card(SPADES, NINE))
		.addCard(new Card(CLUBS, JACK))
		.addCard(new Card(DIAMONDS, KING))
		.addCard(new Card(HEARTS, TWO))
		.addCard(new Card(CLUBS, FOUR));

		MadeHand post = HandEvaluator.evaluate(pre);

		assertEquals(post.handType, TRIPS);
		assertArrayEquals(post.highCardRankings, new int[] {JACK, JACK, JACK, KING, NINE});
	}

	@Test
	public void straightTest() {
		UnmadeHand pre = new UnmadeHand(); 
		pre.addCard(new Card(DIAMONDS, TEN))
		.addCard(new Card(HEARTS, JACK))
		.addCard(new Card(SPADES, NINE))
		.addCard(new Card(CLUBS, TWO))
		.addCard(new Card(DIAMONDS, KING))
		.addCard(new Card(HEARTS, TWO))
		.addCard(new Card(CLUBS, QUEEN));

		MadeHand post = HandEvaluator.evaluate(pre);

		assertEquals(post.handType, STRAIGHT);
		assertEquals(post.highCardRankings[0], KING); 

	}

	@Test
	public void flushTest() {
		UnmadeHand pre = new UnmadeHand(); 
		pre.addCard(new Card(HEARTS, TEN))
			.addCard(new Card(HEARTS, JACK))
			.addCard(new Card(SPADES, NINE))
			.addCard(new Card(HEARTS, THREE))
			.addCard(new Card(DIAMONDS, KING))
			.addCard(new Card(HEARTS, TWO))
			.addCard(new Card(HEARTS, ACE));

		MadeHand post = HandEvaluator.evaluate(pre);

		assertEquals(post.handType, FLUSH); 
		assertArrayEquals(post.highCardRankings, new int[] {ACE, JACK, TEN, THREE, TWO});
	}

	@Test
	public void fullHouseTest() {

		UnmadeHand pre = new UnmadeHand(); 
		pre.addCard(new Card(HEARTS, TEN))
			.addCard(new Card(HEARTS, JACK))
			.addCard(new Card(SPADES, TEN))
			.addCard(new Card(HEARTS, THREE))
			.addCard(new Card(CLUBS, JACK))
			.addCard(new Card(SPADES, JACK))
			.addCard(new Card(HEARTS, ACE));
		
		MadeHand post = HandEvaluator.evaluate(pre);

		assertEquals(post.handType, FULL_HOUSE); 
		assertArrayEquals(post.highCardRankings, new int[] {JACK, JACK, JACK, TEN, TEN});
		
		UnmadeHand preDoubleTrips = new UnmadeHand(); 
		preDoubleTrips.addCard(new Card(HEARTS, ACE))
						.addCard(new Card(SPADES, TWO))
						.addCard(new Card(CLUBS, TWO))
						.addCard(new Card(CLUBS, THREE))
						.addCard(new Card(HEARTS, TWO))
						.addCard(new Card(SPADES, THREE))
						.addCard(new Card(HEARTS, THREE)); 
		
		MadeHand postDoubleTrips = HandEvaluator.evaluate(preDoubleTrips);
		
		assertEquals(postDoubleTrips.handType, FULL_HOUSE); 
		assertArrayEquals(postDoubleTrips.highCardRankings, new int[] {THREE, THREE, THREE, TWO, TWO});
	}

	@Test
	public void quadsTest() {
		UnmadeHand pre = new UnmadeHand(); 
		pre.addCard(new Card(HEARTS, ACE))
			.addCard(new Card(SPADES, TWO))
			.addCard(new Card(CLUBS, TWO))
			.addCard(new Card(CLUBS, THREE))
			.addCard(new Card(HEARTS, TWO))
			.addCard(new Card(SPADES, THREE))
			.addCard(new Card(DIAMONDS, TWO)); 
		
		MadeHand post = HandEvaluator.evaluate(pre); 
		assertEquals(post.handType, QUADS);
		assertArrayEquals(post.highCardRankings, new int[] {TWO, TWO , TWO, TWO, ACE});
	}

	@Test
	public void straightFlushTest() {
		UnmadeHand pre = new UnmadeHand(); 
		pre.addCard(new Card(HEARTS, SIX))
			.addCard(new Card(HEARTS, FIVE))
			.addCard(new Card(SPADES, NINE))
			.addCard(new Card(HEARTS, THREE))
			.addCard(new Card(DIAMONDS, KING))
			.addCard(new Card(HEARTS, TWO))
			.addCard(new Card(HEARTS, FOUR));

		MadeHand post = HandEvaluator.evaluate(pre);
		assertEquals(post.handType, STRAIGHT_FLUSH);
		assertEquals(post.highCardRankings[0], SIX);
	}

	@Test
	public void wheelTest() {
		UnmadeHand pre = new UnmadeHand(); 
		pre.addCard(new Card(DIAMONDS, ACE))
			.addCard(new Card(HEARTS, FIVE))
			.addCard(new Card(SPADES, NINE))
			.addCard(new Card(HEARTS, THREE))
			.addCard(new Card(DIAMONDS, KING))
			.addCard(new Card(HEARTS, TWO))
			.addCard(new Card(HEARTS, FOUR));

		MadeHand post = HandEvaluator.evaluate(pre);
		assertEquals(post.handType, STRAIGHT);
		assertEquals(post.highCardRankings[0], FIVE);
	}

	@Test
	public void straightFlushWheelTest() {
		UnmadeHand pre = new UnmadeHand(); 
		pre.addCard(new Card(HEARTS, ACE))
			.addCard(new Card(HEARTS, FIVE))
			.addCard(new Card(SPADES, NINE))
			.addCard(new Card(HEARTS, THREE))
			.addCard(new Card(DIAMONDS, KING))
			.addCard(new Card(HEARTS, TWO))
			.addCard(new Card(HEARTS, FOUR));

		MadeHand post = HandEvaluator.evaluate(pre);
		assertEquals(post.handType, STRAIGHT_FLUSH);
		assertEquals(post.highCardRankings[0], FIVE);
	}


}
