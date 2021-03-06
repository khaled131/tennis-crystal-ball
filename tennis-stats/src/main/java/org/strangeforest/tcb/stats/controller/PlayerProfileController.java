package org.strangeforest.tcb.stats.controller;

import java.time.*;
import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;
import org.strangeforest.tcb.stats.model.*;
import org.strangeforest.tcb.stats.model.records.*;
import org.strangeforest.tcb.stats.service.*;
import org.strangeforest.tcb.util.*;

import com.neovisionaries.i18n.*;

import static com.google.common.base.Strings.*;
import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;
import static org.strangeforest.tcb.stats.controller.StatsFormatUtil.*;

@Controller
public class PlayerProfileController extends PageController {

	@Autowired private PlayerService playerService;
	@Autowired private RivalriesService rivalriesService;
	@Autowired private TournamentService tournamentService;
	@Autowired private RankingsService rankingsService;
	@Autowired private PlayerTimelineService timelineService;
	@Autowired private PerformanceService performanceService;
	@Autowired private StatisticsService statisticsService;
	@Autowired private GOATPointsService goatPointsService;

	@GetMapping("/playerProfile")
	public ModelAndView playerProfile(
		@RequestParam(name = "playerId", required = false) Integer playerId,
		@RequestParam(name = "name", required = false) String name,
		@RequestParam(name = "tab", required = false) String tab,
		@RequestParam(name = "season", required = false) Integer season,
		@RequestParam(name = "level", required = false) String level,
		@RequestParam(name = "surface", required = false) String surface,
		@RequestParam(name = "result", required = false) String result,
		@RequestParam(name = "round", required = false) String round,
		@RequestParam(name = "opponent", required = false) String opponent,
		@RequestParam(name = "tournamentId", required = false) Integer tournamentId,
		@RequestParam(name = "tournamentEventId", required = false) Integer tournamentEventId,
		@RequestParam(name = "outcome", required = false) String outcome,
		@RequestParam(name = "infamous", required = false) Boolean infamous
	) {
		if (playerId == null && name == null)
			return new ModelAndView("playerProfile");

		Optional<Player> optionalPlayer = playerId != null ? playerService.getPlayer(playerId) : playerService.getPlayer(name);

		ModelMap modelMap = new ModelMap();
		if (optionalPlayer.isPresent())
			modelMap.addAttribute("player", optionalPlayer.get());
		else
			modelMap.addAttribute("playerRef", playerId != null ? playerId : name);
		modelMap.addAttribute("tab", tab);
		modelMap.addAttribute("season", season);
		modelMap.addAttribute("level", level);
		modelMap.addAttribute("surface", surface);
		modelMap.addAttribute("result", result);
		modelMap.addAttribute("round", round);
		modelMap.addAttribute("opponent", opponent);
		modelMap.addAttribute("tournamentId", tournamentId);
		modelMap.addAttribute("tournamentEventId", tournamentEventId);
		modelMap.addAttribute("outcome", outcome);
		modelMap.addAttribute("infamous", infamous);
		return new ModelAndView("playerProfile", modelMap);
	}

	@GetMapping("/playerProfileTab")
	public ModelAndView playerProfileTab(
		@RequestParam(name = "playerId") int playerId
	) {
		Player player = playerService.getPlayer(playerId).get();
		PlayerPerformance performance = performanceService.getPlayerPerformance(playerId);
		FavoriteSurface favoriteSurface = new FavoriteSurface(performance);
		WonDrawLost playerH2H = rivalriesService.getPlayerH2H(playerId).orElse(null);

		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("player", player);
		modelMap.addAttribute("favoriteSurface", favoriteSurface);
		modelMap.addAttribute("performance", performance);
		modelMap.addAttribute("playerH2H", playerH2H);
		return new ModelAndView("playerProfileTab", modelMap);
	}

