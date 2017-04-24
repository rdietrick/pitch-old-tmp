-- This script should be run as the root mysql user to create
-- the user 'pitch' and grant the account appropriate 
-- privileges

grant all privileges on pitch_db2.* to pitch@localhost
identified by 'pittston!';

grant all privileges on pitch_db2.* to pitch@"%"
identified by 'pittston!';

flush privileges;

create database pitch_db2;
