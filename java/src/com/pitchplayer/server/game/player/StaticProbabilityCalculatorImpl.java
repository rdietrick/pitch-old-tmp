package com.pitchplayer.server.game.player;

import com.pitchplayer.Card;

public class StaticProbabilityCalculatorImpl implements ProbabilityCalculator {

	// static array of probabilities:
	double[][] highProbabilities = {
			{0.014092659, 0.021445349,0.032168023,0.047608677,0.06958191,
				0.10050721,0.14358172,0.20299485,0.2841928,0.3942029,
				0.54202896,0.73913044,1.0},
				{0.0007818368,0.0016096641,0.0032193281,0.006269218,	
					0.011911514,0.022121385,0.040220696,0.071697764,
					0.12547109,0.21581028,0.3652174,0.6086956,1.0}
	};
	
	public float getProbHigh(Card c, int handSize, int numPlayers) {
		return (float)highProbabilities[numPlayers-3][c.getValue()];
	}

	public float getProbLow(Card c, int handSize, int numPlayers) {
		return (float)highProbabilities[numPlayers-3][Card.ACE - c.getValue()];
	}

	public float getProbabilityCardsDealt(int setSize, int handSize,
			int numPlayers) {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getWinnerProbability(int setSize, int handSize, int numPlayers) {
		// TODO Auto-generated method stub
		return 0;
	}

}