	@GetMapping("/playerSeason")
	public ModelAndView playerSeason(
		@RequestParam(name = "playerId") int playerId,
		@RequestParam(name = "season", required = false) Integer season
	) {
		List<Integer> seasons = playerService.getPlayerSeasons(playerId);
		if (season == null)
			season = !seasons.isEmpty() ? seasons.get(0) : Integer.valueOf(LocalDate.now().getYear());
		Map<EventResult, List<PlayerTournamentEvent>> seasonHighlights = tournamentService.getPlayerSeasonHighlights(playerId, season, 4);
		PlayerPerformanceEx seasonPerf = performanceService.getPlayerSeasonPerformanceEx(playerId, season);
		PlayerSeasonGOATPoints seasonGOATPoints = goatPointsService.getPlayerSeasonGOATPoints(playerId, season);

		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("playerId", playerId);
		modelMap.addAttribute("season", season);
		modelMap.addAttribute("seasons", seasons);
		modelMap.addAttribute("seasonHighlights", seasonHighlights);
		modelMap.addAttribute("seasonPerf", seasonPerf);
		modelMap.addAttribute("seasonGOATPoints", seasonGOATPoints);
		return new ModelAndView("playerSeason", modelMap);
	}

	@GetMapping("/playerTournaments")
	public ModelAndView playerTournaments(
		@RequestParam(name = "playerId") int playerId,
		@RequestParam(name = "season", required = false) Integer season,
		@RequestParam(name = "level", required = false) String level,
		@RequestParam(name = "surface", required = false) String surface,
		@RequestParam(name = "tournamentId", required = false) Integer tournamentId,
		@RequestParam(name = "result", required = false) String result
	) {
		String name = playerService.getPlayerName(playerId);
		List<Integer> seasons = playerService.getPlayerSeasons(playerId);
		List<TournamentItem> tournaments = tournamentService.getPlayerTournaments(playerId);

		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("playerId", playerId);
		modelMap.addAttribute("playerName", name);
		modelMap.addAttribute("seasons", seasons);
		modelMap.addAttribute("levels", TournamentLevel.MAIN_TOURNAMENT_LEVELS);
		modelMap.addAttribute("levelGroups", TournamentLevelGroup.NON_TEAM_LEVEL_GROUPS);
		modelMap.addAttribute("surfaces", Surface.values());
		modelMap.addAttribute("surfaceGroups", SurfaceGroup.values());
		modelMap.addAttribute("tournaments", tournaments);
		modelMap.addAttribute("results", EventResult.values());
		modelMap.addAttribute("season", season);
		modelMap.addAttribute("level", level);
		modelMap.addAttribute("surface", surface);
		modelMap.addAttribute("tournamentId", tournamentId);
		modelMap.addAttribute("result", result);
		modelMap.addAttribute("categoryClasses", StatsCategory.getCategoryClasses());
		return new ModelAndView("playerTournaments", modelMap);
	}

	@GetMapping("/playerMatches")
	public ModelAndView playerMatches(
		@RequestParam(name = "playerId") int playerId,
		@RequestParam(name = "season", required = false) Integer season,
		@RequestParam(name = "level", required = false) String level,
		@RequestParam(name = "surface", required = false) String surface,
		@RequestParam(name = "round", required = false) String round,
		@RequestParam(name = "opponent", required = false) String opponent,
		@RequestParam(name = "tournamentId", required = false) Integer tournamentId,
		@RequestParam(name = "tournamentEventId", required = false) Integer tournamentEventId,
		@RequestParam(name = "outcome", required = false) String outcome
	) {
		String name = playerService.getPlayerName(playerId);
		List<Integer> seasons = playerService.getPlayerSeasons(playerId);
		List<TournamentItem> tournaments = tournamentService.getPlayerTournaments(playerId);
		List<TournamentEventItem> tournamentEvents = tournamentService.getPlayerTournamentEvents(playerId);
		List<CountryCode> countries = getCountries(playerId);

		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("playerId", playerId);
		modelMap.addAttribute("playerName", name);
		modelMap.addAttribute("seasons", seasons);
		modelMap.addAttribute("levels", TournamentLevel.ALL_TOURNAMENT_LEVELS);
		modelMap.addAttribute("levelGroups", TournamentLevelGroup.ALL_LEVEL_GROUPS);
		modelMap.addAttribute("surfaces", Surface.values());
		modelMap.addAttribute("surfaceGroups", SurfaceGroup.values());
		modelMap.addAttribute("rounds", Round.values());
		modelMap.addAttribute("opponents", Opponent.values());
		modelMap.addAttribute("tournaments", tournaments);
		modelMap.addAttribute("tournamentEvents", tournamentEvents);
		modelMap.addAttribute("countries", countries);
		modelMap.addAttribute("season", season);
		modelMap.addAttribute("level", level);
		modelMap.addAttribute("surface", surface);
		modelMap.addAttribute("round", round);
		if (!isNullOrEmpty(opponent)) {
			modelMap.addAttribute("opponent", opponent);
			if (opponent.startsWith(OpponentFilter.OPPONENT_PREFIX)) {
				int opponentId = Integer.parseInt(opponent.substring(OpponentFilter.OPPONENT_PREFIX.length()));
				modelMap.addAttribute("opponentName", playerService.getPlayerName(opponentId));
			}
		}
		modelMap.addAttribute("tournamentId", tournamentId);
		modelMap.addAttribute("tournamentEventId", tournamentEventId);
		modelMap.addAttribute("outcome", outcome);
		modelMap.addAttribute("categoryClasses", StatsCategory.getCategoryClasses());
		return new ModelAndView("playerMatches", modelMap);
	}

