package com.pitchplayer.stats.dao;

import java.util.List;

import com.pitchplayer.stats.om.GameRecord;

public interface GameRecordDao {

	public void update(GameRecord gameRecord);
	
	public GameRecord getGameRecordById(Integer gameRecordId);
	
	public List<GameRecord> getGameRecordsByPlayerId(Integer playerId);

	public void delete(GameRecord gameRecord);
	
}
