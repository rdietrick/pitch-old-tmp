DROP TABLE game_player_tmp;
DROP TABLE game_tmp;
DROP TABLE user_home_address;
DROP TABLE user_tmp;

CREATE TABLE user_tmp
(
user_id		INTEGER            UNSIGNED PRIMARY KEY AUTO_INCREMENT,    -- users id number
username	VARCHAR(16)        NOT NULL,  -- users login name
passwd_hash	VARCHAR(41)        NOT NULL,  -- users password
registration_date	TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
user_type	tinyint,			-- whether or not player is CPU player
logged_in	tinyint,			-- is player logged in?
session_id	VARCHAR(64),		     -- HTTP Session ID
first_name	VARCHAR(32),                 -- user's first name
last_name	VARCHAR(32),                 -- user's last name
email_address	VARCHAR(128),                -- user's email address
last_login	DATETIME,                    -- date of user's last login
login_count		INTEGER    UNSIGNED DEFAULT 0,          -- number of times logged in
UNIQUE (username)
) TYPE = INNODB DEFAULT CHARSET=utf8;


CREATE TABLE user_home_address
(
user_id		INTEGER            UNSIGNED PRIMARY KEY,
city		VARCHAR(64),
state		VARCHAR(2),
country		VARCHAR(64),
home_phone	VARCHAR(32),
mobile_phone	VARCHAR(32),
FOREIGN KEY user_home_address_fk1 (user_id) REFERENCES user_tmp(user_id) ON DELETE CASCADE ON UPDATE CASCADE
) TYPE = INNODB DEFAULT CHARSET=utf8;


CREATE TABLE game_tmp
(
game_id		INTEGER	UNSIGNED PRIMARY KEY AUTO_INCREMENT,    -- game id
game_type	TINYINT NOT NULL,		-- type of game (0=singles, 1=doubles)
winner_id	INTEGER	UNSIGNED,		-- id of winner
quitter_id	INTEGER	UNSIGNED,		-- id of winner
start_date	DATETIME,           		-- date of game start
end_date	DATETIME,            		-- date of game end
sim		tinyint DEFAULT 0,		-- whether the game is a simulation
crnt_hand	SMALLINT,			-- how many hands played so far (useful for restarting quit games)
FOREIGN KEY game_fk1 (winner_id) REFERENCES user_tmp(user_id),
FOREIGN KEY game_fk2 (quitter_id) REFERENCES user_tmp(user_id)
) TYPE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE game_player_tmp
(
game_id		INTEGER UNSIGNED NOT NULL,
user_id		INTEGER UNSIGNED NOT NULL,
seat		TINYINT NOT NULL,		-- player index in initial rotation
score		SMALLINT,			-- final score
winner		TINYINT,			-- whether or not this player won the game,
jack_points	SMALLINT,
game_points	SMALLINT,
ups		SMALLINT,
jack_steal	SMALLINT,
jack_loss	SMALLINT,
PRIMARY KEY (game_id, user_id),			-- composite primary key on user & game IDs
FOREIGN KEY game_player_fk1 (game_id) REFERENCES game_tmp(game_id),
FOREIGN KEY game_player_fk2 (user_id) REFERENCES user_tmp(user_id)
) TYPE = INNODB DEFAULT CHARSET=utf8;


CREATE TEMPORARY TABLE user_sel_tmp
(
	user_id INTEGER UNSIGNED,
	username VARCHAR(16),
	passwd VARCHAR(41),
	reg_date DATETIME,
	isCPU TINYINT,
	f_name VARCHAR(32),
	l_name VARCHAR(32),
	email VARCHAR(128))
select 
	a.user_id, 
	a.username, 
	a.passwd, 
	a.reg_date, 
	a.isCPU,
	0,
	b.f_name,
	b.l_name,
	b.email
from user a, userinfo b 
where a.user_id = b.user_id;

insert into user_tmp 
(
	user_id,
	username,
	passwd_hash,
	registration_date,
	user_type,
	logged_in,
	first_name,
	last_name,
	email_address
)
select 
	user_id, 
	username, 
	passwd, 
	reg_date, 
	isCPU,
	0,
	f_name,
	l_name,
	email
from user_sel_tmp;


