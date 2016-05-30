package org.strangeforest.tcb.stats.model.records;

import static java.util.Arrays.*;
import static org.strangeforest.tcb.stats.model.records.RecordRowFactory.*;

class GreatestMatchesPctCategory extends RecordCategory {

	private static final String PCT_WIDTH =        "100";
	private static final String WON_WIDTH =         "60";
	private static final String LOST_WIDTH =        "60";
	private static final String PLAYED_WIDTH =      "60";
	private static final String SEASON_WIDTH =      "60";
	private static final String TOURNAMENT_WIDTH = "100";

	enum RecordType {
		WINNING("Winning", "_won", "wonLostPct", "winner_id", "match_for_stats_v", WINNING_PCT, SEASON_WINNING_PCT, TOURNAMENT_WINNING_PCT,
			new RecordColumn("won", "numeric", null, WON_WIDTH, "right", "Won")
		),
		LOSING("Losing", "_lost", "lostWonPct", "loser_id", "match_for_stats_v", LOSING_PCT, SEASON_LOSING_PCT, TOURNAMENT_LOSING_PCT,
			new RecordColumn("lost", "numeric", null, LOST_WIDTH, "right", "Lost")
		);

		final String name;
		final String columnSuffix;
		final String pctAttr;
		final String playerColumn;
		final String tableName;
		final RecordRowFactory rowFactory;
		final RecordRowFactory seasonRowFactory;
		final RecordRowFactory tournamentRowFactory;
		final RecordColumn valueRecordColumn;

		RecordType(String name, String column, String pctAttr, String playerColumn, String tableName,
		           RecordRowFactory rowFactory, RecordRowFactory seasonRowFactory, RecordRowFactory tournamentRowFactory, RecordColumn valueRecordColumn) {
			this.name = name;
			this.columnSuffix = column;
			this.pctAttr = pctAttr;
			this.playerColumn = playerColumn;
			this.tableName = tableName;
			this.rowFactory = rowFactory;
			this.seasonRowFactory = seasonRowFactory;
			this.tournamentRowFactory = tournamentRowFactory;
			this.valueRecordColumn = valueRecordColumn;
		}

		String expression(String prefix) {
			return prefix + columnSuffix + "::real/(" + prefix + "_won + " + prefix + "_lost)";
		}
	}

	GreatestMatchesPctCategory(RecordType type) {
		super("Greatest " + type.name + " Pct.");
		register(greatestMatchPct(N_A, type, N_A, N_A, "matches"));
		register(greatestMatchPct(GRAND_SLAM, type, GRAND_SLAM_NAME, "grand_slam_", "grandSlamMatches"));
		register(greatestMatchPct(TOUR_FINALS, type, TOUR_FINALS_NAME, "tour_finals_", "tourFinalsMatches"));
		register(greatestMatchPct(MASTERS, type, MASTERS_NAME, "masters_", "mastersMatches"));
		register(greatestMatchPct(OLYMPICS, type, OLYMPICS_NAME, "olympics_", "olympicsMatches"));
		register(greatestMatchPct(HARD, type, HARD_NAME, "hard_", "hardMatches"));
		register(greatestMatchPct(CLAY, type, CLAY_NAME, "clay_", "clayMatches"));
		register(greatestMatchPct(GRASS, type, GRASS_NAME, "grass_", "grassMatches"));
		register(greatestMatchPct(CARPET, type, CARPET_NAME, "carpet_", "carpetMatches"));
		register(greatestMatchPctVs(NO_1, type, NO_1_NAME, "no1"));
		register(greatestMatchPctVs(TOP_5, type, TOP_5_NAME, "top5"));
		register(greatestMatchPctVs(TOP_10, type, TOP_10_NAME, "top10"));
		register(greatestSeasonMatchPct(type));
		//TODO For single tournament
	}

	private static Record greatestMatchPct(String id, RecordType type, String name, String columnPrefix, String perfCategory) {
		return new Record(
			id + type.name + "Pct", "Greatest " + suffixSpace(name) + type.name + " Pct.",
			"SELECT player_id, " + type.expression(columnPrefix + "matches") + " AS pct, " + columnPrefix + "matches_won AS won, " + columnPrefix + "matches_lost AS lost\n" +
			"FROM player_performance WHERE " + columnPrefix + "matches_won + " + columnPrefix + "matches_lost >= performance_min_entries('" + perfCategory + "')",
			"r.pct, r.won, r.lost", "r.pct DESC", "r.pct DESC", type.rowFactory,
			asList(
				new RecordColumn(type.pctAttr , null, null, PCT_WIDTH, "right", suffixSpace(name) + type.name + " Pct."),
				type.valueRecordColumn,
				new RecordColumn("played", "numeric", null, PLAYED_WIDTH, "right", "Played")
			)
		);
	}

	private static Record greatestMatchPctVs(String id, RecordType type, String name, String column) {
		return new Record(
			type.name + "PctVs" + id, "Greatest " + type.name + " Pct. Vs. " + name,
			"SELECT player_id, " + type.expression("vs_" + column) + " AS pct, vs_" + column + "_won AS won, vs_" + column + "_lost AS lost\n" +
			"FROM player_performance WHERE vs_" + column + "_won + vs_" + column + "_lost >= performance_min_entries('vs" + id + "')",
			"r.pct, r.won, r.lost", "r.pct DESC", "r.pct DESC", type.rowFactory,
			asList(
				new RecordColumn(type.pctAttr , null, null, PCT_WIDTH, "right", type.name + " Pct. Vs. " + name),
				type.valueRecordColumn,
				new RecordColumn("played", "numeric", null, PLAYED_WIDTH, "right", "Played")
			)
		);
	}

	private static Record greatestSeasonMatchPct(RecordType type) {
		return new Record(
			"SeasonGreatest" + type.name + "Pct", "Greatest " + type.name + " Pct. in Single Season",
			"SELECT player_id, season, " + type.expression("matches") + " AS pct, matches_won AS won, matches_lost AS lost\n" +
			"FROM player_season_performance WHERE matches_won + matches_lost >= performance_min_entries('matches') / 10",
			"r.pct, r.won, r.lost, r.season", "r.pct DESC", "r.pct DESC, r.season", type.seasonRowFactory,
			asList(
				new RecordColumn(type.pctAttr , null, null, PCT_WIDTH, "right", type.name + " Pct."),
				type.valueRecordColumn,
				new RecordColumn("played", "numeric", null, PLAYED_WIDTH, "right", "Played"),
				new RecordColumn("season", "numeric", null, SEASON_WIDTH, "center", "Season")
			)
		);
	}
}
