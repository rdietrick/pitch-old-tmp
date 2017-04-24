package com.pitchplayer.stats.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.pitchplayer.stats.om.PlayerStat;

public class PlayerStatRowMapper implements RowMapper {

	private static final String COL_USERNAME = "username";
	private static final String COL_USER_ID = "user_id";
	private static final String COL_GAMES = "GAMES";
	private static final String COL_LOSSES = "LOSSES";
	private static final String COL_WINS = "WINS";
	private static final String COL_WIN_PCT = "WIN_PCT";
	private static final String COL_QUITS = "QUITS";
	private static final String COL_GAME_TYPE = "game_type";

	private byte gameType = -1;
	
	PlayerStat stat = new PlayerStat();
	
	PlayerStatRowMapper() {
		
	}
	
	PlayerStatRowMapper(byte gameType) {
		this.gameType = gameType;
	}
	
	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		PlayerStat stat = new PlayerStat();
		stat.setUserId(rs.getInt(COL_USER_ID));
		stat.setUsername(rs.getString(COL_USERNAME));
		stat.setGames(rs.getInt(COL_GAMES));
		stat.setLosses(rs.getInt(COL_LOSSES));
		stat.setWins(rs.getInt(COL_WINS));
		stat.setQuits(rs.getInt(COL_QUITS));
		stat.setWinPct(rs.getFloat(COL_WIN_PCT));
		if (gameType == -1) {
			stat.setGameType(rs.getByte(COL_GAME_TYPE));
		}
		else {
			stat.setGameType(gameType);
		}
		return stat;
	}

}
