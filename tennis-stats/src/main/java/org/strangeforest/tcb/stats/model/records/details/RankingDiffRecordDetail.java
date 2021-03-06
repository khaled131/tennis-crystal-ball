package org.strangeforest.tcb.stats.model.records.details;

import java.util.*;

import com.fasterxml.jackson.annotation.*;

public class RankingDiffRecordDetail extends BaseRankingDiffRecordDetail<Integer> {

	public RankingDiffRecordDetail(
		@JsonProperty("value") int value,
		@JsonProperty("player_id2") int playerId2,
		@JsonProperty("name2") String name2,
		@JsonProperty("country_id2") String countryId2,
		@JsonProperty("active2") Boolean active2,
		@JsonProperty("value1") int value1,
		@JsonProperty("value2") int value2,
		@JsonProperty("date") Date date
	) {
		super(value, playerId2, name2, countryId2, active2, value1, value2, date);
	}
}
