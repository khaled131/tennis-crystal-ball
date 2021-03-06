package org.strangeforest.tcb.stats.service;

import java.util.Objects;

import org.springframework.jdbc.core.namedparam.*;

import com.google.common.base.*;

import static com.google.common.base.Strings.*;
import static java.util.Arrays.*;
import static org.strangeforest.tcb.stats.service.FilterUtil.*;

public class StatsPlayerListFilter extends PlayerListFilter {

	private final Integer season;
	private final String surface;
	private final Integer tournamentId;
	private final Integer tournamentEventId;

	private static final String SEASON_CRITERION = " AND season = :season";
	private static final String SURFACE_CRITERION = " AND surface = :surface::surface";
	private static final String SURFACES_CRITERION = " AND surface::TEXT IN (:surfaces)";
	private static final String TOURNAMENT_CRITERION = " AND tournament_id = :tournamentId";
	private static final String TOURNAMENT_EVENT_CRITERION = " AND tournament_event_id = :tournamentEventId";

	public StatsPlayerListFilter(Integer season, Integer tournamentId) {
		this(null, null, season, null, tournamentId, null);
	}

	public StatsPlayerListFilter(Boolean active, String searchPhrase, Integer season, Integer tournamentId) {
		this(active, searchPhrase, season, null, tournamentId, null);
	}

	public StatsPlayerListFilter(Integer season, String surface, Integer tournamentId, Integer tournamentEventId) {
		this(null, null, season, surface, tournamentId, tournamentEventId);
	}

	public StatsPlayerListFilter(Boolean active, String searchPhrase, Integer season, String surface, Integer tournamentId, Integer tournamentEventId) {
		super(active, searchPhrase);
		this.season = season;
		this.surface = surface;
		this.tournamentId = tournamentId;
		this.tournamentEventId = tournamentEventId;
	}

	public boolean hasSeason() {
		return season != null;
	}

	public boolean hasSurface() {
		return !isNullOrEmpty(surface);
	}

	public boolean hasSurfaceGroup() {
		return hasSurface() && surface.length() > 1;
	}

	public boolean hasTournament() {
		return tournamentId != null;
	}

	public boolean hasTournamentEvent() {
		return tournamentEventId != null;
	}

	public boolean hasTournamentOrTournamentEvent() {
		return hasTournament() || hasTournamentEvent();
	}

	public Integer getSeason() {
		return season;
	}

	public int getTournamentId() {
		return tournamentId;
	}

	public String getBaseCriteria() {
		StringBuilder criteria = new StringBuilder();
		appendBaseCriteria(criteria);
		return criteria.toString();
	}

	public String getSearchCriteria() {
		StringBuilder criteria = new StringBuilder();
		super.appendCriteria(criteria);
		return criteria.toString();
	}

	@Override protected void appendCriteria(StringBuilder criteria) {
		appendBaseCriteria(criteria);
		super.appendCriteria(criteria);
	}

	private void appendBaseCriteria(StringBuilder criteria) {
		if (season != null)
			criteria.append(SEASON_CRITERION);
		if (!isNullOrEmpty(surface))
			criteria.append(surface.length() == 1 ? SURFACE_CRITERION : SURFACES_CRITERION);
		if (tournamentId != null)
			criteria.append(TOURNAMENT_CRITERION);
		if (tournamentEventId != null)
			criteria.append(TOURNAMENT_EVENT_CRITERION);
	}

	@Override protected void addParams(MapSqlParameterSource params) {
		super.addParams(params);
		if (season != null)
			params.addValue("season", season);
		if (!isNullOrEmpty(surface)) {
			if (surface.length() == 1)
				params.addValue("surface", surface);
			else
				params.addValue("surfaces", asList(surface.split("")));
		}
		if (tournamentId != null)
			params.addValue("tournamentId", tournamentId);
		if (tournamentEventId != null)
			params.addValue("tournamentEventId", tournamentEventId);
	}


	// Object methods

	@Override public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof StatsPlayerListFilter)) return false;
		if (!super.equals(o)) return false;
		StatsPlayerListFilter filter = (StatsPlayerListFilter)o;
		return Objects.equals(season, filter.season) &&	stringsEqual(surface, filter.surface)
		    && Objects.equals(tournamentId, filter.tournamentId) && Objects.equals(tournamentEventId, filter.tournamentEventId);
	}

	@Override public int hashCode() {
		return Objects.hash(super.hashCode(), season, emptyToNull(surface), tournamentId, tournamentEventId);
	}

	@Override protected MoreObjects.ToStringHelper toStringHelper() {
		return super.toStringHelper()
			.add("season", season)
			.add("surface", surface)
			.add("tournamentId", tournamentId)
			.add("tournamentEventId", tournamentEventId);
	}
}
