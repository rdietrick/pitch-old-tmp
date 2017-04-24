package com.pitchplayer.stats.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.pitchplayer.stats.om.PlayerExtendedStats;

public class ExtendedStatsRowMapper implements RowMapper {

	private static final String COL_GAME_AVG = "game_avg";
	private static final String COL_JACK_AVG = "jack_avg";
	private static final String COL_JACK_LOSS = "jack_loss_avg";
	private static final String COL_JACK_STEAL = "jack_steal_avg";
	private static final String COL_UP_AVG = "ups_avg";
	private static final String COL_USER_ID = "user_id";
	private static final String COL_USERNAME = "username";

	public Object mapRow(ResultSet rs, int index) throws SQLException {
		PlayerExtendedStats stat = new PlayerExtendedStats();
		stat.setGameAvg(rs.getFloat(COL_GAME_AVG));
		stat.setJackAvg(rs.getFloat(COL_JACK_AVG));
		stat.setJackLossAvg(rs.getFloat(COL_JACK_LOSS));
		stat.setJackStealAvg(rs.getFloat(COL_JACK_STEAL));
		stat.setUpAvg(rs.getFloat(COL_UP_AVG));
		stat.setUserId(rs.getInt(COL_USER_ID));
		stat.setUsername(rs.getString(COL_USERNAME));
		return stat;
	}

}
