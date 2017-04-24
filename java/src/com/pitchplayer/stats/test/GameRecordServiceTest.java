package com.pitchplayer.stats.test;

import com.pitchplayer.server.game.GameFactory;
import com.pitchplayer.server.game.GameType;
import com.pitchplayer.stats.GameRecordService;
import com.pitchplayer.stats.om.GameRecord;
import com.pitchplayer.test.AbstractSpringTest;
import com.pitchplayer.userprofiling.UserService;

public class GameRecordServiceTest extends AbstractSpringTest {

	private GameRecordService gameService;
	private UserService userService;
	
	public GameRecordService getGameRecordService() {
		return gameService;
	}

	public void setGameRecordService(GameRecordService service) {
		this.gameService = service;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
		
	public void testGameRecordCreate() {
		GameRecord gameRec = new GameRecord();
		gameRec.setGameType(GameType.SINGLES.getDbFlag());
		gameRec.setSim((byte)0);
		gameService.createGameRecord(gameRec);
		
	}
	
	public void testGamePlayerUpdates() {
		
	}


}
