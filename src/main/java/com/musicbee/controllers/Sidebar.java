package com.musicbee.controllers;
import com.musicbee.entities.Playlist;
import com.musicbee.entities.Song;
import com.musicbee.utility.Database;
import com.musicbee.utility.FilePaths;
import com.musicbee.utility.State;
import com.musicbee.utility.Tools;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class Sidebar implements Initializable {

    @FXML
    private ImageView pfpSide;

    @FXML
    private Label usernameS;
    @FXML
    ListView<Playlist> list = new ListView<>();
    ArrayList<Playlist> playlists=new ArrayList<>();
    ObservableList<Playlist> tableList = FXCollections.observableArrayList();
    ContextMenu contextMenu=new ContextMenu();
    @FXML
    TextField create=new TextField();

    public void initialize(URL url, ResourceBundle resourceBundle) {
        MenuItem item1=new MenuItem("Delete");
        contextMenu.getItems().add(item1);
        EventHandler<MouseEvent> onClick = this::handle;
        create.setVisible(false);
        playlists= Database.getAllPlaylists();
        tableList.addAll(playlists);
        list.setItems(tableList);
        list.setCellFactory((ListView<Playlist> lv) ->{
                ListCell <Playlist> cell = new ListCell<>() {
                    @Override
                    public void updateItem(Playlist playlist, boolean empty) {
                        super.updateItem(playlist, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(playlist.getName());
                        }
                    }
                };
                 cell.setOnMouseClicked(onClick);

                return cell;
         });
        ImageView defaultImage = new ImageView(pfpSide.getImage());

        if(Database.getCurrentUser().getImage() != null) {
            pfpSide.setImage(Database.getCurrentUser().getImage());
        }
        usernameS.setText(Database.getCurrentUser().getUsername());

        Tools.clipImageview(pfpSide, 125);
    }
    public void handle(MouseEvent event) {
        @SuppressWarnings("unchecked") ListCell<Playlist> lc=(ListCell<Playlist>)event.getSource();
        if (lc.getText()==null)return;
        if(event.getButton()== MouseButton.PRIMARY) {
            Node calling = (Node) event.getSource();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FilePaths.PLAYLIST_SCENE));
            State.setCurrentPlaylistID(lc.getItem().getID());
            State.setCurrentPlaylistName(lc.getItem().getName());
            Stage myStage = (Stage) calling.getScene().getWindow();
            try {
                Scene scene = new Scene(loader.load());
                myStage.setScene(scene);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.out.println(getClass().getName() + ": " + getClass().getEnclosingMethod());
                throw new RuntimeException(e);
            }
            try {
                //Parent root = loader.load();
                Database.loadPlaylistSongs(lc.getItem().getID());
                PlaylistSceneController ps = loader.getController();
                ps.makeObservableList(Database.getCurrentPlaylistSongs());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            myStage.show();
        }
        else if(event.getButton()==MouseButton.SECONDARY){
            contextMenu.show(list, event.getScreenX(), event.getScreenY());
            contextMenu.setOnAction(e->{
                try {
                    Database.deletePlaylist(lc.getItem().getID());
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                    System.out.println(getClass().getName() + ": " + getClass().getEnclosingMethod());
                }
                this.deleteFromList(lc.getItem());
            }
            );
        }
    }
    @FXML
    protected void OnClickCreatePlaylist(ActionEvent event)
    {
        create.setVisible(!create.isVisible());
    }
    @FXML
    protected void OnEnteringPlaylistName() throws SQLException {
        create.setVisible(false);
        String pLName=create.getText();
        if(!pLName.isEmpty()) {
            Playlist pL = Database.createPlaylist(pLName);
            this.addToList(pL);
        }
    }
    public void addToList(Playlist pL)
    {
        try {
            tableList.add(pL);
            list.setItems(tableList);
        }catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(getClass().getName() + ": " + getClass().getEnclosingMethod());
        }
    }
    public void deleteFromList(Playlist pL)
    {
        tableList.clear();
        tableList.addAll(Database.getAllPlaylists());
        list.setItems(tableList);
    }

    public void OnClickHome(ActionEvent event) {
        Node callingBtn=(Node)event.getSource();
        Stage myStage=(Stage)callingBtn.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource(FilePaths.HOME));

        try {

            ArrayList<Song> songs = Database.getAllSongs();

            Parent root = loader.load();

            HomeController bl = loader.getController();
            bl.makeObservableList(songs);

            String css = Objects.requireNonNull(getClass().getResource(FilePaths.STYLESHEET)).toExternalForm();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(css);
            myStage.setScene(scene);
            myStage.show();
        }catch (Exception e) {
            //e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}