	private List<CountryCode> getCountries(int playerId) {
		return playerService.getPlayerOpponentCountryIds(playerId).stream().map(Country::code).filter(code -> code != null && code.getAlpha3() != null).distinct().sorted(comparing(CountryCode::getName)).collect(toList());
	}

	@GetMapping("/playerTimeline")
	public ModelAndView playerTimeline(
		@RequestParam(name = "playerId") int playerId
	) {
		PlayerTimeline timeline = timelineService.getPlayerTimeline(playerId);

		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("playerId", playerId);
		modelMap.addAttribute("timeline", timeline);
		return new ModelAndView("playerTimeline", modelMap);
	}

	@GetMapping("/playerRivalries")
	public ModelAndView playerRivalries(
		@RequestParam(name = "playerId") int playerId
	) {
		String name = playerService.getPlayerName(playerId);
		List<Integer> seasons = playerService.getPlayerSeasons(playerId);

		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("playerId", playerId);
		modelMap.addAttribute("playerName", name);
		modelMap.addAttribute("seasons", seasons);
		modelMap.addAttribute("levels", TournamentLevel.TOURNAMENT_LEVELS);
		modelMap.addAttribute("levelGroups", TournamentLevelGroup.ALL_LEVEL_GROUPS);
		modelMap.addAttribute("surfaces", Surface.values());
		modelMap.addAttribute("surfaceGroups", SurfaceGroup.values());
		modelMap.addAttribute("rounds", Round.values());
		return new ModelAndView("playerRivalries", modelMap);
	}

	@GetMapping("/playerRankings")
	public ModelAndView playerRankings(
		@RequestParam(name = "playerId") int playerId
	) {
		List<Integer> seasons = playerService.getPlayerSeasons(playerId);
		RankingHighlights rankingHighlights = rankingsService.getRankingHighlights(playerId);

		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("playerId", new int[] {playerId});
		modelMap.addAttribute("highlights", rankingHighlights);
		modelMap.addAttribute("seasons", seasons);
		modelMap.addAttribute("rankTypes", RankType.values());
		return new ModelAndView("playerRankings", modelMap);
	}

	@GetMapping("/playerPerformance")
	public ModelAndView playerPerformance(
		@RequestParam(name = "playerId") int playerId,
		@RequestParam(name = "season", required = false) Integer season
	) {
		List<Integer> seasons = playerService.getPlayerSeasons(playerId);
		PlayerPerformanceEx perf = season == null
			? performanceService.getPlayerPerformanceEx(playerId)
			: performanceService.getPlayerSeasonPerformanceEx(playerId, season);

		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("playerId", playerId);
		modelMap.addAttribute("seasons", seasons);
		modelMap.addAttribute("season", season);
		modelMap.addAttribute("perf", perf);
		return new ModelAndView("playerPerformance", modelMap);
	}

	@GetMapping("/playerPerformanceChart")
	public ModelAndView playerPerformanceChart(
		@RequestParam(name = "playerId") int playerId
	) {
		List<Integer> seasons = playerService.getPlayerSeasons(playerId);

		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("playerId", new int[] {playerId});
		modelMap.addAttribute("seasons", seasons);
		modelMap.addAttribute("categoryClasses", PerformanceCategory.getCategoryClasses());
		return new ModelAndView("playerPerformanceChart", modelMap);
	}

