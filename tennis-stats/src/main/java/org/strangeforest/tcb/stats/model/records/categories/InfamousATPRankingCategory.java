package org.strangeforest.tcb.stats.model.records.categories;

import org.strangeforest.tcb.stats.model.records.details.*;

public class InfamousATPRankingCategory extends RankingCategory {

	private static final String ADJUSTMENT_NOTES = "Adjusted by factor 1.9 before 2009";

	public InfamousATPRankingCategory() {
		super("Infamous ATP Ranking");
		register(leastPointsAsNo1("LeastATPPointsAsNo1", "Least ATP Points as No. 1", "player_ranking", "adjust_atp_rank_points(rank_points, rank_date)", "rank_points", "ATP Points", ADJUSTMENT_NOTES));
		register(leastEndOfSeasonPointsAsNo1("LeastEndOfSeasonATPPointsAsNo1", "Least End of Season ATP Points as No. 1", "player_year_end_rank", "adjust_atp_rank_points(year_end_rank_points, season_start(season))", "ATP Points", ADJUSTMENT_NOTES));
		register(pointsDifferenceBetweenNo1andNo2(
			"SmallestATPPointsNo1No2Difference", "Smallest ATP Points Difference Between No. 1 and No. 2", "player_ranking", "rank_points", "rank", N_A,
			"adjust_atp_rank_points(r1.rank_points - r2.rank_points, r1.rank_date)", "adjust_atp_rank_points(r1.rank_points, r1.rank_date)", "adjust_atp_rank_points(r2.rank_points, r1.rank_date)", "r.value",
			RankingDiffRecordDetail.class, "numeric", "Points", "Points Diff.", ADJUSTMENT_NOTES
		));
	}
}
