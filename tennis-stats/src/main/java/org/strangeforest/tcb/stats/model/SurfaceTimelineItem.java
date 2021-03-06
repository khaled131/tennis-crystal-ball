package org.strangeforest.tcb.stats.model;

import java.math.*;

import static java.lang.String.*;
import static java.math.RoundingMode.*;
import static org.strangeforest.tcb.stats.util.PercentageUtil.*;

public class SurfaceTimelineItem {

	private final int season;
	private final BigDecimal hardPct;
	private final BigDecimal clayPct;
	private final BigDecimal grassPct;
	private final BigDecimal carpetPct;

	private static final BigDecimal HUNDRED = new BigDecimal(100);

	public SurfaceTimelineItem(int season, int matchCount, int hardMatchCount, int clayMatchCount, int grassMatchCount, int carpetMatchCount) {
		this.season = season;
		hardPct = scaledPct(hardMatchCount, matchCount);
		clayPct = scaledPct(clayMatchCount, matchCount);
		grassPct = scaledPct(grassMatchCount, matchCount);
		BigDecimal aCarpetPct = scaledPct(carpetMatchCount, matchCount);
		BigDecimal hardClayGrassPct = hardPct.add(clayPct).add(grassPct);
		carpetPct = hardClayGrassPct.add(aCarpetPct).compareTo(HUNDRED) <= 0 ? aCarpetPct : HUNDRED.subtract(hardClayGrassPct);
	}

	public int getSeason() {
		return season;
	}

	public String getHardPct() {
		return formatPct(hardPct);
	}

	public String getClayPct() {
		return formatPct(clayPct);
	}

	public String getGrassPct() {
		return formatPct(grassPct);
	}

	public String getCarpetPct() {
		return formatPct(carpetPct);
	}

	private static BigDecimal scaledPct(int value, int from) {
		return new BigDecimal(pct(value, from)).setScale(1, HALF_EVEN);
	}

	private static String formatPct(BigDecimal pct) {
		return format("%1$.1f%%", pct);
	}
}
