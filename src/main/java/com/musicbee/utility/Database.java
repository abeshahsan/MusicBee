package com.musicbee.utility;

import com.musicbee.entities.Playlist;
import com.musicbee.entities.Song;
import com.musicbee.entities.User;
import javafx.scene.image.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;

/**
 * This class handles the connection and operations with the database.
 * <p>All the data provided by the users and produced by the program will be stored and retrieved from the database
 * 'PHOENIX'. This class will facilitate all the functionalities to interact with the database.</p>
 */
public class Database {
    /**
     * The connection object used to connect to the database.
     */
    private static Connection connection;

    /**
     * The username and password used to connect to the database.
     */
    private static final String DB_USERNAME, DB_PASSWORD;

    /**
     * The currently logged-in user.
     * <p>When a user logs in, his information will be stored in this object.
     * @see com.musicbee.entities.User
     */
    private static User currentUser;

    /**
     * The list of all songs in the database.
     */
    private static final ArrayList<Song> ALL_SONGS;

    /**
     * The list of all playlists in the database created by the currently logged-in user.
     */
    private static final ArrayList<Playlist> ALL_PLAYLISTS;

    /**
     * The list of songs in the currently selected playlist.
     */
    private static final ArrayList<Song> CURRENT_PLAYLIST_SONGS;

    // Initialize the static members.
    static {
        DB_USERNAME = "root";
        DB_PASSWORD = "dbms";

        ALL_SONGS = new ArrayList<>();
        ALL_PLAYLISTS = new ArrayList<>();
        CURRENT_PLAYLIST_SONGS = new ArrayList<>();
    }

    /**
     * Connects to the database.
     * @throws SQLException if there is an error connecting to the database
     */
    public static void prepareDatabase() throws SQLException {
        connection = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/phoenix", DB_USERNAME, DB_PASSWORD);
        currentUser = null;
    }