	@GetMapping("/playerStatsTab")
	public ModelAndView playerStatsTab(
		@RequestParam(name = "playerId") int playerId,
		@RequestParam(name = "season", required = false) Integer season,
		@RequestParam(name = "level", required = false) String level,
		@RequestParam(name = "surface", required = false) String surface,
		@RequestParam(name = "compare", defaultValue = "false") boolean compare,
		@RequestParam(name = "compareSeason", required = false) Integer compareSeason,
		@RequestParam(name = "compareLevel", required = false) String compareLevel,
		@RequestParam(name = "compareSurface", required = false) String compareSurface
	) {
		List<Integer> seasons = playerService.getPlayerSeasons(playerId);
		PlayerStats stats = statisticsService.getPlayerStats(playerId, MatchFilter.forStats(season, level, surface));

		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("playerId", playerId);
		modelMap.addAttribute("seasons", seasons);
		modelMap.addAttribute("levels", TournamentLevel.ALL_TOURNAMENT_LEVELS);
		modelMap.addAttribute("levelGroups", TournamentLevelGroup.ALL_LEVEL_GROUPS);
		modelMap.addAttribute("surfaces", Surface.values());
		modelMap.addAttribute("surfaceGroups", SurfaceGroup.values());
		modelMap.addAttribute("season", season);
		modelMap.addAttribute("level", level);
		modelMap.addAttribute("surface", surface);
		modelMap.addAttribute("stats", stats);
		modelMap.addAttribute("statsFormatUtil", new StatsFormatUtil());
		modelMap.addAttribute("compare", compare);
		if (compare) {
			MatchFilter compareFilter = MatchFilter.forStats(compareSeason, compareLevel, compareSurface);
			PlayerStats compareStats = statisticsService.getPlayerStats(playerId, compareFilter);
			if (!compareStats.isEmpty())
				modelMap.addAttribute("compareStats", compareStats);
			modelMap.addAttribute("compareSeason", compareSeason);
			modelMap.addAttribute("compareLevel", compareLevel);
			modelMap.addAttribute("compareSurface", compareSurface);
			modelMap.addAttribute("relativeTo", relativeTo(compareSeason, compareLevel, compareSurface));
		}
		return new ModelAndView("playerStatsTab", modelMap);
	}

	@GetMapping("/playerStatsChart")
	public ModelAndView playerStatsChart(
		@RequestParam(name = "playerId") int playerId
	) {
		List<Integer> seasons = playerService.getPlayerSeasons(playerId);

		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("playerId", new int[] {playerId});
		modelMap.addAttribute("seasons", seasons);
		modelMap.addAttribute("surfaces", Surface.values());
		modelMap.addAttribute("surfaceGroups", SurfaceGroup.values());
		modelMap.addAttribute("categoryTypes", StatsCategory.getCategoryTypes());
		modelMap.addAttribute("categoryClasses", StatsCategory.getCategoryClasses());
		return new ModelAndView("playerStatsChart", modelMap);
	}

	@GetMapping("/playerGOATPoints")
	public ModelAndView playerGOATPoints(
		@RequestParam(name = "playerId") int playerId,
		@RequestParam(name = "season", required = false) Integer season
	) {
		Map<String, List<String>> levelResults = goatPointsService.getLevelResults();
		PlayerGOATPoints goatPoints = goatPointsService.getPlayerGOATPoints(playerId);

		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("playerId", playerId);
		modelMap.addAttribute("levelResults", levelResults);
		modelMap.addAttribute("levelResultCount", levelResults.values().stream().mapToInt(List::size).sum());
		modelMap.addAttribute("goatPoints", goatPoints);
		modelMap.addAttribute("highlightSeason", season);
		return new ModelAndView("playerGOATPoints", modelMap);
	}

	@GetMapping("/playerRecords")
	public ModelAndView playerRecords(
		@RequestParam(name = "playerId") int playerId,
		@RequestParam(name = "infamous", required = false) Boolean infamous
	) {
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("playerId", playerId);
		modelMap.addAttribute("infamous", infamous);
		modelMap.addAttribute("recordCategoryClasses", Records.getRecordCategoryClasses());
		modelMap.addAttribute("infamousRecordCategoryClasses", Records.getInfamousRecordCategoryClasses());
		return new ModelAndView("playerRecords", modelMap);
	}
}
