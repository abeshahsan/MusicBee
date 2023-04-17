package com.musicbee.utility;

import com.musicbee.entities.Playlist;
import com.musicbee.entities.Song;
import com.musicbee.entities.User;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;

public class Database {
    private static Connection connection;
    private static final String DB_USERNAME;
    private static final String DB_PASSWORD;

    private static User currentUser;

    private static final ArrayList<Song> ALL_SONGS;
    private static final ArrayList<Playlist> ALL_PLAYLISTS;
    private static final ArrayList<Song> CURRENT_PLAYLIST_SONGS;

    //static initialization.
    static {
        DB_USERNAME = "root";
        DB_PASSWORD = "dbms";

        ALL_SONGS = new ArrayList<>();
        ALL_PLAYLISTS = new ArrayList<>();
        CURRENT_PLAYLIST_SONGS = new ArrayList<>();
    }

    public static void prepareDatabase() throws Exception {
        connection = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/phoenix", DB_USERNAME, DB_PASSWORD);
        currentUser = null;
    }

    public static User signIn(String username, long password) throws Exception {
        String sqlString = "select * from USER where USERNAME = ? and PASSWORD = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlString);
        preparedStatement.setString(1, username);
        preparedStatement.setLong(2, password);
        ResultSet resultSet = preparedStatement.executeQuery();

        if(resultSet.next()) {
            currentUser = new User(resultSet.getString(1), resultSet.getLong(2));
            currentUser.setFirstName(resultSet.getString(3));
            currentUser.setLastName(resultSet.getString(4));
            currentUser.setJoinDate(resultSet.getDate(5));
            currentUser.setEmail(resultSet.getString(6));

            Image image;
            try {
                InputStream inputStream = resultSet.getBinaryStream(7);
                if(!resultSet.wasNull()) {
                    image = new Image(inputStream);
                    currentUser.setImage(image);
                }
            } catch (SQLException ignored) {
            }

            return currentUser;
        }
        return null;
    }
    public static void signUp(User user) throws Exception {
        String sqlString = "insert into user (username, password, first_name, last_name, EMAIL) values (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlString);
        preparedStatement.setString(1, user.getUsername());
        preparedStatement.setLong(2, user.getPassword());
        preparedStatement.setString(3, user.getFirstName());
        preparedStatement.setString(4, user.getLastName());
        preparedStatement.setString(5, user.getEmail());
        preparedStatement.executeUpdate();

        sqlString = "select sysdate()";

        preparedStatement = connection.prepareStatement(sqlString);
        ResultSet rs = preparedStatement.executeQuery();

        currentUser = user;

        if(rs.next()) {
            currentUser.setJoinDate(rs.getDate(1));
        }
        else currentUser.setJoinDate(null);
    }
    public static void updateCurrentUserInfo(User user) throws Exception {
        String sqlString = "update user set PASSWORD = ?, FIRST_NAME = ?, LAST_NAME = ?, EMAIL = ? where USERNAME = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlString);
        preparedStatement.setLong(1, user.getPassword());
        preparedStatement.setString(2, user.getFirstName());
        preparedStatement.setString(3, user.getLastName());
        preparedStatement.setString(4, user.getEmail());
        preparedStatement.setString(5, user.getUsername());
        preparedStatement.executeUpdate();

        currentUser = user;
    }

    public static void updateUserPhoto(File file) throws Exception {
        String sqlString = "update user set PHOTO = ? where USERNAME = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlString);
        preparedStatement.setString(2, getCurrentUser().getUsername());

        InputStream photo = new FileInputStream(file);
        preparedStatement.setBlob(1, photo);

        preparedStatement.executeUpdate();

        getCurrentUser().setImage(new Image(new FileInputStream(file)));
    }