    /**
     * Logs in a user with the given username and password.
     * @param username the username of the user
     * @param password the password of the user
     * @return the {@link User} object of the logged-in user, or null if the login fails
     * @throws SQLException if there is an error retrieving the user information from the database
     */
    public static User signIn(String username, long password) throws SQLException {
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
    /**
     * Registers a new user in the database.
     * @param user the {@link User} object of the new user
     * @throws Exception if there is an error inserting the user information into the database
     */
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
    /**
     * Updates the information of the currently logged-in user in the database.
     * @param user the {@link User} object with the updated information
     * @throws Exception if there is an error updating the user information in the database
     */
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

    /**
     * Updates the photo of the currently logged-in user in the database.
     * @param file the new photo file
     * @throws Exception if there is an error updating the user photo in the database
     */
    public static void updateUserPhoto(File file) throws Exception {
        String sqlString = "update user set PHOTO = ? where USERNAME = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlString);
        preparedStatement.setString(2, getCurrentUser().getUsername());

        InputStream photo = new FileInputStream(file);
        preparedStatement.setBlob(1, photo);

        preparedStatement.executeUpdate();

        getCurrentUser().setImage(new Image(new FileInputStream(file)));
    }

    /**
     * Retrieves the User object of the currently logged-in user.
     * @return the User object of the currently logged-in user
     */
    public static User getCurrentUser()
    {
        return  currentUser;
    }

    /**
     * Loads all songs from the database into the ALL_SONGS list.
     * @throws SQLException if there is an error retrieving the songs from the database
     */
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
            Tools.getLengthOfSong(song);
        }
    }

    /**
     * Retrieves the list of all songs stored in the {@code ALL_SONGS} arraylist.
     * @return the {@code ALL_SONGS} arraylist.
     */
    public static ArrayList<Song> getAllSongs() {
       return ALL_SONGS;
    }

    /**
     * Logs-out the current user. Clears the {@code currentUser} object.
     */
    public static void logOutCurrentUser() {
        currentUser = null;
        ALL_PLAYLISTS.clear();
        CURRENT_PLAYLIST_SONGS.clear();
        Jukebox.clearState();
    }

    /**
     * If a user forgets is password, he goes for retrieving the password.
     * <p>So he will need to enter his email.</p>
     * <p>This function will verify that the email exists or not.</p>
     * @param email The email address the user provided
     * @return The {@link User} object having the email
     * @throws Exception If any error occurs while retrieving the email from the database
     */
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

    /**
     * Checks if a user already exists with this username or not.
     * @param username The {@code username} the user provided
     * @return true if the username is taken, otherwise false
     * @throws Exception  If any error occurs while retrieving the email from the database
     */
    public static boolean checkForUserName(String username) throws Exception {
        String sqlString = "select * from USER where USERNAME = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlString);
        preparedStatement.setString(1, username);
        ResultSet resultSet = preparedStatement.executeQuery();

        return resultSet.next();
    }
    /**
     * Checks if a user already exists with this email or not.
     * @param mail The {@code email} the user provided
     * @param username The {@code username} the user provided
     * @return true if the email is taken, otherwise false
     * @throws Exception  If any error occurs while retrieving the email from the database
     */
    public static boolean checkForEmail(String mail, String username) throws Exception {
        String sqlString = "select * from USER where USERNAME = ? and EMAIL = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlString);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, mail);
        ResultSet resultSet = preparedStatement.executeQuery();

        return resultSet.next();
    }

    /**
     * Loads all playlists from the database into the ALL_PLAYLISTS list.
     * @throws SQLException If any error occurs while retrieving the songs from the database
     */
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
    /**
     * Retrieves the arraylist having the all the playlists.
     * @return the {@code ALL_PLAYLISTS} having all the playlists
     */
    public static ArrayList<Playlist> getAllPlaylists()
    {
        return ALL_PLAYLISTS;
    }

    /**
     * Loads all songs that belong to a playlist.
     * @param playlistID The ID of the playlist that the user wants to see
     * @throws SQLException If any error occurs while retrieving the songs from the database
     */
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

    /**
     * Retrieves the arraylist having the all songs of the current playlist.
     * @return the {@code ALL_PLAYLISTS} having all the songs of the current playlist
     */
    public static ArrayList<Song> getCurrentPlaylistSongs(){
        return CURRENT_PLAYLIST_SONGS;
    }

    /**
     * Saves the last playback position of the current user into the database
     * @throws SQLException If any error occurs while saving the playback position to the database.
     */
    public static void savePlaybackPosition() throws SQLException {

        String username = getCurrentUser().getUsername();
        int songID = Jukebox.getNowPlaying().getID();
        double time = Jukebox.getPlaybackPos();

        System.out.println(username + " " + songID + " " + time);

        String sqlString = "update last_state set song_id = ?, time = ? where username = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sqlString);
        preparedStatement.setInt(1, songID);
        preparedStatement.setDouble(2, time);
        preparedStatement.setString(3, username);
        preparedStatement.executeUpdate();
    }

    /**
     * Loads the last playback position of the current user from the database
     * @throws SQLException If any error occurs while loading the playback position from the database
     */
    public static ArrayList<Object> loadPlaybackPosition() throws SQLException {
        ArrayList<Object> playback = new ArrayList<>();
        playback.add(-1);
        playback.add(0.0);

        String sqlString = "select song_id, time from last_state where username = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sqlString);
        preparedStatement.setString(1, getCurrentUser().getUsername());
        ResultSet resultSet = preparedStatement.executeQuery();

        if(resultSet.next()) {
            int songID = resultSet.getInt(1);
            if(!resultSet.wasNull()) {
                playback.set(0, songID);
                double time = resultSet.getDouble(2);
                if(!resultSet.wasNull()) playback.set(1, time);
            }
        }
        return playback;
    }

    /**
     * Deletes a playlist from the database. It deletes a row from the Playlist table in the database having the given ID.
     * @param playlistID the ID of playlist that is to be deleted
     * @throws SQLException If any error occurs while deleting the playlist from the database
     */
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

    /**
     * Adds a song to a playlist.
     * <p>Inserts the song ID and playlist ID into the songs_in_playlist table</p>
     * @param songID The ID of the song the user wants to add to the playlist
     * @param playlistID the ID of the target playlist
     * @throws SQLException If any error occurs while inserting the data into the database
     */
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

    /**
     * Creates a new playlist.
     * <p>Inserts a new row in the 'Playlist' table.</p>
     * @param playlistName The name of the new playlist
     * @return the {@link Playlist} object of the newly created playlist
     * @throws SQLException If any error occurs while inserting the data into the database
     */
    public static Playlist createPlaylist(String playlistName) throws SQLException {
        String sqlString =
                """
                insert into playlist(Name,Username) values (?,?);
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sqlString, PreparedStatement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1,playlistName);
        preparedStatement.setString(2,currentUser.getUsername());
        preparedStatement.executeUpdate();

        ResultSet resultSet = preparedStatement.getGeneratedKeys();
        if(resultSet.next()) {
            int playlistID = resultSet.getInt(1);
            Playlist newPlaylist = new Playlist(playlistID, playlistName, currentUser.getUsername());
            ALL_PLAYLISTS.add(newPlaylist);
            return newPlaylist;
        }
        return null;//if error will return null
    }

    /**
     * Deletes the profile picture of the current user.
     * <p>Removes the PHOTO from the row of the 'USER' table,
     * having the username of the current user (sets null in that column).</p>
     * @throws SQLException If any error occurs while deleting the data from the database
     */
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
