package org.strangeforest.tcb.stats.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;
import org.strangeforest.tcb.stats.model.*;
import org.strangeforest.tcb.stats.model.prediction.*;
import org.strangeforest.tcb.stats.model.records.*;
import org.strangeforest.tcb.stats.service.*;

import static java.util.Collections.*;

@Controller
public class TournamentController extends PageController {

	@Autowired private TournamentService tournamentService;
	@Autowired private MatchesService matchesService;
	@Autowired private TournamentForecastService forecastService;

	private static final int MAX_RECORD_PLAYERS = 10;

	@GetMapping("/tournament")
	public ModelAndView tournament(
		@RequestParam(name = "tournamentId") int tournamentId
	) {
		Tournament tournament = tournamentService.getTournament(tournamentId);
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("tournament", tournament);
		modelMap.addAttribute("levels", TournamentLevel.asMap());
		modelMap.addAttribute("surfaces", Surface.asMap());
		return new ModelAndView("tournament", modelMap);
	}

	@GetMapping("/tournamentEvents")
	public ModelAndView tournamentEvents(
		@RequestParam(name = "season", required = false) Integer season,
		@RequestParam(name = "level", required = false) String level,
		@RequestParam(name = "surface", required = false) String surface
	) {
		List<Integer> seasons = dataService.getSeasons();
		List<TournamentItem> tournaments = tournamentService.getTournaments();

		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("season", season);
		modelMap.addAttribute("level", level);
		modelMap.addAttribute("surface", surface);
		modelMap.addAttribute("seasons", seasons);
		modelMap.addAttribute("levels", TournamentLevel.MAIN_TOURNAMENT_LEVELS);
		modelMap.addAttribute("levelGroups", TournamentLevelGroup.NON_TEAM_LEVEL_GROUPS);
		modelMap.addAttribute("surfaces", Surface.values());
		modelMap.addAttribute("surfaceGroups", SurfaceGroup.values());
		modelMap.addAttribute("tournaments", tournaments);
		return new ModelAndView("tournamentEvents", modelMap);
	}

	@GetMapping("/tournamentEvent")
	public ModelAndView tournamentEvent(
		@RequestParam(name = "tournamentEventId") int tournamentEventId
	) {
		TournamentEvent tournamentEvent = tournamentService.getTournamentEvent(tournamentEventId);
		TournamentEventResults results = matchesService.getTournamentEventResults(tournamentEventId);

		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("tournamentEvent", tournamentEvent);
		modelMap.addAttribute("results", results);
		modelMap.addAttribute("levels", TournamentLevel.asMap());
		modelMap.addAttribute("surfaces", Surface.asMap());
		return new ModelAndView("tournamentEvent", modelMap);
	}

	@GetMapping("/tournamentEventStats")
	public ModelAndView tournamentEventStats(
		@RequestParam(name = "tournamentEventId") int tournamentEventId
	) {
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("tournamentEventId", tournamentEventId);
		modelMap.addAttribute("categoryClasses", StatsCategory.getCategoryClasses());
		return new ModelAndView("tournamentEventStats", modelMap);
	}

	@GetMapping("/tournamentEventMap")
	public ModelAndView tournamentEventMap(
		@RequestParam(name = "tournamentEventId") int tournamentEventId
	) {
		String mapProperties = tournamentService.getTournamentEventMapProperties(tournamentEventId);

		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("mapProperties", mapProperties);
		return new ModelAndView("tournamentEventMap", modelMap);
	}

	@GetMapping("/tournamentRecords")
	public ModelAndView tournamentRecords(
		@RequestParam(name = "tournamentId") int tournamentId
	) {
		List<RecordDetailRow> recordTitles = tournamentService.getTournamentRecord(tournamentId, "W", MAX_RECORD_PLAYERS);
		List<RecordDetailRow> recordFinals = tournamentService.getTournamentRecord(tournamentId, "F", MAX_RECORD_PLAYERS);
		List<RecordDetailRow> recordSemiFinals = tournamentService.getTournamentRecord(tournamentId, "SF", MAX_RECORD_PLAYERS);
		List<RecordDetailRow> recordAppearances = tournamentService.getTournamentRecord(tournamentId, "RR", MAX_RECORD_PLAYERS);

		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("recordTitles", recordTitles);
		modelMap.addAttribute("recordFinals", recordFinals);
		modelMap.addAttribute("recordSemiFinals", recordSemiFinals);
		modelMap.addAttribute("recordAppearances", recordAppearances);
		return new ModelAndView("tournamentRecords", modelMap);
	}

	@GetMapping("/tournamentPerformance")
	public ModelAndView tournamentPerformance(
		@RequestParam(name = "tournamentId") int tournamentId
	) {
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("tournamentId", tournamentId);
		modelMap.addAttribute("categoryClasses", PerformanceCategory.getPressureSituationsCategoryClasses());
		return new ModelAndView("tournamentPerformance", modelMap);
	}

	@GetMapping("/tournamentStats")
	public ModelAndView tournamentStats(
		@RequestParam(name = "tournamentId") int tournamentId
	) {
		List<Integer> seasons = tournamentService.getTournament(tournamentId).getSeasons();
		reverse(seasons);

		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("tournamentId", tournamentId);
		modelMap.addAttribute("categoryClasses", StatsCategory.getCategoryClasses());
		modelMap.addAttribute("seasons", seasons);
		return new ModelAndView("tournamentStats", modelMap);
	}

	@GetMapping("/inProgressEventsForecasts")
	public String inProgressEventsForecasts() {
		return "inProgressEventsForecasts";
	}

	@GetMapping("/inProgressEventForecast")
	public ModelAndView inProgressEventForecast(
		@RequestParam(name = "inProgressEventId") int inProgressEventId,
		@RequestParam(name = "tab", required = false) String tab,
      @RequestParam(name = "priceFormat", required = false) PriceFormat priceFormat
	) {
		InProgressEventForecast forecast = forecastService.getInProgressEventForecast(inProgressEventId);

		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("inProgressEventId", inProgressEventId);
		modelMap.addAttribute("tab", tab);
		modelMap.addAttribute("forecast", forecast);
		modelMap.addAttribute("levels", TournamentLevel.asMap());
		modelMap.addAttribute("surfaces", Surface.asMap());
		modelMap.addAttribute("priceFormats", PriceFormat.values());
		modelMap.addAttribute("priceFormat", priceFormat);
		return new ModelAndView("inProgressEventForecast", modelMap);
	}

	@GetMapping("/inProgressEventResults")
	public ModelAndView inProgressEventResults(
		@RequestParam(name = "inProgressEventId") int inProgressEventId
	) {
		TournamentEventResults results = forecastService.getCompletedMatches(inProgressEventId);
		return new ModelAndView("inProgressEventResults", "results", results);
	}

	@GetMapping("/inProgressEventProbableMatches")
	public ModelAndView inProgressEventProbableMatches(
		@RequestParam(name = "inProgressEventId") int inProgressEventId,
		@RequestParam(name = "pinnedPlayerId", required = false) Integer pinnedPlayerId
	) {
		ProbableMatches probableMatches = forecastService.getInProgressEventProbableMatches(inProgressEventId, pinnedPlayerId);

		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("results", probableMatches.getResults());
		modelMap.addAttribute("players", probableMatches.getPlayers());
		modelMap.addAttribute("inProgressEventId", inProgressEventId);
		modelMap.addAttribute("pinnedPlayerId", pinnedPlayerId);
		return new ModelAndView("inProgressEventProbableMatches", modelMap);
	}
}
