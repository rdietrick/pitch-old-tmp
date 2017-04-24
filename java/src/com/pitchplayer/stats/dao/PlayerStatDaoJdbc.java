package com.pitchplayer.stats.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.pitchplayer.common.ResultsPage;
import com.pitchplayer.server.game.GameType;
import com.pitchplayer.stats.om.PlayerExtendedStats;
import com.pitchplayer.stats.om.PlayerStat;

public class PlayerStatDaoJdbc extends JdbcDaoSupport implements PlayerStatDao {

	private static final String SQL_GET_OVERALL_RANKINGS = 
		"select	a.user_id, b.username, "
		+ " sum(games) + sum(quits) as GAMES, "
		+ " sum(wins) as WINS, "
		+ " sum(losses) + sum(quits) as LOSSES, "
		+ " sum(quits) as QUITS, "
		+ " sum(wins) / (sum(games) + sum(quits)) as WIN_PCT "
		+ " from player_stat a, user b "
		+ " where b.status = 1 and games >= ? "
		+ " and a.user_id = b.user_id "
		+ " group by user_id "
		+ " order by WIN_PCT desc"; // 'offset ?' clause will be added if necessary

	private static final String SQL_GET_RANKINGS_FOR_GAME_TYPE = 
		"select	a.user_id, b.username, "
		+ " games + quits as GAMES, "
		+ " wins as WINS, "
		+ " losses + quits as LOSSES, "
		+ " quits as QUITS, "
		+ " wins/(games + quits) as WIN_PCT "
		+ " from player_stat a, user b  "
		+ " where b.status = 1 and game_type = ? "
		+ " and games >= ? "
		+ " and a.user_id = b.user_id "
		+ " order by WIN_PCT desc"; // 'offset ?' clause will be added if necessary

	private static final String SQL_GET_PLAYER_STATS_BY_USER_ID = 
		"select a.user_id, b.username, game_type, "
		+ " games + quits as GAMES, "
		+ " wins as WINS, "
		+ "losses + quits as LOSSES, "
		+ " quits as QUITS, "
		+ " wins/(games + quits) as WIN_PCT "
		+ " from player_stat a, user b  where a.user_id = ? "
		+ " and a.user_id = b.user_id "
		+ " order by game_type asc";

	private static final String SQL_GET_PLAYER_STATS_BY_USERNAME = 
		"select a.user_id, b.username, game_type, "
		+ " games + quits as GAMES, "
		+ " wins as WINS, "
		+ " losses + quits as LOSSES, "
		+ " quits as QUITS, "
		+ " wins/(games + quits) as WIN_PCT "
		+ " from player_stat a, user b  where a.username = ? "
		+ " and a.user_id = b.user_id "
		+ " order by game_type asc";

	private static final String SQL_GET_PLAYER_STATS_FOR_USER_IDS = 
		"select	a.user_id, b.username, "
		+ " sum(games) + sum(quits) as GAMES, "
		+ " sum(wins) as WINS, "
		+ " sum(losses) + sum(quits) as LOSSES, "
		+ " sum(wins) / (sum(games) + sum(quits)) as WIN_PCT, "
		+ " sum(quits) as QUITS "
		+ " from player_stat a, user b  "
		+ " where a.user_id in (%users%) " // need to replace %users% with comma-separated list of '?'s 
		+ " and a.user_id = b.user_id "
		+ " group by user_id "
		+ " order by WIN_PCT desc"; // should add 'offset ?' clause if needed

	private static final String SQL_GET_PLAYER_STATS_FOR_USERNAMES = 
		"select	a.user_id, b.username, "
		+ " sum(games) + sum(quits) as GAMES, "
		+ " sum(wins) as WINS, "
		+ " sum(losses) + sum(quits) as LOSSES, "
		+ " sum(wins) / (sum(games) + sum(quits)) as WIN_PCT, "
		+ " sum(quits) as QUITS "
		+ " from player_stat a, user b  "
		+ " where b.username in (%users%) " // need to replace %users% with comma-separated list of '?'s 
		+ " and a.user_id = b.user_id "
		+ " group by user_id "
		+ " order by WIN_PCT desc"; // should add 'offset ?' clause if needed

	private static final String SQL_COUNT_RANKINGS_FOR_GAME_TYPE = 
		"select	count(*) from player_stat where game_type = ? and games >= ?";

	private static final String SQL_COUNT_RANKINGS = 
		"select	count(distinct user_id) from player_stat where games >= ?";


	// TODO: finish player rank queries
	private static final String SQL_GET_PLAYER_RANK = "";
	
