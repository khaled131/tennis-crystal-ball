package org.strangeforest.tcb.dataload

import groovy.sql.*

import static org.strangeforest.tcb.dataload.LoadParams.*

def sqlPool = new SqlPool()

def eloRatings = new EloRatings(sqlPool)
eloRatings.compute(true, System.getProperty(FULL_LOAD_PROPERTY, String.valueOf(FULL_LOAD_DEFAULT)).toBoolean())

if (System.getProperty(VERBOSE_PROPERTY, String.valueOf(VERBOSE_DEFAULT)).toBoolean()) {
	sqlPool.withSql { sql ->
		showCurrent(eloRatings, sql)
		showPeak(eloRatings, sql)
	}
}


def showCurrent(EloRatings eloRatings, Sql sql) {
	printf '%n%1$4s %2$-30s %3$4s%n', 'Rank', 'Player', 'Elo'
	def i = 0
	eloRatings.current(100).each {
		printf '%1$4s %2$-30s %3$4s%n', ++i, getPlayerName(sql, it.playerId), it
	}
}

def showPeak(EloRatings eloRatings, Sql sql) {
	printf '%n%1$4s %2$-30s %3$4s %4$10s %5$4s/%6$4s%n', 'Rank', 'Player', 'Elo', 'Date', 'PkMt', 'AlMt'
	def i = 0
	eloRatings.peak(100).each {
		def bestRating = it.bestRating
		printf '%1$4s %2$-30s %3$4s %4$10s %5$4s/%6$4s%n', ++i, getPlayerName(sql, it.playerId), bestRating, bestRating.lastDate.format('dd-MM-yyyy'), bestRating.matches, it.matches
	}
}

def getPlayerName(Sql sql, int playerId) {
	sql.firstRow('SELECT name FROM player_v WHERE player_id = ?', [playerId]).name
}
