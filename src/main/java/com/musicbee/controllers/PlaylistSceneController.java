package com.musicbee.controllers;

import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import com.musicbee.entities.Song;
import com.musicbee.utility.Database;
import com.musicbee.utility.Jukebox;
import com.musicbee.utility.State;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class PlaylistSceneController implements Initializable {

    @FXML
    private ImageView profileIcon;

    @FXML
    private TableView<Song> table = new TableView<>();

    @FXML
    private TableColumn<Song, String> Title;

    @FXML
    private TableColumn<Song, String> Artist;

    @FXML
    private TableColumn<Song, String> Album;
    @FXML
    private TableColumn<Song,String> Length;

    @FXML
    private JFXHamburger myHamburger;
    @FXML
    private JFXDrawer drawer;

    @FXML
    private TextField searchBar;

    private ControlPanel controlPanel;

    @FXML
    private MenuButton menuButton;
    @FXML
    private VBox bottom;

    private final ArrayList<Song> allSongs = new ArrayList<>();
    private final ArrayList<Song> filteredSongs = new ArrayList<>();
    private final ContextMenu contextMenu = new ContextMenu();

    ObservableList <Song> tableList = FXCollections.observableArrayList();
    @FXML
    private Label name;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        name.setText(State.getCurrentPlaylistName());
        MenuItem item1=new MenuItem("Remove song from playlist");
        item1.setId("1");
        contextMenu.getItems().add(item1);
        menuButton.setText(Database.getCurrentUser().getUsername());
        if(Database.getCurrentUser().getImage() != null) {
            profileIcon.setImage(Database.getCurrentUser().getImage());
        }

        prepareTableview();
        loadSideBar();
        setHamburger();
        controlPanel = loadControlPanel();
    }

    private ControlPanel loadControlPanel() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/musicbee/musicbee/ControlPanel.fxml"));
        try {
            VBox vBox = fxmlLoader.load();
//            ControlPanel bottomController = fxmlLoader.getController();
            bottom.getChildren().clear();
            bottom.getChildren().addAll(vBox.getChildren());
        } catch (IOException e) {
            System.out.println(e);
        }

        return fxmlLoader.getController();
    }

    private void setHamburger() {
        HamburgerBasicCloseTransition transition= new HamburgerBasicCloseTransition(myHamburger);

        if(State.getBurgerState()==-1)
        {
            transition.setRate(-1);
            drawer.toggle();
        }
        else
        {
            transition.setRate(1);
            drawer.toggle();
        }
        transition.play();
        addHamburgerEventHandler(transition);
    }

    private void addHamburgerEventHandler(HamburgerBasicCloseTransition transition) {
        myHamburger.addEventHandler(MouseEvent.MOUSE_PRESSED,(e)->{
            transition.setRate(transition.getRate()*-1);
            if(transition.getRate()==-1)
            {
                State.setBurgerState(-1);
            }
            else {
                State.setBurgerState(1);
            }
            transition.play();
            if(drawer.isOpened() || drawer.isOpening())
            {
                drawer.close();
            }
            else drawer.open();
        });
    }

    private void loadSideBar() {
        try {
            VBox vbox= FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/musicbee/musicbee/Sidebar.fxml")));
            drawer.setSidePane(vbox);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void prepareTableview() {
        EventHandler<MouseEvent> onClick = this::clickItem;
        table.setRowFactory(param -> {
            TableRow<Song> row = new TableRow<>();
            row.setPrefHeight(40);
            row.setStyle("-fx-font-size: 15");
            row.setOnMouseClicked(onClick);
            return row;
        });

        Title.setCellValueFactory(new PropertyValueFactory<>("name"));
        Artist.setCellValueFactory(new PropertyValueFactory<>("artistName"));
        Album.setCellValueFactory(new PropertyValueFactory<>("albumName"));
        Length.setCellValueFactory(new PropertyValueFactory<>("lengthInString"));

        TableColumn<Song, Integer> indexColumn = new TableColumn<>("#");
        indexColumn.setSortable(false);
        indexColumn.setResizable(false);
        indexColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(
                table.getItems().indexOf(column.getValue()) + 1
        ));
        table.getColumns().add(0, indexColumn);

        table.setItems(tableList);
    }

    @FXML
    public void clickItem(MouseEvent event) {
        contextMenu.hide();
        TableRow<Song> row = (TableRow<Song>) event.getSource();
        if(event.getButton() == MouseButton.PRIMARY) {
            try {
                if(!row.isEmpty() && row.getItem()!=null) {
                    int index = row.getIndex();
                    State.setCurrentSongIndex(index);
                    Song song = State.getSongsInTable().get(index);

                    Jukebox.prepareJukebox(song);
                    Jukebox.play();

                    controlPanel.setPause();
//                    System.out.println(State.getVolume());
                    State.setCurrentSongName(State.getSongsInTable().get(State.getCurrentSongIndex()).getName());
                    State.setCurrentSongArtist(State.getSongsInTable().get(State.getCurrentSongIndex()).getArtistName());
                    updateNames(State.getCurrentSongName(),
                            State.getCurrentSongArtist());
                    Jukebox.getMediaPlayer().setVolume(State.getVolume());
                    State.setLastSongID(State.getSongsInTable().get(index).getID());
                }
                controlPanel.setTimeSlider();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        else if(event.getButton()==MouseButton.SECONDARY){
            contextMenu.show(table, event.getScreenX(), event.getScreenY());
            System.out.println(contextMenu.getItems().get(0).getId());
            contextMenu.setOnAction(e->{
                try {
                    Database.deleteSongFromPlaylist(row.getItem().getID(),State.getCurrentPlaylistID());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                makeObservableList(Database.getCurrentPlaylistSongs());
            });
        }
    }
    public void makeObservableList(ArrayList<Song> list) {
        allSongs.clear();
        filteredSongs.clear();
        tableList.clear();
        allSongs.addAll(list);
        filteredSongs.addAll(list);
        tableList.addAll(list);
        State.setSongsInTable(list);
    }

    @FXML
    private void onTypedSearchBar() {
        if(searchBar.getText().trim().isEmpty()) {
            tableList.clear();
            tableList.addAll(allSongs);
        }
        else {
            filteredSongs.clear();
            for(Song song : allSongs) {
                String substr = searchBar.getText().trim().toLowerCase();
                String name = song.getName().toLowerCase();
                if(name.contains(substr)) {
                    filteredSongs.add(song);
                }
            }
            tableList.clear();
            tableList.addAll(filteredSongs);
            State.setSongsInTable(filteredSongs);
        }
    }

    @FXML
    public void onClickProfile(ActionEvent event) throws IOException {
        MenuItem menuItem = (MenuItem) event.getSource();
        Stage myStage = (Stage) menuItem.getParentPopup().getOwnerWindow();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/musicbee/musicbee/Profile.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        String css = Objects.requireNonNull(getClass().getResource("/com/musicbee/musicbee/Stylesheet.css")).toExternalForm();
        scene.getStylesheets().add(css);
        myStage.setScene(scene);
        myStage.show();
    }
    public void onClickLogOut(ActionEvent event) throws IOException, SQLException {
        MediaPlayer player = Jukebox.getMediaPlayer();

        if(player != null) {
            Jukebox.dispose();
        }

        MenuItem menuItem = (MenuItem) event.getSource();
        Stage myStage = (Stage) menuItem.getParentPopup().getOwnerWindow();

        Database.logOutCurrentUser();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/musicbee/musicbee/SignIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        String css = Objects.requireNonNull(getClass().getResource("/com/musicbee/musicbee/Stylesheet.css")).toExternalForm();
        scene.getStylesheets().add(css);
        myStage.setScene(scene);

        myStage.show();
    }
    private void updateNames(String songName, String artist) {
        controlPanel.getSongName().setText(songName);
        controlPanel.getArtistName().setText(artist);
    }
}