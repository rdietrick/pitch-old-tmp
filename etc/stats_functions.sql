DELIMITER $$

DROP PROCEDURE IF EXISTS `pitch_db2`.`clearStats`$$
CREATE PROCEDURE `pitch_db2`.`clearStats` ()
BEGIN
	update player_stat set games = 0, wins = 0, losses = 0, quits = 0, win_pct = 0;
	delete from game_player;
	delete from game;	
END$$

DELIMITER ;
