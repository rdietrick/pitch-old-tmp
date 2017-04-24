drop trigger stat_update_trig;

DELIMITER ;;
/*!50003 SET SESSION SQL_MODE="" */;;
/*!50003 CREATE */ /*!50017 DEFINER=`root`@`localhost` */ /*!50003 TRIGGER `stat_update_trig` BEFORE UPDATE ON `game` FOR EACH ROW BEGIN
        IF OLD.winner_id is null and NEW.winner_id is not null THEN
                CREATE TEMPORARY TABLE winners (user_id integer);
                IF NEW.game_type = 2 OR NEW.game_type = 4 THEN
                        insert into winners SELECT user_id from game_player where game_id = NEW.game_id and mod(seat, 2) =
                        (select seat from game_player a, game b where b.game_id = NEW.game_id and a.game_id = b.game_id and a.user_id = NEW.winner_id);
                ELSE
                        insert into winners SELECT user_id from game_player where game_id = NEW.game_id and user_id = NEW.winner_id;
                END IF;         
                UPDATE player_stat set wins = wins + 1, games = games + 1, win_pct = (wins/games)
                where user_id in (select user_id from winners) and game_type = NEW.game_type;
                UPDATE player_stat set losses = losses + 1, games = games + 1, win_pct = (wins/games)
                where user_id in (select user_id from game_player where game_id = NEW.game_id)
                and user_id not in (select user_id from winners) and game_type = NEW.game_type;
                DROP TEMPORARY TABLE winners;
        ELSEIF NEW.quitter_id > 0 THEN
                UPDATE player_stat set quits = quits + 1 where user_id = NEW.quitter_id and game_type = NEW.game_type;
        END IF;
END */;;

drop trigger stat_prepare_trig;

CREATE TRIGGER stat_prepare_trig AFTER INSERT ON user
FOR EACH ROW BEGIN
	insert into player_stat set user_id = NEW.user_id, username = NEW.username, game_type = 1, games = 0, wins = 0, losses = 0, quits = 0, win_pct = 0;
	insert into player_stat set user_id = NEW.user_id, username = NEW.username, game_type = 2, games = 0, wins = 0, losses = 0, quits = 0, win_pct = 0;
	IF NEW.user_type = 1 THEN
		insert into player_stat set user_id = NEW.user_id, username = NEW.username, game_type = 3, games = 0, wins = 0, losses = 0, quits = 0, win_pct = 0;
		insert into player_stat set user_id = NEW.user_id, username = NEW.username, game_type = 4, games = 0, wins = 0, losses = 0, quits = 0, win_pct = 0;
	END IF;
END;
;;
delimiter ;