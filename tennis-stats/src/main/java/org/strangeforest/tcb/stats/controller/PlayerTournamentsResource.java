package org.strangeforest.tcb.stats.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.strangeforest.tcb.stats.model.*;
import org.strangeforest.tcb.stats.model.table.*;
import org.strangeforest.tcb.stats.service.*;
import org.strangeforest.tcb.stats.util.*;

import com.google.common.collect.*;

@RestController
public class PlayerTournamentsResource {

	@Autowired private TournamentService tournamentService;
	@Autowired private StatisticsService statisticsService;

	private static final int MAX_TOURNAMENTS = 1000;

	private static Map<String, String> ORDER_MAP = ImmutableMap.<String, String>builder()
		.put("season", "season")
		.put("date", "date")
		.put("name", "name")
		.put("surface", "surface")
		.put("draw", "draw_type, draw_size")
		.put("participationPoints", "participation_points NULLS LAST")
		.put("participationPct", "participation_points::REAL / max_participation_points NULLS LAST")
		.put("result", "result")
	.build();
	private static final OrderBy DEFAULT_ORDER = OrderBy.desc("date");

	@GetMapping("/playerTournamentsTable")
	public BootgridTable<PlayerTournamentEvent> playerTournamentsTable(
		@RequestParam(name = "playerId") int playerId,
		@RequestParam(name = "season", required = false) Integer season,
		@RequestParam(name = "level", required = false) String level,
		@RequestParam(name = "surface", required = false) String surface,
		@RequestParam(name = "tournamentId", required = false) Integer tournamentId,
		@RequestParam(name = "result", required = false) String result,
		@RequestParam(name = "statsCategory", required = false) String statsCategory,
		@RequestParam(name = "statsFrom", required = false) Double statsFrom,
		@RequestParam(name = "statsTo", required = false) Double statsTo,
		@RequestParam(name = "current") int current,
		@RequestParam(name = "rowCount") int rowCount,
		@RequestParam(name = "searchPhrase") String searchPhrase,
		@RequestParam Map<String, String> requestParams
	) {
		StatsFilter statsFilter = new StatsFilter(statsCategory, statsFrom, statsTo);
		TournamentEventResultFilter filter = new TournamentEventResultFilter(season, level, surface, tournamentId, result, statsFilter, searchPhrase);
		String orderBy = BootgridUtil.getOrderBy(requestParams, ORDER_MAP, DEFAULT_ORDER);
		int pageSize = rowCount > 0 ? rowCount : MAX_TOURNAMENTS;
		return tournamentService.getPlayerTournamentEventResultsTable(playerId, filter, orderBy, pageSize, current);
	}

	@GetMapping("/playerTournamentsStat")
	public Number playerTournamentsTable(
		@RequestParam(name = "playerId") int playerId,
		@RequestParam(name = "season", required = false) Integer season,
		@RequestParam(name = "level", required = false) String level,
		@RequestParam(name = "surface", required = false) String surface,
		@RequestParam(name = "tournamentId", required = false) Integer tournamentId,
		@RequestParam(name = "result", required = false) String result,
		@RequestParam(name = "statsCategory", required = false) String statsCategory,
		@RequestParam(name = "searchPhrase") String searchPhrase
	) {
		MatchFilter filter = MatchFilter.forStats(season, level, surface, tournamentId, result, StatsFilter.ALL, searchPhrase);
		PlayerStats stats = statisticsService.getPlayerStats(playerId, filter);
		return StatsCategory.get(statsCategory).getStat(stats);
	}
}
