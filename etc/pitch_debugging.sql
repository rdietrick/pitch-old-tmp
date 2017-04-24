select * from game_player where user_id = 4 and winner = 1;

select a.*, b.*, c.username from game_player a, game b, user c where a.game_id = b.game_id and a.user_id = c.user_id and b.game_type = 1 or b.game_type = 2;

select * from player_stat where games > 0;

select distinct(game_type) from game;

select * from user;

select * from game a, game_player b where a.game_id = b.game_id and game_type < 3;



update player_stat set games = 0, wins = 0, losses = 0, quits = 0, win_pct = 0;
delete from game_player;
delete from game;
commit;
update game_player set winner = 0 where winner is null;