-- create table player_stat
CREATE TABLE player_stat
(
   player_stat_id INTEGER UNSIGNED PRIMARY KEY AUTO_INCREMENT,
   user_id INTEGER UNSIGNED NOT NULL,
   username varchar(16) NOT NULL,
   game_type TINYINT NOT NULL,
   games INTEGER,
   wins INTEGER,
   losses INTEGER,
   win_pct DECIMAL(4,3),
   FOREIGN KEY player_stat_fk1 (user_id) REFERENCES user (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
   UNIQUE(user_id, game_type)
) TYPE=INNODB DEFAULT CHARSET=utf8;



-- this was taken from MySQL using 'show create view player_exteneded_stats'
-- this is not the original stmt
CREATE VIEW player_extended_stats AS select `b`.`user_id` AS `user_id`,`c`.`username` AS `username`,count(0) AS `games`,avg(`b`.`game_points`) AS `game_avg`,avg(`b`.`jack_points`) AS `jack_avg`,avg(`b`.`jack_steal`) AS `jack_steal_avg`,avg(`b`.`jack_loss`) AS `jack_loss_avg`,avg(`b`.`ups`) AS `ups_avg` from ((`game` `a` join `game_player` `b`) join `user` `c`) where ((`a`.`game_id` = `b`.`game_id`) and (`a`.`winner_id` is not null) and (`b`.`user_id` = `c`.`user_id`)) group by `b`.`user_id`

-- create trigger to init player_stats table on user insert
delimiter |
CREATE TRIGGER stat_prepare_trig AFTER INSERT ON user
     FOR EACH ROW BEGIN
	insert into player_stats set user_id = NEW.user_id, username = NEW.username, game_type = 1, games = 0, wins = 0, losses = 0, win_pct = 0;
	insert into player_stats set user_id = NEW.user_id, username = NEW.username, game_type = 2, games = 0, wins = 0, losses = 0, win_pct = 0;
     END;
|
delimiter ;

-- prepare all users for stats 
insert into player_stats (user_id, username, game_type, games, wins, losses, win_pct)
select user_id, username, 1, 0, 0, 0, 0 from user;

insert into player_stats (user_id, username, game_type, games, wins, losses, win_pct)
select user_id, username, 2, 0, 0, 0, 0 from user;

insert into player_stats (user_id, username, game_type, games, wins, losses, win_pct)
select user_id, username, 3, 0, 0, 0, 0 from user where user_type = 1;


-- create trigger for player_stats update

CREATE TRIGGER stat_update_trig BEFORE UPDATE ON game 
FOR EACH ROW BEGIN
	IF OLD.end_date is null and NEW.end_date is not null THEN
		UPDATE player_stat set wins = wins + 1, games = games + 1, win_pct = (wins/games)
		where user_id = NEW.winner_id and game_type = NEW.game_type;
		UPDATE player_stat set losses = losses + 1, games = games + 1, win_pct = (wins/games)
		where user_id in
		(select user_id from game_player where game_id = NEW.game_id and user_id <> NEW.winner_id)
		and game_type = NEW.game_type;
	END IF;
END;
|
delimiter ;

-- create table for user invitations
-- TODO: place unique constraint on invitation_code
create table user_invitation 
(
	invitation_id		INTEGER UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	invitation_code		VARCHAR(64) NOT NULL,
	inviter_id		INTEGER UNSIGNED NOT NULL,
	invitee_email		VARCHAR(128) NOT NULL,
	status			tinyint NOT NULL,
	sent_date		DATETIME NOT NULL,
	FOREIGN KEY user_invitation_fk1 (inviter_id) REFERENCES user(user_id) ON DELETE CASCADE ON UPDATE CASCADE
) TYPE = INNODB DEFAULT CHARSET=utf8;


-- create table for user associations (friends, partners, etc.)
CREATE TABLE user_association
(
	user_association_id	INTEGER UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	user_id			INTEGER UNSIGNED NOT NULL,
	associate_id		INTEGER UNSIGNED NOT NULL,
	association_type	TINYINT NOT NULL,
	date_created		DATETIME NOT NULL,
	date_accepted		DATETIME,
	status			TINYINT,
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
	update player_stat set wins = 0, games = 0, losses = 0, win_pct = 0 where game_type = 3;
END;




