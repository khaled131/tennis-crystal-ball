package org.strangeforest.tcb.stats.model.records.categories;

import org.strangeforest.tcb.stats.model.records.*;
import org.strangeforest.tcb.stats.model.records.details.*;

import static java.util.Arrays.*;
import static org.strangeforest.tcb.stats.model.records.RecordDomain.*;
import static org.strangeforest.tcb.stats.model.records.categories.ItemsWinningTitleCategory.ItemType.*;

public class ItemsWinningTitleCategory extends RecordCategory {

	public enum RecordType {
		LEAST("Least", "r.value, r.matches DESC"),
		MOST("Most", "r.value DESC, r.matches");

		private final String name;
		private final String order;

		RecordType(String name, String order) {
			this.name = name;
			this.order = order;
		}
	}

	enum ItemType {
		GAMES("Games", "o_games"),
		SETS("Sets", "o_sets");

		private final String name;
		private final String column;

		ItemType(String name, String column) {
			this.name = name;
			this.column = column;
		}
	}

	private static final String ITEMS_WIDTH =       "80";
	private static final String TOURNAMENT_WIDTH = "120";
	private static final String SEASON_WIDTH =      "80";
	private static final String MATCHES_WIDTH =     "80";

	public ItemsWinningTitleCategory(RecordType type) {
		super(type.name + " Games / Sets Lost Winning Title");
		register(itemsLostWinningTitle(type, GAMES, ALL));
		register(itemsLostWinningTitle(type, GAMES, GRAND_SLAM));
		register(itemsLostWinningTitle(type, GAMES, TOUR_FINALS));
		register(itemsLostWinningTitle(type, GAMES, MASTERS));
		register(itemsLostWinningTitle(type, GAMES, OLYMPICS));
		register(itemsLostWinningTitle(type, GAMES, ATP_500));
		register(itemsLostWinningTitle(type, GAMES, ATP_250));
		register(itemsLostWinningTitle(type, GAMES, HARD, surfaceTournaments("H", "e.")));
		register(itemsLostWinningTitle(type, GAMES, CLAY, surfaceTournaments("C", "e.")));
		register(itemsLostWinningTitle(type, GAMES, GRASS, surfaceTournaments("G", "e.")));
		register(itemsLostWinningTitle(type, GAMES, CARPET, surfaceTournaments("P", "e.")));
		register(itemsLostWinningTitle(type, SETS, GRAND_SLAM));
		register(itemsLostWinningTitle(type, SETS, TOUR_FINALS));
		register(itemsLostWinningTitle(type, SETS, MASTERS));
		register(itemsLostWinningTitle(type, SETS, OLYMPICS));
	}

	private static Record itemsLostWinningTitle(RecordType type, ItemType item, RecordDomain domain) {
		return itemsLostWinningTitle(type, item, domain, null);
	}

	private static Record itemsLostWinningTitle(RecordType type, ItemType item, RecordDomain domain, String condition) {
		if (condition == null)
			condition = domain.condition;
		return new Record<>(
			type.name + item.name + "LostWinning" + domain.id + "Title", suffix(type.name, " ") + item.name + " Lost Winning " + suffix(domain.name, " ") + "Title",
			/* language=SQL */
			"SELECT player_id, tournament_event_id, e.name AS tournament, e.level, e.season, e.date, sum(m." + item.column + ") AS value, count(m.match_id) AS matches\n" +
			"FROM player_tournament_event_result r INNER JOIN tournament_event e USING (tournament_event_id) LEFT JOIN player_match_for_stats_v m USING (player_id, tournament_event_id)\n" +
			"WHERE result = 'W' AND e." + condition + "\n" +
			"GROUP BY player_id, tournament_event_id, e.name, e.level, e.season, e.date HAVING count(m.match_id) >= 3",
			"r.value, r.tournament_event_id, r.tournament, r.level, r.season, r.matches", type.order, type.order + ", r.date",
			TournamentEventIntegerRecordDetail.class, null,
			asList(
				new RecordColumn("value", "numeric", null, ITEMS_WIDTH, "right", item.name),
				new RecordColumn("matches", "numeric", null, MATCHES_WIDTH, "right", "Matches"),
				new RecordColumn("season", "numeric", null, SEASON_WIDTH, "center", "Season"),
				new RecordColumn("tournament", null, "tournamentEvent", TOURNAMENT_WIDTH, "left", "Tournament")
			),
			"Minimum 3 matches played to won the title"
		);
	}
}