    public static User getCurrentUser()
    {
        return  currentUser;
    }
    public static void loadAllSongs() throws SQLException {
        String sqlString =
                """
                select T2.*, artist.first_name, artist.last_name from
                    (select T1.*, song_artist.ARTIST_ID from (select song.*, album.NAME as ALBUM from song left join album on song.ALBUM_ID = album.ALBUM_ID) T1
                        left join song_artist on T1.SONG_ID = song_artist.SONG_ID) T2 left join artist
                        on T2.ARTIST_ID = artist.ARTIST_ID
                        order by NAME;
                """;

        PreparedStatement preparedStatement = connection.prepareStatement(sqlString);

        ResultSet resultSet = preparedStatement.executeQuery();

        while(resultSet.next()) {
            Song song = new Song(resultSet.getInt(1),
                    resultSet.getString(2), resultSet.getString(3));
            song.setAlbumName(resultSet.getString(5));
            song.setArtistFirstName(resultSet.getString(7));
            song.setArtistLastName(resultSet.getString(8));
            ALL_SONGS.add(song);
            getLengthOfSong(song);
        }
    }

    private static void getLengthOfSong(Song song) {
        Media media = new Media( (new File(song.getPath())).toURI().toString() );
        MediaPlayer player = new MediaPlayer( media );

        player.setOnReady(()-> {
            double totalTime = player.getTotalDuration().toMillis();
            song.setLength(totalTime);
//                System.out.println(totalTime);
            player.pause();
        });
    }

    public static ArrayList<Song> getAllSongs() {
       return ALL_SONGS;
    }
    public static void logOutCurrentUser() throws SQLException {
        Database.saveLastState();
        currentUser = null;
        ALL_PLAYLISTS.clear();
        CURRENT_PLAYLIST_SONGS.clear();
        State.clearState();
    }

    public static User verifyEmail(String email) throws Exception {
        String sqlString = "select * from USER where EMAIL = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlString);
        preparedStatement.setString(1, email);
        ResultSet resultSet = preparedStatement.executeQuery();

        if(resultSet.next()) {
            currentUser = new User(resultSet.getString(1), resultSet.getLong(2));
            currentUser.setFirstName(resultSet.getString(3));
            currentUser.setLastName(resultSet.getString(4));
            currentUser.setJoinDate(resultSet.getDate(5));
            currentUser.setEmail(resultSet.getString(6));
            return currentUser;
        }
        return null;
    }
    public static boolean checkForUserName(String username) throws Exception {
        String sqlString = "select * from USER where USERNAME = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlString);
        preparedStatement.setString(1, username);
        ResultSet resultSet = preparedStatement.executeQuery();

        return resultSet.next();
    }
    public static void loadAllPlaylists() throws SQLException {
        ALL_PLAYLISTS.clear();
        String sqlString =
                """
                select t.playlist_id,t.name,t.username 
                from playlist t
                where t.username=?;
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sqlString);
        preparedStatement.setString(1, currentUser.getUsername());
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()) {
            Playlist playlist = new Playlist(resultSet.getInt(1),
                    resultSet.getString(2), resultSet.getString(3));
            ALL_PLAYLISTS.add(playlist);
        }
    }
    public static boolean checkForEmail(String mail, String username) throws Exception {
        String sqlString = "select * from USER where USERNAME = ? and EMAIL = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlString);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, mail);
        ResultSet resultSet = preparedStatement.executeQuery();

        return resultSet.next();
    }
    public static ArrayList<Playlist> getAllPlaylists()
    {
        return ALL_PLAYLISTS;
    }
    public static void loadPlaylistSongs(int playlistID) throws SQLException {
        CURRENT_PLAYLIST_SONGS.clear();
                String sqlString =
                """
                 select SONG_ID from songs_in_playlist where PLAYLIST_ID = ?;
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sqlString);
        preparedStatement.setInt(1, playlistID);
        ResultSet resultSet = preparedStatement.executeQuery();

