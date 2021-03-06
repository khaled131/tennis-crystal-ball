package org.strangeforest.tcb.stats.model.prediction;

import static java.lang.Math.*;
import static org.strangeforest.tcb.stats.model.prediction.RankingPredictionItem.*;

public class RankingMatchPredictor implements MatchPredictor {

	private final RankingData rankingData1;
	private final RankingData rankingData2;

	private static final int DEFAULT_RANK = 500;
	private static final int DEFAULT_RANK_POINTS = 0;
	private static final int DEFAULT_ELO_RATING = 1500;

	public RankingMatchPredictor(RankingData rankingData1, RankingData rankingData2) {
		this.rankingData1 = rankingData1;
		this.rankingData2 = rankingData2;
	}

	@Override public PredictionArea getArea() {
		return PredictionArea.RANKING;
	}

	@Override public MatchPrediction predictMatch() {
		MatchPrediction prediction = new MatchPrediction();
		addRankItemProbabilities(prediction, RANK, rankingData1.getRank(), rankingData2.getRank());
		addRankPointsItemProbabilities(prediction, RANK_POINTS, rankingData1.getRankPoints(), rankingData2.getRankPoints());
		addEloItemProbabilities(prediction, ELO, rankingData1.getEloRating(), rankingData2.getEloRating());
		addEloItemProbabilities(prediction, SURFACE_ELO, rankingData1.getSurfaceEloRating(), rankingData2.getSurfaceEloRating());
		return prediction;
	}


	// Rank

	private void addRankItemProbabilities(MatchPrediction prediction, RankingPredictionItem item, Integer rank1, Integer rank2) {
		double weight = item.getWeight() * presenceWeight(rank1, rank2);
		if (weight > 0.0) {
			prediction.addItemProbability1(getArea(), item, weight, rankWinProbability(rank1, rank2));
			prediction.addItemProbability2(getArea(), item, weight, rankWinProbability(rank2, rank1));
		}
	}

	private static double rankWinProbability(Integer rank1, Integer rank2) {
		rank1 = defaultIfNull(rank1, DEFAULT_RANK);
		rank2 = defaultIfNull(rank2, DEFAULT_RANK);
		return 1 / (1 + pow(2, log(rank1) - log(rank2)));
	}


	// Rank Points

	private void addRankPointsItemProbabilities(MatchPrediction prediction, RankingPredictionItem item, Integer rankPoints1, Integer rankPoints2) {
		double weight = item.getWeight() * presenceWeight(rankPoints1, rankPoints2);
		if (weight > 0.0) {
			prediction.addItemProbability1(getArea(), item, weight, rankPointsWinProbability(rankPoints1, rankPoints2));
			prediction.addItemProbability2(getArea(), item, weight, rankPointsWinProbability(rankPoints2, rankPoints1));
		}
	}

	private static double rankPointsWinProbability(Integer rankPoints1, Integer rankPoints2) {
		rankPoints1 = defaultIfNull(rankPoints1, DEFAULT_RANK_POINTS);
		rankPoints2 = defaultIfNull(rankPoints2, DEFAULT_RANK_POINTS);
		return 1 / (1 + pow(E, log(rankPoints2) - log(rankPoints1)));
	}


	// Elo

	private void addEloItemProbabilities(MatchPrediction prediction, RankingPredictionItem item, Integer eloRating1, Integer eloRating2) {
		double weight = item.getWeight() * presenceWeight(eloRating1, eloRating2);
		if (weight > 0.0) {
			prediction.addItemProbability1(getArea(), item, weight, eloWinProbability(eloRating1, eloRating2));
			prediction.addItemProbability2(getArea(), item, weight, eloWinProbability(eloRating2, eloRating1));
		}
	}

	private static double eloWinProbability(Integer eloRating1, Integer eloRating2) {
		eloRating1 = defaultIfNull(eloRating1, DEFAULT_ELO_RATING);
		eloRating2 = defaultIfNull(eloRating2, DEFAULT_ELO_RATING);
		return 1 / (1 + pow(10.0, (eloRating2 - eloRating1) / 400.0));
	}


	// Util

	private static double presenceWeight(Integer value1, Integer value2) {
		boolean present1 = isPresent(value1);
		boolean present2 = isPresent(value2);
		if (present1 && present2)
			return 1.0;
		else if (!present1 && !present2)
			return 0.0;
		else
			return 0.5;
	}

	private static Integer defaultIfNull(Integer value, Integer defaultValue) {
		return isPresent(value) ? value : defaultValue;
	}

	private static boolean isPresent(Integer value) {
		return value != null && value != 0;
	}
}
