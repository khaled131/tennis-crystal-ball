package org.strangeforest.tcb.stats.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;
import org.strangeforest.tcb.stats.model.*;
import org.strangeforest.tcb.stats.model.records.*;
import org.strangeforest.tcb.stats.service.*;

@Controller
public class SeasonsController extends PageController {

	@Autowired private SeasonsService seasonsService;

	private static final int MAX_RECORD_PLAYERS = 10;

	@GetMapping("/seasons")
	public String seasons() {
		return "seasons";
	}

	@GetMapping("/season")
	public ModelAndView season(
		@RequestParam(name = "season") int season,
		@RequestParam(name = "tab", required = false) String tab,
		@RequestParam(name = "level", required = false) String level,
		@RequestParam(name = "surface", required = false) String surface
	) {
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("season", season);
		modelMap.addAttribute("tab", tab);
		modelMap.addAttribute("level", level);
		modelMap.addAttribute("surface", surface);
		return new ModelAndView("season", modelMap);
	}

	@GetMapping("/seasonRecords")
	public ModelAndView seasonRecords(
		@RequestParam(name = "season") int season
	) {
		List<RecordDetailRow> seasonTitles = seasonsService.getSeasonResults(season, "W", MAX_RECORD_PLAYERS);
		List<RecordDetailRow> seasonFinals = seasonsService.getSeasonResults(season, "F", MAX_RECORD_PLAYERS);
		List<RecordDetailRow> seasonSemiFinals = seasonsService.getSeasonResults(season, "SF", MAX_RECORD_PLAYERS);
		List<RecordDetailRow> seasonAppearances = seasonsService.getSeasonResults(season, "RR", MAX_RECORD_PLAYERS);

		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("seasonTitles", seasonTitles);
		modelMap.addAttribute("seasonFinals", seasonFinals);
		modelMap.addAttribute("seasonSemiFinals", seasonSemiFinals);
		modelMap.addAttribute("seasonAppearances", seasonAppearances);
		return new ModelAndView("seasonRecords", modelMap);
	}

	@GetMapping("/seasonEvents")
	public ModelAndView seasonEvents(
		@RequestParam(name = "season") int season,
		@RequestParam(name = "level", required = false) String level,
		@RequestParam(name = "surface", required = false) String surface
	) {
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("season", season);
		modelMap.addAttribute("level", level);
		modelMap.addAttribute("surface", surface);
		modelMap.addAttribute("levels", TournamentLevel.MAIN_TOURNAMENT_LEVELS);
		modelMap.addAttribute("levelGroups", TournamentLevelGroup.NON_TEAM_LEVEL_GROUPS);
		modelMap.addAttribute("surfaces", Surface.values());
		modelMap.addAttribute("surfaceGroups", SurfaceGroup.values());
		return new ModelAndView("seasonEvents", modelMap);
	}

	@GetMapping("/seasonRankings")
	public ModelAndView seasonRankings(
		@RequestParam(name = "season") int season
	) {
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("season", season);
		modelMap.addAttribute("surfaces", Surface.values());
		return new ModelAndView("seasonRankings", modelMap);
	}

	@GetMapping("/seasonPerformance")
	public ModelAndView seasonPerformance(
		@RequestParam(name = "season") int season
	) {
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("season", season);
		modelMap.addAttribute("categoryClasses", PerformanceCategory.getCategoryClasses());
		return new ModelAndView("seasonPerformance", modelMap);
	}

	@GetMapping("/seasonStats")
	public ModelAndView seasonStats(
		@RequestParam(name = "season") int season
	) {
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("season", season);
		modelMap.addAttribute("categoryClasses", StatsCategory.getCategoryClasses());
		modelMap.addAttribute("surfaces", Surface.values());
		modelMap.addAttribute("surfaceGroups", SurfaceGroup.values());
		return new ModelAndView("seasonStats", modelMap);
	}
	
	@GetMapping("/seasonGOATPoints")
	public ModelAndView seasonGOATPoints(
		@RequestParam(name = "season") int season
	) {
		List<RecordDetailRow> totalPoints = seasonsService.getSeasonGOATPoints(season, "", MAX_RECORD_PLAYERS);
		List<RecordDetailRow> tournamentPoints = seasonsService.getSeasonGOATPoints(season, "tournament_", MAX_RECORD_PLAYERS);
		List<RecordDetailRow> rankingPoints = seasonsService.getSeasonGOATPoints(season, "ranking_", MAX_RECORD_PLAYERS);
		List<RecordDetailRow> achievementsPoints = seasonsService.getSeasonGOATPoints(season, "achievements_", MAX_RECORD_PLAYERS);

		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("totalPoints", totalPoints);
		modelMap.addAttribute("tournamentPoints", tournamentPoints);
		modelMap.addAttribute("rankingPoints", rankingPoints);
		modelMap.addAttribute("achievementsPoints", achievementsPoints);
		return new ModelAndView("seasonGOATPoints", modelMap);
	}

	@GetMapping("/bestSeasons")
	public ModelAndView bestSeasons() {
		int minSeasonGOATPoints = seasonsService.getMinSeasonGOATPoints();
		return new ModelAndView("bestSeasons", "minSeasonGOATPoints", minSeasonGOATPoints);
	}
}
