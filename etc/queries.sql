-- get user point stats
select b.user_id user_id, 
count(*) games, 
avg(b.winner) win_perc, 
avg(b.game_points) 'game/game',
avg(b.jack_points) 'jack/game',
avg(b.jack_steal) 'j steals/game',
avg(b.jack_loss) 'j loss/game',
avg(ups) 'ups/game'
from game a, game_player b
where
a.game_id = b.game_id
group by user_id;