        while(resultSet.next()) {
            int songID = resultSet.getInt(1);
            for (Song s : ALL_SONGS) {
                if (s.getID() == songID) {
                    System.out.println(s.getLength());
                    CURRENT_PLAYLIST_SONGS.add(s);
                }
            }
        }
    }
    public static ArrayList<Song> getCurrentPlaylistSongs(){
        return CURRENT_PLAYLIST_SONGS;
    }

    public static void saveLastState() throws SQLException {

        String username = getCurrentUser().getUsername();
        int songID = State.getLastSongID();
        double time = State.getLastTimeStamp();

        System.out.println(username + " " + songID + " " + time);

        String sqlString = "update last_state set song_id = ?, time = ? where username = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sqlString);
        preparedStatement.setInt(1, songID);
        preparedStatement.setDouble(2, time);
        preparedStatement.setString(3, username);
        preparedStatement.executeUpdate();
    }
    public static ArrayList<Object> getLastState() throws SQLException {
        ArrayList<Object> state = new ArrayList<>();
        state.add(-1);
        state.add(0.0);

        String sqlString = "select song_id, time from last_state where username = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sqlString);
        preparedStatement.setString(1, getCurrentUser().getUsername());
        ResultSet resultSet = preparedStatement.executeQuery();

        if(resultSet.next()) {
            int songID = resultSet.getInt(1);
            if(!resultSet.wasNull()) {
                state.set(0, songID);
                double time = resultSet.getDouble(2);
                if(!resultSet.wasNull()) state.set(1, time);
            }
        }
        return state;
    }
    public static void deletePlaylist(int playlistID) throws SQLException {
        String sqlString =
                """
                delete from playlist where playlist_id=?;
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sqlString);
        preparedStatement.setInt(1,playlistID);
        preparedStatement.executeUpdate();
        for(int i = 0; i< ALL_PLAYLISTS.size(); i++){
            if(ALL_PLAYLISTS.get(i).getID()==playlistID){
                ALL_PLAYLISTS.remove(i);
                break;
            }
        }
    }
    public static void addSongToPlaylist(int songID, int playlistID) throws SQLException {
        String sqlString =
                """
               insert into songs_in_playlist(song_id, playlist_id) values (?, ?);
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sqlString);
        preparedStatement.setInt(1,songID);
        preparedStatement.setInt(2,playlistID);
        preparedStatement.executeUpdate();
    }
    public static void deleteSongFromPlaylist(int songID, int playlistID) throws SQLException {
//        System.out.println(songID + " " + playlistID);
        String sqlString =
                """
               delete from songs_in_playlist where song_id=? and playlist_id=?;
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sqlString);
        preparedStatement.setInt(1, songID);
        preparedStatement.setInt(2, playlistID);
        preparedStatement.executeUpdate();
        for(int i = 0; i< CURRENT_PLAYLIST_SONGS.size(); i++){
            if(CURRENT_PLAYLIST_SONGS.get(i).getID() == songID){
                CURRENT_PLAYLIST_SONGS.remove(i);
                break;
            }
        }
    }
    public static Playlist createPlaylist(String pLName) throws SQLException {
        String sqlString =
                """
                insert into playlist(Name,Username) values (?,?);
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sqlString, PreparedStatement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1,pLName);
        preparedStatement.setString(2,currentUser.getUsername());
        preparedStatement.executeUpdate();

        ResultSet resultSet = preparedStatement.getGeneratedKeys();
        if(resultSet.next()) {
            int playlistID = resultSet.getInt(1);
            Playlist newPlaylist = new Playlist(playlistID, pLName, currentUser.getUsername());
            ALL_PLAYLISTS.add(newPlaylist);
            return newPlaylist;
        }
        return null;//if error will return null
    }
    public static void deleteUserPhoto() throws SQLException {
        String sqlString =
                """
                update user set PHOTO = null where USERNAME = ?;
                """;

        PreparedStatement preparedStatement = connection.prepareStatement(sqlString);
        preparedStatement.setString(1, Database.getCurrentUser().getUsername());
        preparedStatement.executeUpdate();
        getCurrentUser().setImage(null);
    }
}
