
# drop table if exists GENRE_SONG;
drop table if exists SONG_ARTIST;
drop table if exists SONGS_IN_PLAYLIST;
# drop table if exists GENRE;
drop table if exists SONG;
drop table if exists ALBUM;
drop table if exists ARTIST;
drop table if exists PLAYLIST;
drop table if exists USER;
drop table if exists last_ids;

CREATE TABLE USER
(
    USERNAME   varchar(35) not null,
    PASSWORD   bigint      not null,
    FIRST_NAME varchar(15) null,
    LAST_NAME  varchar(15) null,
    JOIN_DATE  date        null,
    EMAIL      varchar(75) not null,
    PHOTO      mediumblob  null,
    CONSTRAINT USER_PK PRIMARY KEY (USERNAME)
);

CREATE TABLE PLAYLIST
(
    PLAYLIST_ID         int auto_increment,
    NAME                VARCHAR(60) null,
    USERNAME            VARCHAR(35) null,

    CONSTRAINT PLAYLIST_PK PRIMARY KEY (PLAYLIST_ID),

    CONSTRAINT PLAYLIST_FK FOREIGN KEY (USERNAME)
        REFERENCES USER(USERNAME)
            ON DELETE CASCADE
);

CREATE TABLE ARTIST
(
    ARTIST_ID   int auto_increment,
    FIRST_NAME  VARCHAR(15) null,
    LAST_NAME   VARCHAR(15) null,

    CONSTRAINT ARTIST_PK PRIMARY KEY (ARTIST_ID)
);

CREATE TABLE ALBUM
(
    ALBUM_ID    int auto_increment,
    NAME        VARCHAR(40) null ,
    ARTIST_ID   int not null,
    CONSTRAINT ALBUM_PK PRIMARY KEY (ALBUM_ID),
    CONSTRAINT ALBUM_FK FOREIGN KEY (ARTIST_ID)
        REFERENCES ARTIST(ARTIST_ID)
            ON DELETE CASCADE
            ON UPDATE CASCADE
);

CREATE TABLE SONG
(
    SONG_ID     int auto_increment,
    NAME        VARCHAR(60) NOT NULL,
#     DURATION    INT,  #in seconds
    PATH        VARCHAR(100) NOT NULL,
    ALBUM_ID    INT NULL ,
#     Genre_ID    VARCHAR(5),

    CONSTRAINT SONG_PK PRIMARY KEY (SONG_ID),

    CONSTRAINT SONG_FK FOREIGN KEY (ALBUM_ID)
        REFERENCES ALBUM(ALBUM_ID)
            ON DELETE SET NULL
            ON UPDATE CASCADE
);

####################### Junction Tables ###########################

CREATE TABLE SONGS_IN_PLAYLIST
(
    SONG_ID         int,
    PLAYLIST_ID     int,

    CONSTRAINT SONGS_IN_PLAYLIST_PK PRIMARY KEY (SONG_ID, PLAYLIST_ID),

    CONSTRAINT SONGS_IN_PLAYLIST_FK1 FOREIGN KEY (SONG_ID)
        REFERENCES SONG(SONG_ID)
            ON UPDATE CASCADE
            ON DELETE CASCADE,
    CONSTRAINT SONGS_IN_PLAYLIST_FK2 FOREIGN KEY (PLAYLIST_ID)
        REFERENCES PLAYLIST(PLAYLIST_ID)
            ON UPDATE CASCADE
            ON DELETE CASCADE
);

CREATE TABLE SONG_ARTIST
(
    SONG_ID         int,
    ARTIST_ID       int,
#     IS_FEATURED     BOOL, # 1 for featured, 0 for owner.
    CONSTRAINT SONG_ARTIST_PK PRIMARY KEY (SONG_ID, ARTIST_ID),
    CONSTRAINT SONG_ARTIST_FK1 FOREIGN KEY (SONG_ID)
        REFERENCES SONG(SONG_ID)
            ON UPDATE CASCADE
            ON DELETE CASCADE,
    CONSTRAINT SONG_ARTIST_FK2 FOREIGN KEY (ARTIST_ID)
        REFERENCES ARTIST(ARTIST_ID)
            ON UPDATE CASCADE
            ON DELETE CASCADE
);

drop trigger if exists INSERT_DATE;
    DELIMITER //
CREATE TRIGGER INSERT_DATE
    BEFORE INSERT ON USER
    FOR EACH ROW
BEGIN
    SET NEW.JOIN_DATE = SYSDATE();
END //
    DELIMITER ;

drop trigger if exists last_state_new_user;
    DELIMITER //
CREATE TRIGGER last_state_new_user
    AFTER INSERT ON USER
    FOR EACH ROW
BEGIN
    INSERT INTO last_state(username) VALUES (NEW.USERNAME);
END //
    DELIMITER ;

#
# SELECT Concat('DROP TRIGGER ', Trigger_Name, ';') FROM  information_schema.TRIGGERS WHERE TRIGGER_SCHEMA = 'PHOENIX';
# DROP TRIGGER GEN_SONG_ID;
# DROP TRIGGER GEN_ALBUM_ID;
# DROP TRIGGER GEN_GENRE_ID;
# DROP TRIGGER GEN_PLAYLIST_ID;
# DROP TRIGGER GEN_ARTIST_ID;
