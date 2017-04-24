--
-- Script to create tables used by the Pitch Server
--

-- DATABASE: 	pitch_db
-- the pitch database
-- CREATE DATABASE pitch_db;



-- TABLE: 	user
-- all users of the pitch server stored in this table
-- DROP TABLE user;
CREATE TABLE user
(
user_id            INTEGER            UNSIGNED PRIMARY KEY AUTO_INCREMENT,    -- users id number
username          VARCHAR(16)        NOT NULL,  -- users login name
passwd            VARCHAR(41)        NOT NULL,  -- users password
isCPU		  tinyint,			-- whether or not player is CPU player
logged_in	  tinyint,			-- is player logged in?
UNIQUE (username)
);



-- TABLE:    userinfo
-- user information
-- DROP TABLE userinfo;
CREATE TABLE userinfo
(
user_id      INTEGER            UNSIGNED PRIMARY KEY    -- user's id number
REFERENCES user.user_id
ON DELETE CASCADE
ON UPDATE CASCADE,
f_name		VARCHAR(32),                 -- user's first name
l_name		VARCHAR(32),                 -- user's last name
email		VARCHAR(128),                -- user's email address
hometown	VARCHAR(64),                   -- user's hometown
homestate	VARCHAR(2),                   -- user's home state
last_login	DATETIME,                    -- date of user's last login
logins		INTEGER    UNSIGNED DEFAULT 0          -- number of times logged in
);



-- TABLE: game
-- record of all played games
-- DROP TABLE game;
CREATE TABLE game 
(
game_id		INTEGER	UNSIGNED PRIMARY KEY AUTO_INCREMENT,    -- game id
game_type	TINYINT NOT NULL,		-- type of game (0=singles, 1=doubles)
winner_id	INTEGER	UNSIGNED REFERENCES user.user_id,	-- id of winner
quitter_id	INTEGER	UNSIGNED REFERENCES user.user_id,	-- id of winner
start_date	DATETIME,           		-- date of game start
end_date	DATETIME,            		-- date of game end
sim		tinyint			-- whether the game is a simulation
);

-- TABLE: game_player
-- players in a game
-- DROP TABLE game_player;
CREATE TABLE game_player
(
game_id		INTEGER UNSIGNED NOT NULL REFERENCES game.game_id,
user_id		INTEGER UNSIGNED NOT NULL REFERENCES user.user_id,
seat		TINYINT NOT NULL,		-- player index in initial rotation
score		SMALLINT,
winner		TINYINT,			-- whether or not this player won the game
jack_points	SMALLINT,
game_points	SMALLINT,
ups		SMALLINT,
jack_steal	SMALLINT
);
