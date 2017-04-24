-- Server version       5.0.27

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


DROP TABLE player_stat;
DROP TABLE game_player;
DROP TABLE game;
DROP TABLE user_home_address;
DROP TABLE user;

CREATE TABLE user
(
user_id		INTEGER            UNSIGNED PRIMARY KEY AUTO_INCREMENT,    -- users id number
username	VARCHAR(16)        NOT NULL,  -- users login name
passwd_hash	VARCHAR(41)        NOT NULL,  -- users password
status 		tinyint			DEFAULT 1 NOT NULL,
registration_date	TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
user_type	tinyint,			-- whether or not player is CPU player
logged_in	tinyint,			-- is player logged in?
first_name	VARCHAR(32),                 -- user's first name
last_name	VARCHAR(32),                 -- user's last name
email_address	VARCHAR(128),                -- user's email address
last_login	DATETIME,                    -- date of user's last login
login_count		INTEGER    UNSIGNED DEFAULT 0,          -- number of times logged in
birth_date datetime default NULL,
session_id varchar(64) default NULL
) TYPE = INNODB DEFAULT CHARSET=utf8;

ALTER TABLE user AUTO_INCREMENT = 1000;
CREATE UNIQUE INDEX user_username_uix on user(username);
CREATE UNIQUE INDEX user_email_uix on user(email_address);

