-- added column to capture newsletter subscription status
alter table user_pref add column subscribe_newsletter tinyint default 1 after show_state;