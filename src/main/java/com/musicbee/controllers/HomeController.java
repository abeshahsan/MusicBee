package com.musicbee.controllers;

import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import com.musicbee.entities.Playlist;
import com.musicbee.entities.Song;
import com.musicbee.utility.Database;
import com.musicbee.utility.FilePaths;
import com.musicbee.utility.MediaPlayerControl;
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

public class HomeController implements Initializable {
    @FXML
    private TableView<Song> table;
    @FXML
    private TableColumn<Song, String> Title;
    @FXML
    private TableColumn<Song, String> Artist;
    @FXML
    private TableColumn<Song, String> Album;
    @FXML
    private TableColumn<Song, String> Length;
    @FXML
    private JFXHamburger myHamburger;
    @FXML
    private JFXDrawer drawer;

    @FXML
    private TextField searchBar;

    @FXML
    private MenuButton menuButton;
    ContextMenu contextMenu = new ContextMenu();
    Menu child = new Menu("Add song to playlist");

    @FXML
    private VBox bottom;

    @FXML
    private ImageView profileIcon;

    private ControlPanel controlPanel;

    private final ArrayList<Song> allSongs = new ArrayList<>();
    private final ArrayList<Song> filteredSongs = new ArrayList<>();

    ObservableList <Song> tableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        contextMenu.getItems().clear();
        contextMenu.getItems().add(child);
        loadContextMenuPlaylists();

        menuButton.setText(Database.getCurrentUser().getUsername());
        if(Database.getCurrentUser().getImage() != null ) {
            profileIcon.setImage(Database.getCurrentUser().getImage());
        }

        prepareTableview();
        loadSideBar();
        setHamburger();
        controlPanel = loadControlPanel();
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

        table.getFocusModel().focus(1);
    }

    private ControlPanel loadControlPanel() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FilePaths.CONTROL_PANEL));
        try {
            VBox vBox = fxmlLoader.load();
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
            drawer.close();
        }
        else
        {
            transition.setRate(1);
            drawer.open();
        }
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
        try
        {
            VBox vbox= FXMLLoader.load(Objects.requireNonNull(getClass().getResource(FilePaths.SIDE_BAR)));
            drawer.setSidePane(vbox);
        } catch (Exception e) {
            System.out.println(e);;
        }
    }

    @FXML
    private void clickItem(MouseEvent event)
    {
        contextMenu.hide();
        TableRow<Song> row = (TableRow<Song>) event.getSource();
        if(event.getButton() == MouseButton.PRIMARY) {
            try {
                if(!row.isEmpty() && row.getItem() != null) {
                    int index = row.getIndex();
                    State.setCurrentSongIndex(index);
                    Song song = State.getSongsInTable().get(index);

                    MediaPlayerControl.prepareJukebox(song);
                    MediaPlayerControl.play();

                    controlPanel.setPause();
                    State.setCurrentSongName(State.getSongsInTable().get(State.getCurrentSongIndex()).getName());
                    State.setCurrentSongArtist(State.getSongsInTable().get(State.getCurrentSongIndex()).getArtistName());
                    updateNames(State.getCurrentSongName(),
                            State.getCurrentSongArtist());
                    MediaPlayerControl.getMediaPlayer().setVolume(State.getVolume());
                    State.setLastSongID(State.getSongsInTable().get(index).getID());
                }

                controlPanel.setTimeSlider();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        else if(event.getButton()==MouseButton.SECONDARY){
            contextMenu.show(table, event.getScreenX(), event.getScreenY());
            child.getItems().forEach(menuItem ->{
                menuItem.setOnAction(e->{
                    try {
                        Database.addSongToPlaylist(row.getItem().getID(), Integer.parseInt(menuItem.getId()));
                        } catch (SQLException ex) {
                            System.out.println(ex);
                        }
                    }
                );
            });
        }
    }
    private void loadContextMenuPlaylists(){
        ArrayList<Playlist> a=Database.getAllPlaylists();
        for(int i=0;i<a.size();i++){
            MenuItem item=new MenuItem(a.get(i).getName());
            child.getItems().add(item);
            child.getItems().get(i).setId(String.valueOf(a.get(i).getID()));
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
    private void onClickProfile(ActionEvent event) throws IOException {
        MenuItem menuItem = (MenuItem) event.getSource();
        Stage myStage = (Stage) menuItem.getParentPopup().getOwnerWindow();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FilePaths.PROFILE));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        String css = Objects.requireNonNull(getClass().getResource(FilePaths.STYLESHEET)).toExternalForm();
        scene.getStylesheets().add(css);
        myStage.setScene(scene);
        myStage.show();
    }

    @FXML
    private void onClickLogOut(ActionEvent event) throws IOException, SQLException {
        MediaPlayer player = MediaPlayerControl.getMediaPlayer();

        if(player != null) {
            MediaPlayerControl.dispose();
        }

        MenuItem menuItem = (MenuItem) event.getSource();
        Stage myStage = (Stage) menuItem.getParentPopup().getOwnerWindow();

        Database.savePlaybackPosition();
        Database.logOutCurrentUser();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FilePaths.SIGN_IN));
        Scene scene = new Scene(fxmlLoader.load());
        String css = Objects.requireNonNull(getClass().getResource(FilePaths.STYLESHEET)).toExternalForm();
        scene.getStylesheets().add(css);
        myStage.setScene(scene);
        myStage.show();
    }
    private void updateNames(String name, String artist) {
        controlPanel.getSongName().setText(name);
        controlPanel.getArtistName().setText(artist);
    }
}
