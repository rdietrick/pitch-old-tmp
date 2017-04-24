package com.pitchplayer.stats;

import java.util.Date;

import org.apache.log4j.Logger;

import com.pitchplayer.server.game.CardGame;
import com.pitchplayer.stats.dao.GameRecordDao;
import com.pitchplayer.stats.om.GamePlayerId;
import com.pitchplayer.stats.om.GamePlayerRecord;
import com.pitchplayer.stats.om.GameRecord;
import com.pitchplayer.userprofiling.UserService;
import com.pitchplayer.userprofiling.om.User;

public class GameRecordServiceImpl implements GameRecordService {

	GameRecordDao gameRecordDao;
	UserService userService;
	Logger log = Logger.getLogger(this.getClass().getName());
	
	public void createGameRecord(GameRecord gameRecord) {
		gameRecordDao.update(gameRecord);
	}

	public GameRecord getGameRecordByGameId(Integer gameId) {
		return gameRecordDao.getGameRecordById(gameId);
	}
	
	public void updateGameRecord(GameRecord gameRecord) {
		gameRecordDao.update(gameRecord);
	}

	public GameRecordDao getGameRecordDao() {
		return gameRecordDao;
	}

	public void setGameRecordDao(GameRecordDao gameRecordDao) {
		this.gameRecordDao = gameRecordDao;
	}

	public GameRecord logGameStart(CardGame game) {
		GameRecord gameRecord = getGameRecordByGameId(game.getGameId());
		gameRecord.setStartDate(new Date());
		int initialScore = game.getInitialPlayerScore();
		for (int i = 0, n = game.getNumPlayers(); i < n; i++) {
			User user = userService.getUserById(game.getPlayerId(i));
			GamePlayerRecord playerRec = new GamePlayerRecord();
			playerRec.setUser(user);
			playerRec.setSeat((byte) i);
			playerRec.setScore(new Short((short)initialScore));
			GamePlayerId id = new GamePlayerId(gameRecord.getGameId(), user.getUserId());
			playerRec.setId(id);
			gameRecord.addGamePlayerRecord(playerRec);
		}
		updateGameRecord(gameRecord);
		return gameRecord;		
	}

	public void deleteGameRecord(GameRecord gameRecord) {
		gameRecordDao.delete(gameRecord);
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}