CREATE TABLE user_home_address
(
user_id		INTEGER UNSIGNED PRIMARY KEY,
city		VARCHAR(64),
state		VARCHAR(2),
country		VARCHAR(64),
home_phone	VARCHAR(32),
mobile_phone	VARCHAR(32),
FOREIGN KEY user_home_address_fk1 (user_id) REFERENCES user(user_id) ON DELETE CASCADE ON UPDATE CASCADE
) TYPE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE user_pref
(
	user_id		INTEGER UNSIGNED PRIMARY KEY,
	show_name	tinyint NOT NULL DEFAULT 0,
	show_city	tinyint NOT NULL DEFAULT 0,
	show_state	tinyint NOT NULL DEFAULT 0,
	subscribe_newsletter tinyint default 1,
	date_updated	datetime,
	FOREIGN KEY user_prefs_fk1 (user_id) REFERENCES user(user_id) ON DELETE CASCADE ON UPDATE CASCADE
) TYPE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE user_game_pref
(
	user_id				INTEGER UNSIGNED PRIMARY KEY,
	dflt_game_type		TINYINT NOT NULL DEFAULT 1,
	dflt_low_scoring	TINYINT NOT NULL DEFAULT 1,
	dflt_challenge_send	TINYINT NOT NULL DEFAULT 1,   
	dflt_challenge_show	TINYINT NOT NULL DEFAULT 2,   
	date_updated	datetime,	
	FOREIGN KEY user_game_prefs_fk1 (user_id) REFERENCES user(user_id) ON DELETE CASCADE ON UPDATE CASCADE
) TYPE = INNODB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `cpu_player`;
CREATE TABLE `cpu_player` (
	user_id			int(10) unsigned NOT NULL,
	class_name		varchar(128) NOT NULL,
	player_type		tinyint(4) NOT NULL,
	skill_level		tinyint NOT NULL,
	status			varchar(32) NOT NULL DEFAULT 'ENABLED',	
	PRIMARY KEY  (`user_id`),
	CONSTRAINT `cpu_player_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


--
-- Table structure for table `email_validation`
--

DROP TABLE IF EXISTS `email_validation`;
CREATE TABLE email_validation (
	user_id				int(10) unsigned NOT NULL,
	validation_code		varchar(64) NOT NULL,
	validation_status	tinyint(4) default NULL,
	sent_date			datetime default NULL,
	validated_date		datetime default NULL,
  PRIMARY KEY  (user_id),
  CONSTRAINT `email_validation_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

CREATE UNIQUE INDEX email_val_uix1 on email_validation(validation_code);


CREATE TABLE game
(
	game_id			INTEGER	UNSIGNED PRIMARY KEY AUTO_INCREMENT,    -- game id
	game_type		TINYINT NOT NULL,		-- type of game (0=singles, 1=doubles)
	winner_id		INTEGER	UNSIGNED,		-- id of winner
	quitter_id		INTEGER	UNSIGNED,		-- id of quitter
	start_date		DATETIME,           		-- date of game start
	end_date		DATETIME,            		-- date of game end
	sim				tinyint DEFAULT 0,		-- whether the game is a simulation
	crnt_hand		SMALLINT,			-- how many hands played so far (useful for restarting quit games)
	FOREIGN KEY game_fk1 (winner_id) REFERENCES user(user_id),
	FOREIGN KEY game_fk2 (quitter_id) REFERENCES user(user_id)
) TYPE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE game_player
(
	game_id			INTEGER UNSIGNED NOT NULL,
	user_id			INTEGER UNSIGNED NOT NULL,
	seat			TINYINT NOT NULL,		-- player index in initial rotation
	score			SMALLINT,			-- final score
	winner			TINYINT,			-- whether or not this player won the game,
	jack_points		SMALLINT,
	game_points		SMALLINT,
	ups				SMALLINT,
	jack_steal		SMALLINT,
	jack_loss		SMALLINT,
	PRIMARY KEY (game_id, user_id),			-- composite primary key on user & game IDs
	FOREIGN KEY game_player_fk1 (game_id) REFERENCES game(game_id),
	FOREIGN KEY game_player_fk2 (user_id) REFERENCES user(user_id)
) TYPE = INNODB DEFAULT CHARSET=utf8;

-- create table player_stat
CREATE TABLE player_stat
(
   player_stat_id 		INTEGER UNSIGNED PRIMARY KEY AUTO_INCREMENT,
   user_id 				INTEGER UNSIGNED NOT NULL,
   username 			varchar(16) NOT NULL,
   game_type 			TINYINT NOT NULL,
   games 				INTEGER,
   wins 				INTEGER,
   losses 				INTEGER,
   quits				INTEGER,
   win_pct				DECIMAL(4,3),
   FOREIGN KEY player_stat_fk1 (user_id) REFERENCES user (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
   UNIQUE(user_id, game_type)
) TYPE=INNODB DEFAULT CHARSET=utf8;



-- create player_extended_stats view
CREATE VIEW player_extended_stats AS 
select b.user_id AS user_id,
c.username AS username,
avg(b.game_points) AS game_avg,
avg(b.jack_points) AS jack_avg,
avg(b.jack_steal) AS jack_steal_avg,
avg(b.jack_loss) AS jack_loss_avg,
avg(b.ups) AS ups_avg 
from ((game a join game_player b on a.game_id = b.game_id) join user c on b.user_id = c.user_id)
where 
a.winner_id is not null and
a.game_type = 1 or a.game_type = 2 
group by b.user_id;


-- prepare all users for stats 
insert into player_stat (user_id, username, game_type, games, wins, losses, win_pct)
select user_id, username, 1, 0, 0, 0, 0 from user;

insert into player_stat (user_id, username, game_type, games, wins, losses, win_pct)
select user_id, username, 2, 0, 0, 0, 0 from user;

insert into player_stat (user_id, username, game_type, games, wins, losses, win_pct)
select user_id, username, 3, 0, 0, 0, 0 from user where user_type = 1;



-- create table for user invitations
-- TODO: place unique constraint on invitation_code
DROP TABLE IF EXISTS `user_invitation`; 
create table user_invitation 
(
	invitation_id		INTEGER UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	invitation_code		VARCHAR(64) NOT NULL,
	inviter_id			INTEGER UNSIGNED NOT NULL,
	invitee_email		VARCHAR(128) NOT NULL,
	status				tinyint NOT NULL,
	sent_date			DATETIME NOT NULL,
	FOREIGN KEY user_invitation_fk1 (inviter_id) REFERENCES user(user_id) ON DELETE CASCADE ON UPDATE CASCADE
) TYPE = INNODB DEFAULT CHARSET=utf8;

CREATE UNIQUE INDEX user_invitation_uix1 on user_invitation(invitation_code);


-- create table for user associations (friends, partners, etc.)
DROP TABLE IF EXISTS `user_association`;
CREATE TABLE user_association
(
	user_association_id	INTEGER UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	user_id				INTEGER UNSIGNED NOT NULL,
	associate_id		INTEGER UNSIGNED NOT NULL,
	association_type	TINYINT NOT NULL,
	date_created		DATETIME NOT NULL,
	date_accepted		DATETIME,
	status				TINYINT,
	FOREIGN KEY user_associate_fk1 (user_id) REFERENCES user(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY user_associate_fk2 (associate_id) REFERENCES user(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
        UNIQUE(user_id, associate_id, association_type)
) TYPE = INNODB DEFAULT CHARSET=utf8;


-- create stored proc to clear all stats
-- SHOULD ONLY BE USED ON DEV DBs!!!
-- in prod, use clearSimStats()
CREATE PROCEDURE clearAllStats()
BEGIN
	delete from game_player;
	delete from game;
	update player_stat set wins = 0, games = 0, losses = 0, win_pct = 0;
END;

CREATE PROCEDURE clearSimStats()
BEGIN
	delete from game_player where game_id in (select game_id from game where game_type = 3);
	delete from game where game_type = 3;
	update player_stat set wins = 0, games = 0, losses = 0, quits = 0, win_pct = 0 where game_type = 3;
END;



-- initialize CPU players
INSERT INTO user (user_id, username, passwd_hash, registration_date, user_type) values
(1, 'dummy', password('poiulkjh'), NOW(), 1),
(2, 'smart', password('poiulkjh'), NOW(), 1),
(3, 'original', password('poiulkjh'), NOW(), 1),
(4, 'wiseguy', password('poiulkjh'), NOW(), 1),
(5, 'cardcounter', password('poiulkjh'), NOW(), 1);

INSERT INTO `cpu_player` VALUES 
(1,'com.pitchplayer.server.game.player.DummyCPUPlayer',1, 2, 50),
(2,'com.pitchplayer.server.game.player.SmartCPUPlayer',1, 2, 50),
(3,'com.pitchplayer.server.game.player.OriginalCPUPlayer',1, 2, 50),
(4,'com.pitchplayer.server.game.player.SmartAssCPUPlayer',1, 2, 50),
(5,'com.pitchplayer.server.game.player.CardCountingCPUPlayer',1, 3, 50);


-- create trigger for player_stats update
-- run as root
-- TODO: this shouold be replaced with a statement/procedure invoked via JDBC after game update
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
