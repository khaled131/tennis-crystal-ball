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
public class InProgressEventsResource {

	@Autowired private TournamentForecastService forecastService;

	private static Map<String, String> ORDER_MAP = ImmutableMap.<String, String>builder()
		.put("date", "date")
		.put("name", "name")
		.put("surface", "surface")
		.put("draw", "draw_type, draw_size")
		.put("playerCount", "player_count NULLS LAST")
		.put("participationPoints", "participation_points NULLS LAST")
		.put("participationPct", "participation_points::REAL / max_participation_points NULLS LAST")
	.build();
	public static final OrderBy[] DEFAULT_ORDER = new OrderBy[] {desc("date"), asc("level"), asc("name")};

	@GetMapping("/inProgressEventsTable")
	public BootgridTable<InProgressEvent> inProgressEventsTable(
		@RequestParam Map<String, String> requestParams
	) {
		String orderBy = BootgridUtil.getOrderBy(requestParams, ORDER_MAP, DEFAULT_ORDER);
		return forecastService.getInProgressEventsTable(orderBy);
	}
}
