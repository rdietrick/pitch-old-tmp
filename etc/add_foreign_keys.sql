alter table game_player add constraint foreign key
(user_id)
references user.user_id;

alter table game_player add constraint foreign key
(game_id)
references game.game_id;

alter table userinfo
add constraint foreign key (user_id)
references user.user_id;