	private static final String SQL_GET_EXT_STATS_FOR_USERNAME = 
		"select * from player_extended_stats where username = ?";
	private static final String SQL_GET_EXT_STATS_FOR_USER_ID = 
		"select * from player_extended_stats where user_id = ?";
	private static final String SQL_GET_EXT_STATS_FOR_USER_IDS = 
		"select * from player_extended_stats where user_id in ";
	private static final String SQL_GET_EXT_STATS_FOR_USERNAMES = 
		"select * from player_extended_stats where username in ";

	private static final String SQL_CLEAR_SIM_STATS = 
		"update player_stat set games = 0, wins = 0, losses = 0, quits = 0, win_pct = 0 where game_type = ?";
	
	private static final String SQL_DELETE_SIM_GAME_PLAYERS = 
		"delete from game_player where game_id in (select game_id from game where game_type = ?)";
	
	private static final String SQL_DELETE_SIM_GAMES = 
		"delete from game where game_type = ?";
	
	protected int minUserGames = 20;

	protected Logger log = Logger.getLogger(this.getClass().getName());
	
	public Map<GameType, PlayerStat> getPlayerStats(final Integer userId) {
		PlayerStatRowMapper rowMapper = new PlayerStatRowMapper();
		List<PlayerStat> stats = this.getJdbcTemplate().query(SQL_GET_PLAYER_STATS_BY_USER_ID, 
				new PreparedStatementSetter() {

					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setInt(1, userId);
					}
			
		}, rowMapper);
		Map<GameType, PlayerStat> statMap = new HashMap<GameType, PlayerStat>(stats.size());
		for (PlayerStat stat : stats) {
			statMap.put(GameType.fromDbFlag(stat.getGameType()), stat);
		}
		return statMap;
	}

	public Map<GameType, PlayerStat> getPlayerStats(final String username) {
		PlayerStatRowMapper rowMapper = new PlayerStatRowMapper();
		List<PlayerStat> stats = this.getJdbcTemplate().query(SQL_GET_PLAYER_STATS_BY_USERNAME, 
				new PreparedStatementSetter() {

					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, username);
					}
			
		}, rowMapper);
		Map<GameType, PlayerStat> statMap = new HashMap<GameType, PlayerStat>(stats.size());
		for (PlayerStat stat : stats) {
			statMap.put(GameType.fromDbFlag(stat.getGameType()), stat);
		}
		return statMap;
	}
	
	public ResultsPage getPlayerStats(final GameType gameType, final int offset, final int limit) {
		int size = getJdbcTemplate().queryForInt(SQL_COUNT_RANKINGS_FOR_GAME_TYPE, new Object[]{gameType.getDbFlag(), minUserGames});
		PlayerStatRowMapper rowMapper = new PlayerStatRowMapper(gameType.getDbFlag());
		StringBuilder query = new StringBuilder(SQL_GET_RANKINGS_FOR_GAME_TYPE);
		if (limit > 0) {
			query.append(" limit ? offset ?");
		}
		List<PlayerStat> stats = getJdbcTemplate().query(query.toString(), new PreparedStatementSetter() {

			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, gameType.getDbFlag());
				ps.setInt(2, minUserGames);
				if (limit > 0) {
					ps.setInt(3, limit);
					ps.setInt(4, offset);
				}
			}
			
		}, rowMapper);
		return new ResultsPage(stats, size, offset);
	}

	public ResultsPage getPlayerStats(final int offset, final int limit) {
		int size = getJdbcTemplate().queryForInt(SQL_COUNT_RANKINGS, new Object[]{minUserGames});
		PlayerStatRowMapper rowMapper = new PlayerStatRowMapper(PlayerStat.COMBINED_GAME_TYPE);
		StringBuilder query = new StringBuilder(SQL_GET_OVERALL_RANKINGS);
		if (limit > 0) {
			query.append(" limit ? offset ?");
		}
		List<PlayerStat> stats = getJdbcTemplate().query(query.toString(), new PreparedStatementSetter() {

			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, minUserGames);
				if (limit > 0) {
					ps.setInt(2, limit);
					ps.setInt(3, offset);
				}
			}
			
		}, rowMapper);
		return new ResultsPage(stats, size, offset);
	}

	public int getMinUserGames() {
		return minUserGames;
	}

	public void setMinUserGames(int minUserGames) {
		this.minUserGames = minUserGames;
	}
	
	public PlayerExtendedStats getExtendedStats(String username) {
		return (PlayerExtendedStats) getJdbcTemplate().queryForObject(SQL_GET_EXT_STATS_FOR_USERNAME, new Object[] {username}, new ExtendedStatsRowMapper());
	}

	public PlayerExtendedStats getExtendedStats(Integer userId) {
		return (PlayerExtendedStats) getJdbcTemplate().queryForObject(SQL_GET_EXT_STATS_FOR_USER_ID, new Object[] {userId}, new ExtendedStatsRowMapper());
	}

	public Map<Integer, PlayerExtendedStats> getExtendedStatsForUserIds(
			Collection<Integer> userIds) {
		StringBuilder query = new StringBuilder(SQL_GET_EXT_STATS_FOR_USER_IDS).append("(");
		for (int i=0;i<userIds.size();i++) {
			query.append("?");
			if (i < userIds.size()-1) {
				query.append(",");
			}
		}
		query.append(")");
		List<PlayerExtendedStats> stats = getJdbcTemplate().query(query.toString(), userIds.toArray(), new ExtendedStatsRowMapper());
		Map<Integer, PlayerExtendedStats> statMap = new HashMap<Integer, PlayerExtendedStats>(userIds.size());
		for (PlayerExtendedStats stat : stats) {
			statMap.put(stat.getUserId(), stat);
		}
		return statMap;
	}

	public Map<String, PlayerExtendedStats> getExtendedStatsForUsernames(
			Collection<String> usernames) {
		StringBuilder query = new StringBuilder(SQL_GET_EXT_STATS_FOR_USERNAMES).append("(");
		for (int i=0;i<usernames.size();i++) {
			query.append("?");
			if (i < usernames.size()-1) {
				query.append(",");
			}
		}
		query.append(")");
		List<PlayerExtendedStats> stats = getJdbcTemplate().query(query.toString(), usernames.toArray(), new ExtendedStatsRowMapper());
		Map<String, PlayerExtendedStats> statMap = new HashMap<String, PlayerExtendedStats>(usernames.size());
		for (PlayerExtendedStats stat : stats) {
			statMap.put(stat.getUsername(), stat);
		}
		return statMap;
	}

	public Map<Integer, PlayerStat> getPlayerStatsForUserIds(
			final Collection<Integer> userIds) {
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<userIds.size();i++) {
			sb.append("?");
			if (i<userIds.size()-1) {
				sb.append(",");
			}
		}
		PlayerStatRowMapper rowMapper = new PlayerStatRowMapper(PlayerStat.COMBINED_GAME_TYPE);
		StringBuilder query = new StringBuilder(SQL_GET_PLAYER_STATS_FOR_USER_IDS.replaceAll("%users%", sb.toString()));
		List<PlayerStat> stats = getJdbcTemplate().query(query.toString(), new PreparedStatementSetter() {

			public void setValues(PreparedStatement ps) throws SQLException {
				int i = 1;
				for (Integer id : userIds) {
					ps.setInt(i++, id);
				}
			}
			
		}, rowMapper);
		LinkedHashMap<Integer, PlayerStat> m = new LinkedHashMap<Integer, PlayerStat>();
		for (PlayerStat s : stats) {
			m.put(s.getUserId(), s);
		}
		return m;
	}

	public Map<String, PlayerStat> getPlayerStatsForUsernames(
			final Collection<String> usernames) {
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<usernames.size();i++) {
			sb.append("?");
			if (i<usernames.size()-1) {
				sb.append(",");
			}
		}
		PlayerStatRowMapper rowMapper = new PlayerStatRowMapper(PlayerStat.COMBINED_GAME_TYPE);
		StringBuilder query = new StringBuilder(SQL_GET_PLAYER_STATS_FOR_USERNAMES.replaceAll("%users%", sb.toString()));
		List<PlayerStat> stats = getJdbcTemplate().query(query.toString(), new PreparedStatementSetter() {

			public void setValues(PreparedStatement ps) throws SQLException {
				int i = 1;
				for (String username : usernames) {
					ps.setString(i++, username);
				}
			}
			
		}, rowMapper);
		TreeMap<String, PlayerStat> m = new TreeMap<String, PlayerStat>();
		for (PlayerStat s : stats) {
			m.put(s.getUsername(), s);
		}
		return m;
	}

	public Integer getRankForUser(Integer userId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getRankForUser(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<Integer, Integer> getRanksForUserIds(Collection<Integer> userIds) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Integer> getRanksForUsernames(
			Collection<String> usernames) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteSimStats(final GameType gameType) {
		Object[] args = new Object[] {gameType.getDbFlag()};
		getJdbcTemplate().update(SQL_CLEAR_SIM_STATS, args);
		getJdbcTemplate().update(SQL_DELETE_SIM_GAME_PLAYERS, args);
		getJdbcTemplate().update(SQL_DELETE_SIM_GAMES, args);
		log.debug("delete all stats of type " + gameType.getDbFlag());
	}

	public ResultsPage getPlayersRankingsPage(Integer userId, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

}
