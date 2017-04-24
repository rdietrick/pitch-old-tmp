package com.pitchplayer.stats;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pitchplayer.server.game.CardGame;
import com.pitchplayer.stats.om.GameRecord;

@Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
public interface GameRecordService {

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public void createGameRecord(GameRecord gameRecord);
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public void updateGameRecord(GameRecord gameRecord);
	
	public GameRecord getGameRecordByGameId(Integer gameId);

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public GameRecord logGameStart(CardGame game);

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public void deleteGameRecord(GameRecord gameRecord);
	
}
