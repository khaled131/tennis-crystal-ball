package org.strangeforest.tcb.stats.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.strangeforest.tcb.stats.model.*;
import org.strangeforest.tcb.stats.model.table.*;
import org.strangeforest.tcb.stats.service.*;
import org.strangeforest.tcb.stats.util.*;

import com.google.common.collect.*;

import static org.strangeforest.tcb.stats.util.OrderBy.*;

@RestController
public class PlayerMatchesResource {

	@Autowired private MatchesService matchesService;
	@Autowired private StatisticsService statisticsService;

	private static final int MAX_MATCHES = 10000;

	private static Map<String, String> ORDER_MAP = ImmutableMap.<String, String>builder()
		.put("date", "date")
		.put("tournament", "tournament")
		.put("surface", "surface")
		.put("round", "round")
		.put("wonLost", "CASE WHEN pw.player_id = :playerId THEN 1 ELSE 0 END")
	.build();
	private static final OrderBy[] DEFAULT_ORDERS = new OrderBy[] {desc("date"), desc("round"), desc("match_num")};

	@GetMapping("/matchesTable")
	public BootgridTable<Match> matchesTable(
		@RequestParam(name = "playerId") int playerId,
		@RequestParam(name = "season", required = false) Integer season,
		@RequestParam(name = "level", required = false) String level,
		@RequestParam(name = "surface", required = false) String surface,
		@RequestParam(name = "tournamentId", required = false) Integer tournamentId,
		@RequestParam(name = "tournamentEventId", required = false) Integer tournamentEventId,
		@RequestParam(name = "round", required = false) String round,
		@RequestParam(name = "opponent", required = false) String opponent,
		@RequestParam(name = "outcome", required = false) String outcome,
		@RequestParam(name = "score", required = false) String score,
		@RequestParam(name = "statsCategory", required = false) String statsCategory,
		@RequestParam(name = "statsFrom", required = false) Double statsFrom,
		@RequestParam(name = "statsTo", required = false) Double statsTo,
		@RequestParam(name = "countryId", required = false) String countryId,
		@RequestParam(name = "current") int current,
		@RequestParam(name = "rowCount") int rowCount,
		@RequestParam(name = "searchPhrase") String searchPhrase,
		@RequestParam Map<String, String> requestParams
	) {
		OpponentFilter opponentFilter = OpponentFilter.forMatches(opponent, matchesService.getSameCountryIds(countryId));
		OutcomeFilter outcomeFilter = OutcomeFilter.forMatches(outcome);
		ScoreFilter scoreFilter = ScoreFilter.forMatches(score);
		StatsFilter statsFilter = new StatsFilter(statsCategory, statsFrom, statsTo);
		MatchFilter filter = MatchFilter.forMatches(season, level, surface, tournamentId, tournamentEventId, round, opponentFilter, outcomeFilter, scoreFilter, statsFilter, searchPhrase);
		String orderBy = BootgridUtil.getOrderBy(requestParams, ORDER_MAP, DEFAULT_ORDERS);
		int pageSize = rowCount > 0 ? rowCount : MAX_MATCHES;
		return matchesService.getPlayerMatchesTable(playerId, filter, orderBy, pageSize, current);
	}
	
	@GetMapping("/matchesStat")
	public Number matchesStat(
		@RequestParam(name = "playerId") int playerId,
		@RequestParam(name = "season", required = false) Integer season,
		@RequestParam(name = "level", required = false) String level,
		@RequestParam(name = "surface", required = false) String surface,
		@RequestParam(name = "tournamentId", required = false) Integer tournamentId,
		@RequestParam(name = "tournamentEventId", required = false) Integer tournamentEventId,
		@RequestParam(name = "round", required = false) String round,
		@RequestParam(name = "opponent", required = false) String opponent,
		@RequestParam(name = "score", required = false) String score,
		@RequestParam(name = "outcome", required = false) String outcome,
		@RequestParam(name = "statsCategory", required = false) String statsCategory,
		@RequestParam(name = "countryId", required = false) String countryId,
		@RequestParam(name = "searchPhrase") String searchPhrase
	) {
		OpponentFilter opponentFilter = OpponentFilter.forStats(opponent, matchesService.getSameCountryIds(countryId));
		OutcomeFilter outcomeFilter = OutcomeFilter.forStats(outcome);
		ScoreFilter scoreFilter = ScoreFilter.forStats(score);
		MatchFilter filter = MatchFilter.forStats(season, level, surface, tournamentId, tournamentEventId, round, opponentFilter, outcomeFilter, scoreFilter, StatsFilter.ALL, searchPhrase);
		PlayerStats stats = statisticsService.getPlayerStats(playerId, filter);
		return StatsCategory.get(statsCategory).getStat(stats);
	}
}
